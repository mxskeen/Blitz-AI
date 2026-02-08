package com.krishna.blitzai.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.krishna.blitzai.database.dao.ChatsDao
import com.krishna.blitzai.database.dao.GeneratedAudiosDao
import com.krishna.blitzai.database.dao.GeneratedImagesDao
import com.krishna.blitzai.database.dao.MemoriesDao
import com.krishna.blitzai.database.dao.MessagesDao
import com.krishna.blitzai.database.entity.Chat
import com.krishna.blitzai.database.entity.ChatMessage
import com.krishna.blitzai.database.entity.GeneratedAudio
import com.krishna.blitzai.database.entity.GeneratedImage
import com.krishna.blitzai.database.entity.Memory

@Database(entities = [Chat::class, ChatMessage::class, GeneratedImage::class, GeneratedAudio::class, Memory::class], version = 5)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        val MIGRATION_1_2 = Migration(1, 2) {
            it.execSQL("ALTER TABLE ChatMessage ADD COLUMN generating INTEGER NOT NULL DEFAULT 0")
        }
        val MIGRATION_2_3 = Migration(2, 3) {
            it.execSQL("CREATE TABLE GeneratedImage(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, prompt TEXT, url TEXT)")
        }
        val MIGRATION_3_4 = Migration(3, 4) {
            it.execSQL("CREATE TABLE GeneratedAudio(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, input TEXT NOT NULL, file_path TEXT NOT NULL, file_mime_type TEXT NOT NULL)")
        }
        val MIGRATION_4_5 = Migration(4, 5) {
            it.execSQL("CREATE TABLE Memory(id INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL, content TEXT NOT NULL, chat_id INTEGER, created_time INTEGER NOT NULL DEFAULT 0, updated_time INTEGER NOT NULL DEFAULT 0)")
        }
    }
    abstract fun chatsDao(): ChatsDao
    abstract fun messagesDao(): MessagesDao
    abstract fun imagesDao(): GeneratedImagesDao
    abstract fun audiosDao(): GeneratedAudiosDao
    abstract fun memoriesDao(): MemoriesDao
}