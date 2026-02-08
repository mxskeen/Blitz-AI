package com.krishna.blitzai.repository.local

import com.krishna.blitzai.database.dao.MemoriesDao
import com.krishna.blitzai.database.entity.ChatMessage
import com.krishna.blitzai.database.entity.Memory
import com.krishna.blitzai.repository.base.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepository @Inject constructor(
    private val memoriesDao: MemoriesDao
) : BaseRepository() {

    val allMemories: Flow<List<Memory>> = memoriesDao.getAll()

    suspend fun getLatestMemories(limit: Int = 10): List<Memory> = withContext(Dispatchers.IO) {
        memoriesDao.getLatest(limit)
    }

    suspend fun searchMemories(query: String, limit: Int = 5): List<Memory> = withContext(Dispatchers.IO) {
        memoriesDao.search(query, limit)
    }

    suspend fun saveMemory(content: String, chatId: Long? = null): Long = withContext(Dispatchers.IO) {
        memoriesDao.insert(Memory(content = content, chatId = chatId))
    }

    suspend fun deleteMemory(memory: Memory) = withContext(Dispatchers.IO) {
        memoriesDao.delete(memory)
    }

    suspend fun deleteAllMemories() = withContext(Dispatchers.IO) {
        memoriesDao.deleteAll()
    }

    /**
     * Generates a summary memory from conversation messages.
     * This creates a concise summary of what was discussed.
     */
    fun generateConversationSummary(messages: List<ChatMessage>): String {
        if (messages.isEmpty()) return ""

        val userMessages = messages.filter { it.role == "user" }.mapNotNull { it.content }
        val assistantMessages = messages.filter { it.role == "assistant" }.mapNotNull { it.content }

        if (userMessages.isEmpty()) return ""

        val topics = extractTopics(userMessages + assistantMessages)
        val summary = buildString {
            append("Conversation about: ")
            append(topics.joinToString(", "))
            if (userMessages.isNotEmpty()) {
                append(". User asked about: ")
                append(userMessages.last().take(100))
                if (userMessages.last().length > 100) append("...")
            }
        }

        return summary
    }

    /**
     * Extract relevant memories based on the current message context.
     * Combines latest memories and search-based relevance.
     */
    suspend fun getRelevantMemories(currentMessage: String, limit: Int = 3): List<Memory> {
        val searchResults = searchMemories(currentMessage, limit)
        val latest = getLatestMemories(limit)

        // Combine and deduplicate, prioritizing search results
        return (searchResults + latest).distinctBy { it.id }.take(limit)
    }

    private fun extractTopics(texts: List<String>): List<String> {
        // Simple keyword extraction - can be enhanced with NLP
        val commonWords = setOf(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
            "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
            "this", "but", "his", "by", "from", "they", "we", "say", "her",
            "she", "or", "an", "will", "my", "one", "all", "would", "there",
            "their", "what", "so", "up", "out", "if", "about", "who", "get",
            "which", "go", "me", "when", "make", "can", "like", "time", "no",
            "just", "him", "know", "take", "people", "into", "year", "your",
            "good", "some", "could", "them", "see", "other", "than", "then",
            "now", "look", "only", "come", "its", "over", "think", "also",
            "back", "after", "use", "two", "how", "our", "work", "first",
            "well", "way", "even", "new", "want", "because", "any", "these",
            "give", "day", "most", "us", "is", "was", "are", "were", "been",
            "has", "had", "did", "does", "doing", "done"
        )

        val wordFreq = mutableMapOf<String, Int>()
        texts.forEach { text ->
            text.lowercase()
                .replace(Regex("[^a-zA-Z\\s]"), "")
                .split(Regex("\\s+"))
                .filter { it.length > 3 && it !in commonWords }
                .forEach { word ->
                    wordFreq[word] = wordFreq.getOrDefault(word, 0) + 1
                }
        }

        return wordFreq.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key }
    }
}
