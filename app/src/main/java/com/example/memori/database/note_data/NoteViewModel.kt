/**
 * ViewModel for managing notes in the application.
 *
 * This ViewModel acts as a bridge between the UI and the data layer, handling operations related to notes,
 * including CRUD operations, archiving, folder management, and synchronization with Firestore.
 *
 * @property repository The local notes repository for database operations.
 */
package com.example.memori.database.note_data

import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memori.preference.UserPreferences
import com.example.memori.sync.FirestoreNoteRepository
import com.example.memori.sync.NetworkStatusTracker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(private val repository: NotesRepository, private val context: Context): ViewModel() {

    /**
     * Repository instance for handling Firestore operations related to notes.
     */

    /**
     * A [StateFlow] that emits the list of all notes from the repository.
     *
     * This flow is eagerly started in the [viewModelScope] and initialized with an empty list.
     */
    private val repoFireStore = FirestoreNoteRepository()
    val isCloudOnline = NetworkStatusTracker.isConnected

    private val isSyncEnabled = UserPreferences.isSyncEnabled(context)


    val allNotes: StateFlow<List<NotesEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * ViewModel for managing notes, providing methods for CRUD operations, folder management,
     * archiving, searching, and synchronization with a remote Firestore repository.
     *
     * Functions:
     * - insert(note, onInserted): Inserts a note into the local database and Firestore, invoking a callback with the new note's ID.
     * - delete(noteId): Deletes a note from the local database and Firestore.
     * - update(note): Updates a note in the local database and Firestore.
     * - getNoteById(id): Retrieves a note by its ID as a StateFlow.
     * - getFavoritesNote(): Returns a Flow of favorite notes.
     * - searchNotes(searchQuery): Searches notes by a query string, returning a Flow of results.
     * - getArchivedNotes(): Returns a Flow of archived notes.
     * - archiveNote(noteId): Archives a note locally and updates it in Firestore.
     * - unArchiveNote(noteId): Unarchives a note locally and updates it in Firestore.
     * - moveNoteToFolder(noteId, folderId): Moves a note to a folder locally and updates it in Firestore.
     * - getNotesInFolder(folderId): Returns a Flow of notes in a specific folder.
     * - clearNoteFolder(noteId): Removes a note from its folder locally and updates it in Firestore.
     * - syncAllNotes(userId): Synchronizes all local notes with Firestore, uploading local notes and merging remote changes.
     *
     * All operations that modify data are performed within the ViewModel's coroutine scope.
     * Firestore operations are performed only if a user is authenticated.
     */
    fun insert(note: NotesEntity, onInserted: (Int) -> Unit) = viewModelScope.launch {
        val dbId = repository.insert(note)

        withContext(Dispatchers.Main) {
            onInserted(dbId.toInt())
        }

        val userID = Firebase.auth.currentUser?.uid



        if(userID != null && isSyncEnabled.first()){
            repoFireStore.insertOneNote(userID, note.copy(id = dbId.toInt()))
        }
    }

    fun delete(noteId: Int) = viewModelScope.launch {
        repository.delete(noteId)

        val userID = Firebase.auth.currentUser?.uid

        if (userID != null && isSyncEnabled.first()) {
            repoFireStore.deleteNote(userID, noteId)

        }
    }

    fun update(note: NotesEntity) = viewModelScope.launch {
        repository.update(note)

        val userID = Firebase.auth.currentUser?.uid

        if (userID != null && isSyncEnabled.first()) {
            repoFireStore.uploadOneNote(userID, note)
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

        if (userID != null && isSyncEnabled.first()) {
            val note = repository.getNoteById(noteId).firstOrNull()
            if (note != null) {
                repoFireStore.uploadOneNote(userID, note)
            }
        }

    }

    fun unArchiveNote(noteId: Int) = viewModelScope.launch {
        repository.unArchiveNote(noteId)

        val userID = Firebase.auth.currentUser?.uid
        if (userID != null && isSyncEnabled.first()) {
            val note = repository.getNoteById(noteId).firstOrNull()
            if (note != null) {
                repoFireStore.uploadOneNote(userID, note)
            }
        }

    }

    fun moveNoteToFolder(noteId: Int, folderId: Int) = viewModelScope.launch {
        repository.moveNoteToFolder(noteId, folderId)

        val userID = Firebase.auth.currentUser?.uid
        if (userID != null && isSyncEnabled.first()) {
            val note = repository.getNoteById(noteId).firstOrNull()
            if (note != null) {
                repoFireStore.uploadOneNote(userID, note)
            }
        }

    }

    fun getNotesInFolder(folderId: Int): Flow<List<NotesEntity>> {
        return repository.getNotesInFolder(folderId)
    }

    fun clearNoteFolder(noteId: Int) = viewModelScope.launch {
        repository.clearNoteFolder(noteId)

        val userID = Firebase.auth.currentUser?.uid
        if (userID != null && isSyncEnabled.first()) {
            val note = repository.getNoteById(noteId).firstOrNull()
            if (note != null) {
                repoFireStore.uploadOneNote(userID, note)
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