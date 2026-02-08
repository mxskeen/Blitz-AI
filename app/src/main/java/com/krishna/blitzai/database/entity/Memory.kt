package com.krishna.blitzai.database.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class Memory(
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "chat_id") val chatId: Long? = null,
    @ColumnInfo(name = "created_time") val createdTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_time") val updatedTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0
)
