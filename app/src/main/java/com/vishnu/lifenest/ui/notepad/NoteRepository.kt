package com.vishnu.lifenest.ui.notepad

import androidx.lifecycle.LiveData
import com.vishnu.lifenest.data.NoteDao
import com.vishnu.lifenest.data.NoteEntity

class NoteRepository(private val dao: NoteDao) {
    fun getAll(): LiveData<List<NoteEntity>> = dao.getAll()
    fun search(query: String): LiveData<List<NoteEntity>> = dao.search(query)
    suspend fun insert(note: NoteEntity) = dao.insert(note)
    suspend fun update(note: NoteEntity) = dao.update(note)
    suspend fun delete(note: NoteEntity) = dao.delete(note)
    suspend fun getById(id: Long) = dao.getById(id)
}
