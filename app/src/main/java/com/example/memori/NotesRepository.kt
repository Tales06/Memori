package com.example.memori

import kotlinx.coroutines.flow.Flow

class NotesRepository(private val noteDao: NoteDao){
    val allNotes: Flow<List<NotesEntity>> = noteDao.getAllNotes()

    suspend fun insert(note: NotesEntity){
        noteDao.insert(note)

    }

    fun getNoteById(id: Int): Flow<NotesEntity> {
        return noteDao.getNoteById(id)
    }

    suspend fun update(note: NotesEntity){
        noteDao.update(note)
    }

    suspend fun delete(note: NotesEntity){
        noteDao.delete(note)
    }

    fun getFavoritesNote(): Flow<List<NotesEntity>> {
        return noteDao.getFavoritesNote()
    }

    fun searchNotes(searchQuery: String): Flow<List<NotesEntity>> {
        return noteDao.searchNotes(searchQuery)
    }


}