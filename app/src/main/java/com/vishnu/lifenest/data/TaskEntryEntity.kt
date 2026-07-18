package com.vishnu.lifenest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Status values for a task on a given day.
 */
object TaskStatus {
    const val BLANK = 0
    const val DONE = 1
    const val MISSED = 2

    fun next(current: Int): Int = when (current) {
        DONE -> MISSED
        MISSED -> BLANK
        else -> DONE
    }
}

/**
 * One row per (task, date). Holds status, start/end time, spent time,
 * and remarks -- all of which are different for every day, as requested.
 * dateStr is stored as "yyyy-MM-dd" for easy sorting/filtering.
 */
@Entity(tableName = "task_entries", primaryKeys = ["taskId", "dateStr"])
data class TaskEntryEntity(
    val taskId: Long,
    val dateStr: String,
    var status: Int = TaskStatus.BLANK,
    var startTime: String? = null,   // stored as "HH:mm a" e.g. "05:30 AM"
    var endTime: String? = null,
    var spentMinutes: Int? = null,
    var remarks: String? = null
)
