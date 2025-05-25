/**
 * Repository class that provides an abstraction layer over the [NoteDao] for managing notes data.
 * Handles all data operations related to notes, including CRUD operations, searching, archiving,
 * folder management, and favorites.
 *
 * @property noteDao The Data Access Object for notes.
 *
 * Exposed methods:
 * - [allNotes]: A [Flow] emitting the list of all notes.
 * - [insert]: Inserts a new note and returns its row ID.
 * - [getNoteById]: Retrieves a note by its ID as a [Flow].
 * - [update]: Updates an existing note.
 * - [delete]: Deletes a note by its ID.
 * - [getFavoritesNote]: Retrieves all favorite notes as a [Flow].
 * - [searchNotes]: Searches notes by a query string as a [Flow].
 * - [getArchivedNotes]: Retrieves all archived notes as a [Flow].
 * - [archiveNote]: Archives a note by its ID.
 * - [unArchiveNote]: Unarchives a note by its ID.
 * - [moveNoteToFolder]: Moves a note to a specified folder.
 * - [getNotesInFolder]: Retrieves all notes in a specific folder as a [Flow].
 * - [clearNoteFolder]: Removes a note from its folder.
 */
package com.example.memori.database.note_data

import kotlinx.coroutines.flow.Flow

class NotesRepository(private val noteDao: NoteDao){


    val allNotes: Flow<List<NotesEntity>> = noteDao.getAllNotes()

    //funzione per inserire una nota
    suspend fun insert(note: NotesEntity): Long {
       return noteDao.insert(note)
    }



    fun getNoteById(id: Int): Flow<NotesEntity> {
        return noteDao.getNoteById(id)
    }

    suspend fun update(note: NotesEntity){
        noteDao.update(note)
    }

    suspend fun delete(noteId: Int){
        noteDao.delete(noteId)
    }

    fun getFavoritesNote(): Flow<List<NotesEntity>> {
        return noteDao.getFavoritesNote()
    }

    fun searchNotes(searchQuery: String): Flow<List<NotesEntity>> {
        return noteDao.searchNotes(searchQuery)
    }

    fun getArchivedNotes(): Flow<List<NotesEntity>> {
        return noteDao.getArchivedNotes()
    }

    suspend fun archiveNote(noteId: Int) {
        noteDao.archiveNote(noteId)
    }

    suspend fun unArchiveNote(noteId: Int) {
        noteDao.unArchiveNote(noteId)
    }

    suspend fun moveNoteToFolder(noteId: Int, folderId: Int) {
        noteDao.moveNoteToFolder(noteId, folderId)
    }

    fun getNotesInFolder(folderId: Int): Flow<List<NotesEntity>> {
        return noteDao.getNotesInFolder(folderId)
    }

    suspend fun clearNoteFolder(noteId: Int) {
        noteDao.clearNoteFolder(noteId)
    }








}