package com.vishnu.lifenest.ui.notepad

import android.app.Application
import androidx.lifecycle.*
import com.vishnu.lifenest.data.AppDatabase
import com.vishnu.lifenest.data.NoteEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NoteRepository(AppDatabase.getInstance(application).noteDao())
    private val timeFormat = SimpleDateFormat("hh:mm a, d MMMM yyyy", Locale.US)

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    val notes: LiveData<List<NoteEntity>> = _searchQuery.switchMap { q ->
        if (q.isNullOrBlank()) repository.getAll() else repository.search(q)
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun addNote(heading: String, content: String, onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val now = timeFormat.format(Date())
            val id = repository.insert(NoteEntity(heading = heading, content = content, createdAt = now, modifiedAt = now))
            onSaved(id)
        }
    }

    fun updateNote(note: NoteEntity, newHeading: String, newContent: String) {
        viewModelScope.launch {
            repository.update(note.copy(heading = newHeading, content = newContent, modifiedAt = timeFormat.format(Date())))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch { repository.delete(note) }
    }

    suspend fun getById(id: Long): NoteEntity? = repository.getById(id)
}
