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