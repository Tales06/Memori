package com.example.memori

import androidx.room.*
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

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%'")
    fun searchNotes(searchQuery: String): Flow<List<NotesEntity>>

}

@Dao
interface CheckListDao{

    @Insert
    suspend fun insert(checkListNote: CheckListNoteEntity): Long

    @Query("SELECT * FROM checklist_item")
    fun getAllCheckListItems(): Flow<List<CheckListNoteEntity>>

    @Query("SELECT * FROM checklist_item WHERE checkList_id = :checkListId")
    fun getCheckListItems(checkListId: Int): Flow<CheckListNoteEntity>

    @Update
    suspend fun updateCheckListItem(checkListNote: CheckListNoteEntity)


    @Query("DELETE FROM checklist_item WHERE checkList_id = :checkListId")
    suspend fun deleteCheckListItems(checkListId: Int)

}