package com.vishnu.lifenest.ui.creditdebt

import android.app.Application
import androidx.lifecycle.*
import com.vishnu.lifenest.data.AppDatabase
import com.vishnu.lifenest.data.LedgerEntity
import com.vishnu.lifenest.data.LedgerType
import kotlinx.coroutines.launch

object LedgerFilter {
    const val ALL = 0
    const val CREDIT = 1
    const val DEBIT = 2
}

data class LedgerTotals(val credit: Double, val debit: Double) {
    val net: Double get() = credit - debit
}

class CreditDebtViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LedgerRepository(AppDatabase.getInstance(application).ledgerDao())
    private val allEntries = repository.getAll()

    private val _filter = MutableLiveData(LedgerFilter.ALL)
    val filter: LiveData<Int> = _filter

    val visibleEntries: LiveData<List<LedgerEntity>> = MediatorLiveData<List<LedgerEntity>>().apply {
        fun update() {
            val list = allEntries.value ?: emptyList()
            value = when (_filter.value) {
                LedgerFilter.CREDIT -> list.filter { it.type == LedgerType.CREDIT }
                LedgerFilter.DEBIT -> list.filter { it.type == LedgerType.DEBIT }
                else -> list
            }
        }
        addSource(allEntries) { update() }
        addSource(_filter) { update() }
    }

    val totals: LiveData<LedgerTotals> = allEntries.map { list ->
        val credit = list.filter { it.type == LedgerType.CREDIT && !it.settled }.sumOf { it.amount }
        val debit = list.filter { it.type == LedgerType.DEBIT && !it.settled }.sumOf { it.amount }
        LedgerTotals(credit, debit)
    }

    fun setFilter(f: Int) {
        _filter.value = f
    }

    fun addEntry(name: String, amount: Double, type: Int, note: String, dateStr: String) {
        if (name.isBlank() || amount <= 0) return
        viewModelScope.launch {
            repository.insert(
                LedgerEntity(personName = name.trim(), amount = amount, type = type, note = note.trim(), dateStr = dateStr)
            )
        }
    }

    fun toggleSettled(entry: LedgerEntity) {
        viewModelScope.launch {
            repository.update(entry.copy(settled = !entry.settled))
        }
    }

    fun delete(entry: LedgerEntity) {
        viewModelScope.launch { repository.delete(entry) }
    }
}
