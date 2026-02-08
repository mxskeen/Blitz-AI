package com.krishna.blitzai.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.krishna.blitzai.database.entity.Memory
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoriesDao {

    @Query("SELECT * FROM Memory ORDER BY updated_time DESC")
    fun getAll(): Flow<List<Memory>>

    @Query("SELECT * FROM Memory ORDER BY updated_time DESC LIMIT :limit")
    suspend fun getLatest(limit: Int): List<Memory>

    @Query("SELECT * FROM Memory WHERE content LIKE '%' || :query || '%' ORDER BY updated_time DESC LIMIT :limit")
    suspend fun search(query: String, limit: Int = 5): List<Memory>

    @Insert
    suspend fun insert(memory: Memory): Long

    @Query("UPDATE Memory SET content = :content, updated_time = :updatedTime WHERE id = :id")
    suspend fun updateContent(id: Long, content: String, updatedTime: Long = System.currentTimeMillis())

    @Delete
    suspend fun delete(memory: Memory)

    @Query("DELETE FROM Memory")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM Memory")
    suspend fun count(): Int
}
