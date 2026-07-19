package com.vishnu.lifenest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

object LedgerType {
    const val CREDIT = 0  // someone owes / will pay the user
    const val DEBIT = 1   // the user owes / will pay someone
}

@Entity(tableName = "ledger")
data class LedgerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var personName: String,
    var amount: Double,
    var type: Int,
    var note: String = "",
    var dateStr: String,        // "yyyy-MM-dd", editable
    var settled: Boolean = false
)
