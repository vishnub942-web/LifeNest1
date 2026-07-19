package com.vishnu.lifenest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

object EventType {
    const val YEARLY = 0   // birthday / anniversary, repeats every year on fromDate's month/day
    const val RANGE = 1    // fromDate (required) -> toDate (optional)
}

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var title: String,
    var type: Int,
    var fromDate: String,     // "yyyy-MM-dd"
    var toDate: String? = null
)
