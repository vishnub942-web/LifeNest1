package com.vishnu.lifenest.ui.calendar

import android.app.Application
import androidx.lifecycle.*
import com.vishnu.lifenest.data.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskCalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository =
        TaskRepository(AppDatabase.getInstance(application).taskDao())
    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val monthFmt = SimpleDateFormat("yyyy-MM", Locale.US)

    private val cal = Calendar.getInstance()

    private val _selectedDate = MutableLiveData(dateFmt.format(cal.time))
    val selectedDate: LiveData<String> = _selectedDate

    private val _visibleMonth = MutableLiveData(monthFmt.format(cal.time)) // "yyyy-MM"
    val visibleMonth: LiveData<String> = _visibleMonth

    val tasksForSelectedDate: LiveData<List<TaskWithEntry>> = _selectedDate.switchMap { date ->
        repository.getTasksForDate(date)
    }

    private val _monthlyCompletion = MutableLiveData<List<TaskCompletion>>()
    val monthlyCompletion: LiveData<List<TaskCompletion>> = _monthlyCompletion

    // dates (yyyy-MM-dd) a specific tapped task was marked DONE, for highlighting on the grid
    private val _highlightedDates = MutableLiveData<Set<String>>(emptySet())
    val highlightedDates: LiveData<Set<String>> = _highlightedDates

    init {
        refreshCompletionReport()
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun changeMonth(delta: Int) {
        val c = Calendar.getInstance()
        c.time = monthFmt.parse(_visibleMonth.value ?: monthFmt.format(Date())) ?: Date()
        c.add(Calendar.MONTH, delta)
        _visibleMonth.value = monthFmt.format(c.time)
        refreshCompletionReport()
        _highlightedDates.value = emptySet()
    }

    fun refreshCompletionReport() {
        viewModelScope.launch {
            val yearMonth = _visibleMonth.value ?: return@launch
            val tasks = repository.getAllTasksSync()
            val today = dateFmt.format(Date())
            val daysSoFarInMonth = daysElapsedInMonth(yearMonth, today)
            val result = tasks.map { task ->
                val done = repository.countDoneInMonth(task.id, yearMonth)
                TaskCompletion(task.id, task.name, done, daysSoFarInMonth)
            }
            _monthlyCompletion.postValue(result)
        }
    }

    /** Tap a task name in the completion report -> highlight the dates it was done on the grid */
    fun highlightTaskDates(taskId: Long) {
        viewModelScope.launch {
            val dates = repository.getDoneDatesForTask(taskId).toSet()
            _highlightedDates.postValue(dates)
        }
    }

    fun clearHighlight() {
        _highlightedDates.value = emptySet()
    }

    private fun daysElapsedInMonth(yearMonth: String, todayStr: String): Int {
        val todayMonth = todayStr.substring(0, 7)
        return if (yearMonth == todayMonth) {
            todayStr.substring(8, 10).toIntOrNull() ?: 1
        } else {
            // past or future month: use full days in that month if it's in the past
            val c = Calendar.getInstance()
            c.time = monthFmt.parse(yearMonth) ?: Date()
            val maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH)
            if (yearMonth < todayMonth) maxDay else 0
        }
    }
}
