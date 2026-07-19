package com.vishnu.lifenest.ui.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.vishnu.lifenest.data.AppDatabase
import com.vishnu.lifenest.data.ToDoEntity
import com.vishnu.lifenest.data.ToDoStatus
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ToDoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ToDoRepository(AppDatabase.getInstance(application).toDoDao())
    private val timeFormat = SimpleDateFormat("hh:mm a, d MMMM yyyy", Locale.US)

    val items: LiveData<List<ToDoEntity>> = repository.getAll()

    fun addItem(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.insert(ToDoEntity(text = text.trim(), addedAt = timeFormat.format(Date())))
        }
    }

    fun cycleStatus(item: ToDoEntity) {
        viewModelScope.launch {
            val next = ToDoStatus.next(item.status)
            repository.update(
                item.copy(
                    status = next,
                    markedAt = if (next == ToDoStatus.PENDING) null else timeFormat.format(Date())
                )
            )
        }
    }

    fun delete(item: ToDoEntity) {
        viewModelScope.launch { repository.delete(item) }
    }
}
