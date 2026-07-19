package com.vishnu.lifenest.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LedgerDao {
    @Query("SELECT * FROM ledger ORDER BY settled ASC, id DESC")
    fun getAll(): LiveData<List<LedgerEntity>>

    @Insert
    suspend fun insert(entry: LedgerEntity): Long

    @Update
    suspend fun update(entry: LedgerEntity)

    @Delete
    suspend fun delete(entry: LedgerEntity)
}
