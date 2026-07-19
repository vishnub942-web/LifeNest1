package com.vishnu.lifenest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

object ToDoStatus {
    const val PENDING = 0
    const val DONE = 1
    const val NOT_DONE = 2

    fun next(current: Int): Int = when (current) {
        DONE -> NOT_DONE
        NOT_DONE -> PENDING
        else -> DONE
    }
}

@Entity(tableName = "todos")
data class ToDoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var text: String,
    val addedAt: String,       // display string e.g. "11:11 AM, 5 March 2026"
    var status: Int = ToDoStatus.PENDING,
    var markedAt: String? = null
)
