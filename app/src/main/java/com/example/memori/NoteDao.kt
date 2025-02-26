package com.example.memori

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


/*
* funzioni suspend permettono di sospendere la loro esecuzione senza bloccare il thread
* le funzioni suspend possono essere chiamate da una coroutine o da un altro suspend function
* e non bloccano il thread
* */
@Dao
interface NoteDao{

    @Insert
    suspend fun insert(note: NotesEntity): Long

    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<NotesEntity>>

    @Update
    suspend fun update(note: NotesEntity)

    @Delete
    suspend fun delete(note: NotesEntity)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteById(noteId: Int): Flow<NotesEntity>

    @Query("SELECT * FROM notes WHERE favorite = 1")
    fun getFavoritesNote(): Flow<List<NotesEntity>>

}