package com.vishnu.lifenest.ui.dailytask

import android.app.Application
import androidx.lifecycle.*
import com.vishnu.lifenest.data.AppDatabase
import com.vishnu.lifenest.data.TaskEntryEntity
import com.vishnu.lifenest.data.TaskRepository
import com.vishnu.lifenest.data.TaskWithEntry
import com.vishnu.lifenest.util.TimeUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DailyTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val _selectedDate = MutableLiveData(dateFormat.format(Date()))
    val selectedDate: LiveData<String> = _selectedDate

    val tasksForDate: LiveData<List<TaskWithEntry>> = _selectedDate.switchMap { date ->
        repository.getTasksForDate(date)
    }

    init {
        val dao = AppDatabase.getInstance(application).taskDao()
        repository = TaskRepository(dao)
    }

    fun setDate(date: String) {
        _selectedDate.value = date
    }

    fun addTask(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val existing = tasksForDate.value?.size ?: 0
            repository.addTask(name.trim(), existing)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun renameTask(taskId: Long, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.renameTask(taskId, newName.trim())
        }
    }

    /** Cycles status: blank -> done -> missed -> blank */
    fun cycleStatus(item: TaskWithEntry) {
        viewModelScope.launch {
            val date = _selectedDate.value ?: return@launch
            val current = repository.getEntry(item.taskId, date)
            val nextStatus = com.vishnu.lifenest.data.TaskStatus.next(current?.status ?: 0)
            val entry = (current ?: TaskEntryEntity(item.taskId, date)).apply { status = nextStatus }
            repository.saveEntry(entry)
        }
    }

    /** Called when user types start/end time (already formatted like "05:30 AM") */
    fun updateTime(item: TaskWithEntry, newStart: String?, newEnd: String?) {
        viewModelScope.launch {
            val date = _selectedDate.value ?: return@launch
            val current = repository.getEntry(item.taskId, date) ?: TaskEntryEntity(item.taskId, date)
            val start = newStart ?: current.startTime
            val end = newEnd ?: current.endTime
            current.startTime = start
            current.endTime = end
            current.spentMinutes = TimeUtils.spentMinutes(start, end)
            repository.saveEntry(current)
        }
    }

    fun updateRemarks(item: TaskWithEntry, remarks: String) {
        viewModelScope.launch {
            val date = _selectedDate.value ?: return@launch
            val current = repository.getEntry(item.taskId, date) ?: TaskEntryEntity(item.taskId, date)
            current.remarks = remarks
            repository.saveEntry(current)
        }
    }
}
