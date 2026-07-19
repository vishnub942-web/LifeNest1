package com.vishnu.lifenest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var heading: String,
    var content: String,
    val createdAt: String,   // display string, never changes after creation
    var modifiedAt: String   // updated on every edit
)
