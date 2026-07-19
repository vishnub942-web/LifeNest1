package com.vishnu.lifenest.ui.daycounter

import androidx.lifecycle.LiveData
import com.vishnu.lifenest.data.EventDao
import com.vishnu.lifenest.data.EventEntity

class EventRepository(private val dao: EventDao) {
    fun getAll(): LiveData<List<EventEntity>> = dao.getAll()
    suspend fun insert(event: EventEntity) = dao.insert(event)
    suspend fun delete(event: EventEntity) = dao.delete(event)
}
