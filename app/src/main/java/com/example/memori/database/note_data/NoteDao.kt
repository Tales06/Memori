/**
 * Data Access Object (DAO) for the NotesEntity.
 * Provides methods for interacting with the notes table in the database.
 *
 * Functions include:
 * - Inserting, updating, and deleting notes.
 * - Querying notes by various criteria (all notes, by ID, favorites, archived, by folder, search).
 * - Archiving and unarchiving notes.
 * - Moving notes to folders and clearing folder assignments.
 *
 * All database operations that modify data are suspend functions to support coroutine usage.
 * Query functions return Flow to provide observable data streams.
 */
package com.example.memori.database.note_data

import androidx.room.*
import kotlinx.coroutines.flow.Flow


/*
* funzioni suspend permettono di sospendere la loro esecuzione senza bloccare il thread
* le funzioni suspend possono essere chiamate da una coroutine o da un altro suspend function
* e non bloccano il thread
* */
@Dao
interface NoteDao{

    @Upsert
    suspend fun insert(note: NotesEntity): Long

    @Query("SELECT * FROM notes WHERE archivedNote = 0 AND folderId IS NULL ORDER BY id DESC")
    fun getAllNotes(): Flow<List<NotesEntity>>

    @Update
    suspend fun update(note: NotesEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun delete(noteId: Int)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteById(noteId: Int): Flow<NotesEntity>

    @Query("SELECT * FROM notes WHERE favorite = 1")
    fun getFavoritesNote(): Flow<List<NotesEntity>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%'")
    fun searchNotes(searchQuery: String): Flow<List<NotesEntity>>

    @Query("SELECT * FROM notes WHERE archivedNote = 1")
    fun getArchivedNotes(): Flow<List<NotesEntity>>

    @Query("UPDATE notes SET archivedNote = 1 WHERE id = :noteId")
    suspend fun archiveNote(noteId: Int)

    @Query("UPDATE notes SET archivedNote = 0 WHERE id = :noteId")
    suspend fun unArchiveNote(noteId: Int)

    @Query("UPDATE notes SET folderId = :folderId WHERE id = :noteId")
    suspend fun moveNoteToFolder(noteId: Int, folderId: Int)

    @Query("SELECT * FROM notes WHERE folderId = :folderId")
    fun getNotesInFolder(folderId: Int): Flow<List<NotesEntity>>

    @Query("UPDATE notes SET folderId = NULL WHERE id = :noteId")
    suspend fun clearNoteFolder(noteId: Int)







}

