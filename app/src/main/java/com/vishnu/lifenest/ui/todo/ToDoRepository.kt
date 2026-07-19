package com.vishnu.lifenest.ui.todo

import androidx.lifecycle.LiveData
import com.vishnu.lifenest.data.ToDoDao
import com.vishnu.lifenest.data.ToDoEntity

class ToDoRepository(private val dao: ToDoDao) {
    fun getAll(): LiveData<List<ToDoEntity>> = dao.getAll()
    suspend fun insert(item: ToDoEntity) = dao.insert(item)
    suspend fun update(item: ToDoEntity) = dao.update(item)
    suspend fun delete(item: ToDoEntity) = dao.delete(item)
}
