package com.vishnu.lifenest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A task NAME (e.g. "Morning Workout"). This is the template row that
 * stays the same across all days. Add/remove/rename here affects
 * every day going forward, as requested.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String,
    var sortOrder: Int = 0
)
