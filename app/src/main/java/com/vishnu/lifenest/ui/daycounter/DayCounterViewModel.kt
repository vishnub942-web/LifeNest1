package com.vishnu.lifenest.ui.daycounter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.vishnu.lifenest.data.AppDatabase
import com.vishnu.lifenest.data.EventEntity
import com.vishnu.lifenest.data.EventType
import kotlinx.coroutines.launch

class DayCounterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EventRepository(AppDatabase.getInstance(application).eventDao())
    val events: LiveData<List<EventEntity>> = repository.getAll()

    fun addYearlyEvent(title: String, fromDate: String) {
        if (title.isBlank() || fromDate.isBlank()) return
        viewModelScope.launch {
            repository.insert(EventEntity(title = title.trim(), type = EventType.YEARLY, fromDate = fromDate))
        }
    }

    fun addRangeEvent(title: String, fromDate: String, toDate: String?) {
        if (title.isBlank() || fromDate.isBlank()) return
        viewModelScope.launch {
            repository.insert(EventEntity(title = title.trim(), type = EventType.RANGE, fromDate = fromDate, toDate = toDate))
        }
    }

    fun delete(event: EventEntity) {
        viewModelScope.launch { repository.delete(event) }
    }
}
