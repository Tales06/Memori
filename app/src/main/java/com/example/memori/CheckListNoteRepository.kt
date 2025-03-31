package com.example.memori

import kotlinx.coroutines.flow.Flow

class CheckListNoteRepository(private val checkListDao: CheckListDao) {

    val allCheckListItems: Flow<List<CheckListNoteEntity>> = checkListDao.getAllCheckListItems()

    //funzione per inserire un elemento della lista di controllo
    suspend fun insertCheckListItem(checkListNoteEntity: CheckListNoteEntity){
        checkListDao.insert(checkListNoteEntity)
    }

    //funzione per ottenere un elemento della lista di controllo tramite id
    fun getCheckListItemsById(id: Int): Flow<CheckListNoteEntity> {
        return checkListDao.getCheckListItems(id)
    }

    //funzione per aggiornare un elemento della lista di controllo
    suspend fun updateCheckListItem(checkListNoteEntity: CheckListNoteEntity){
        checkListDao.updateCheckListItem(checkListNoteEntity)
    }

    //funzione per eliminare un elemento della lista di controllo
    suspend fun deleteCheckListItems(checkListId: Int){
        checkListDao.deleteCheckListItems(checkListId)
    }


}