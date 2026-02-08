package com.krishna.blitzai.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.krishna.blitzai.BlitzAI
import com.krishna.blitzai.database.AppDatabase
import com.krishna.blitzai.database.entity.ChatMessage
import com.krishna.blitzai.database.entity.ChatWithMessages
import com.krishna.blitzai.di.viewmodel.ChatId
import com.krishna.blitzai.repository.local.MemoryRepository
import com.krishna.blitzai.repository.network.OpenAIRepository
import com.krishna.blitzai.store.datastore.SettingsDataStore
import com.krishna.blitzai.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ChatId val chatId: Long,
    private val database: AppDatabase,
    private val openAIRepository: OpenAIRepository,
    private val memoryRepository: MemoryRepository,
    private val settingsDataStore: SettingsDataStore,
    application: Application
): BaseViewModel(application) {

    val chatWithMessages = database.chatsDao().getById(chatId)
        .map { it.copy(messages = it.messages.asReversed()) }
        .flowOn(Dispatchers.IO)

    var text by mutableStateOf("")
    var addingMyMessage by mutableStateOf(false)

    fun send(chatWithMessages: ChatWithMessages) = BlitzAI.applicationScope.onIO({
        if (database.messagesDao().countGeneratingMessagesInChat(chatId) > 0) return@onIO

        val messageText = text.trim()
        if (messageText.isEmpty()) return@onIO

        // Check if memory is enabled
        val memoryEnabled = settingsDataStore.memoryEnabled.first()
        
        // Get relevant memories if enabled
        val relevantMemories = if (memoryEnabled) {
            memoryRepository.getRelevantMemories(messageText, limit = 3)
        } else {
            emptyList()
        }

        val userMessage = ChatMessage(
            content = messageText,
            role = "user",
            chatId = chatId
        )

        withContext(Dispatchers.Main) {
            addingMyMessage = true
            text = ""
        }

        chatWithMessages.chat.also { chat ->
            if (chat.title == null) {
                database.chatsDao().insert(chat.copy(title = userMessage.content))
            }
        }

        val responseMessage = ChatMessage(
            role = "assistant",
            generating = true,
            chatId = chatId
        )
        val responseMessageId = database.messagesDao().insert(userMessage, responseMessage).last()

        val responseMessages = mutableMapOf(0 to responseMessage.copy(id = responseMessageId))
        openAIRepository.generateMessagesStream(
            chatWithMessages.messages.asReversed() + userMessage,
            memories = relevantMemories
        ).collect { (index, content) ->
            responseMessages.getOrPut(index) {
                responseMessage.copy(id = 0)
            }.copy(
                content = content,
                time = System.currentTimeMillis()
            ).also { message ->
                database.messagesDao().insert(message).first().also { newId ->
                    if (message.id == 0L)
                        responseMessages[index] = message.copy(id = newId)
                    else
                        responseMessages[index] = message
                }
            }
        }

        val finalMessages = responseMessages.values.map {
            it.copy(
                generating = false,
                content = it.content?.trim()
            )
        }

        database.messagesDao().insert(finalMessages)

        // Generate memory from conversation if enabled
        if (memoryEnabled) {
            val allMessages = chatWithMessages.messages.asReversed() + userMessage + finalMessages
            saveConversationMemory(allMessages, chatId)
        }
    }, errorBlock = {
        database.messagesDao().apply {
            markAllAsNotGeneratingInChat(chatId)
            deleteEmptyMessagesInChat(chatId)
        }
    })

    fun delete(message: ChatMessage) = viewModelScope.onIO {
        database.messagesDao().delete(message)
    }

    private fun saveConversationMemory(messages: List<ChatMessage>, chatId: Long) {
        BlitzAI.applicationScope.launch(Dispatchers.IO) {
            try {
                val summary = memoryRepository.generateConversationSummary(messages)
                if (summary.isNotEmpty()) {
                    memoryRepository.saveMemory(summary, chatId)
                }
            } catch (_: Exception) {
            }
        }
    }
}