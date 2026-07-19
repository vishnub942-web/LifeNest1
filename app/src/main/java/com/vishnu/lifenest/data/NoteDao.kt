package com.vishnu.lifenest.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY modifiedAt DESC, id DESC")
    fun getAll(): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE heading LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY modifiedAt DESC")
    fun search(query: String): LiveData<List<NoteEntity>>

    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): NoteEntity?
}
