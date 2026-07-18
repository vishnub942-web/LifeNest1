package com.vishnu.lifenest.data

/**
 * Combines a task's name with that specific day's entry data.
 * If no entry exists yet for the date, entry fields default to blank.
 */
data class TaskWithEntry(
    val taskId: Long,
    val taskName: String,
    val dateStr: String,
    val status: Int,
    val startTime: String?,
    val endTime: String?,
    val spentMinutes: Int?,
    val remarks: String?
)

/**
 * Used on the Task Calendar screen: how many days in the month
 * a given task was marked DONE.
 */
data class TaskCompletion(
    val taskId: Long,
    val taskName: String,
    val doneCount: Int,
    val totalDaysSoFar: Int
)
