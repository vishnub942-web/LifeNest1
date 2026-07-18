package com.vishnu.lifenest.data

import androidx.lifecycle.LiveData

class TaskRepository(private val dao: TaskDao) {

    fun getAllTasks(): LiveData<List<TaskEntity>> = dao.getAllTasks()

    suspend fun getAllTasksSync(): List<TaskEntity> = dao.getAllTasksSync()

    fun getTasksForDate(date: String): LiveData<List<TaskWithEntry>> = dao.getTasksForDate(date)

    suspend fun addTask(name: String, sortOrder: Int) {
        dao.insertTask(TaskEntity(name = name, sortOrder = sortOrder))
    }

    suspend fun renameTask(taskId: Long, newName: String) {
        dao.renameTask(taskId, newName)
    }

    suspend fun deleteTask(taskId: Long) {
        dao.deleteEntriesForTask(taskId)
        dao.deleteTaskById(taskId)
    }

    suspend fun saveEntry(entry: TaskEntryEntity) {
        dao.upsertEntry(entry)
    }

    suspend fun getEntry(taskId: Long, date: String): TaskEntryEntity? = dao.getEntry(taskId, date)

    suspend fun getEntriesForTask(taskId: Long): List<TaskEntryEntity> = dao.getEntriesForTask(taskId)

    suspend fun countDoneInMonth(taskId: Long, yearMonth: String): Int =
        dao.countDoneInMonth(taskId, yearMonth)

    suspend fun getDoneDatesForTask(taskId: Long): List<String> = dao.getDoneDatesForTask(taskId)
}
