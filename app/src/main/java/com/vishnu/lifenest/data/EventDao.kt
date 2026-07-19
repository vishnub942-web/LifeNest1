package com.vishnu.lifenest.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY id DESC")
    fun getAll(): LiveData<List<EventEntity>>

    @Insert
    suspend fun insert(event: EventEntity): Long

    @Delete
    suspend fun delete(event: EventEntity)
}
