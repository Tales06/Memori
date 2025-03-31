package com.example.memori
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CheckListItemViewModel(private val repository: CheckListNoteRepository): ViewModel() {


    val allCheckListItems: StateFlow<List<CheckListNoteEntity>> = repository.allCheckListItems
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun getCheckListItemsById(id: Int): StateFlow<CheckListNoteEntity?>{
        return repository.getCheckListItemsById(id).stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    fun insertCheckListItem(checkListNoteEntity: CheckListNoteEntity) = viewModelScope.launch {
        repository.insertCheckListItem(checkListNoteEntity)
    }

    fun updateCheckListItem(checkListNoteEntity: CheckListNoteEntity) = viewModelScope.launch {
        repository.updateCheckListItem(checkListNoteEntity)
    }

    fun deleteCheckListItems(checkListId: Int) = viewModelScope.launch {
        repository.deleteCheckListItems(checkListId)
    }


}