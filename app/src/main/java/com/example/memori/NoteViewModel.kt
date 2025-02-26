package com.example.memori

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NotesRepository): ViewModel() {

    val allNotes: StateFlow<List<NotesEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun insert(note: NotesEntity) = viewModelScope.launch {
        repository.insert(note)
    }

    fun delete(note: NotesEntity) = viewModelScope.launch {
        repository.delete(note)
    }

    fun update(note: NotesEntity) = viewModelScope.launch {
        repository.update(note)
    }

    fun getNoteById(id: Int): StateFlow<NotesEntity?>{
        return repository.getNoteById(id).stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    fun getFavoritesNote(): Flow<List<NotesEntity>> {
        return repository.getFavoritesNote()
    }
}