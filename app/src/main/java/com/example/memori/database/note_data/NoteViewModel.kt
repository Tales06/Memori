package com.example.memori.database.note_data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memori.sync.FirestoreNoteRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NotesRepository): ViewModel() {

    private val repoFireStore = FirestoreNoteRepository()

    val allNotes: StateFlow<List<NotesEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun insert(note: NotesEntity) = viewModelScope.launch {
        repository.insert(note)

        val userID = Firebase.auth.currentUser?.uid

        if(userID != null){
            repoFireStore.uploadNote(userID, listOf(note))
        }
    }

    fun delete(noteId: Int) = viewModelScope.launch {
        repository.delete(noteId)

        val userID = Firebase.auth.currentUser?.uid

        if (userID != null) {
            repoFireStore.deleteNote(userID, noteId)

        }
    }

    fun update(note: NotesEntity) = viewModelScope.launch {
        repository.update(note)

        val userID = Firebase.auth.currentUser?.uid

        if (userID != null) {
            repoFireStore.uploadNote(userID, listOf(note))
        }
    }

    fun getNoteById(id: Int): StateFlow<NotesEntity?>{
        return repository.getNoteById(id).stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    fun getFavoritesNote(): Flow<List<NotesEntity>> {
        return repository.getFavoritesNote()
    }

    fun searchNotes(searchQuery: String): Flow<List<NotesEntity>> {
        return repository.searchNotes(searchQuery)
    }

    fun getArchivedNotes(): Flow<List<NotesEntity>> {
        return repository.getArchivedNotes()
    }

    fun archiveNote(noteId: Int) = viewModelScope.launch {
        repository.archiveNote(noteId)

        val userID = Firebase.auth.currentUser?.uid

        if (userID != null) {
            val note = repository.getNoteById(noteId).firstOrNull()
            if (note != null) {
                repoFireStore.uploadNote(userID, listOf(note))
            }
        }

    }

    fun unArchiveNote(noteId: Int) = viewModelScope.launch {
        repository.unArchiveNote(noteId)

        val userID = Firebase.auth.currentUser?.uid
        if (userID != null) {
            val note = repository.getNoteById(noteId).firstOrNull()
            if (note != null) {
                repoFireStore.uploadNote(userID, listOf(note))
            }
        }

    }

    fun moveNoteToFolder(noteId: Int, folderId: Int) = viewModelScope.launch {
        repository.moveNoteToFolder(noteId, folderId)

        val userID = Firebase.auth.currentUser?.uid
        if (userID != null) {
            val note = repository.getNoteById(noteId).firstOrNull()
            if (note != null) {
                repoFireStore.uploadNote(userID, listOf(note))
            }
        }

    }

    fun getNotesInFolder(folderId: Int): Flow<List<NotesEntity>> {
        return repository.getNotesInFolder(folderId)
    }

    fun deletePathImg(noteId: Int) = viewModelScope.launch {
        repository.deletePathImg(noteId)

        val userID = Firebase.auth.currentUser?.uid
        if (userID != null) {
            val note = repository.getNoteById(noteId).firstOrNull()
            if (note != null) {
                repoFireStore.uploadNote(userID, listOf(note))
            }
        }

    }






    //funzione di sincronizzazione
    fun syncAllNotes(userId: String){
        viewModelScope.launch {
            val localNotes = repository.allNotes.first()
            repoFireStore.uploadNote(userId, localNotes)

            val remoteNotes = repoFireStore.getAllNotesFromCloud(userId)

            remoteNotes.forEach { remote ->
                val localDb = repository.getNoteById(remote.id).firstOrNull()
                if(localDb == null || remote.lastModified > localDb.lastModified){
                    repository.insert(remote)
                } else {
                    repository.update(remote)
                }
            }

        }
    }


}