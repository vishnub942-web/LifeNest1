package com.vishnu.lifenest.ui.creditdebt

import androidx.lifecycle.LiveData
import com.vishnu.lifenest.data.LedgerDao
import com.vishnu.lifenest.data.LedgerEntity

class LedgerRepository(private val dao: LedgerDao) {
    fun getAll(): LiveData<List<LedgerEntity>> = dao.getAll()
    suspend fun insert(entry: LedgerEntity) = dao.insert(entry)
    suspend fun update(entry: LedgerEntity) = dao.update(entry)
    suspend fun delete(entry: LedgerEntity) = dao.delete(entry)
}
