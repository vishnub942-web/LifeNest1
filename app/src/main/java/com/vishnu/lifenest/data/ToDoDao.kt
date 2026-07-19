package com.vishnu.lifenest.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ToDoDao {
    @Query("SELECT * FROM todos ORDER BY id DESC")
    fun getAll(): LiveData<List<ToDoEntity>>

    @Insert
    suspend fun insert(item: ToDoEntity): Long

    @Update
    suspend fun update(item: ToDoEntity)

    @Delete
    suspend fun delete(item: ToDoEntity)
}
