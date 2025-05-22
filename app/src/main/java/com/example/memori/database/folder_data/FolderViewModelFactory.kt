package com.example.memori.database.folder_data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FolderViewModelFactory(private val repository: FolderRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FolderViewModel::class.java)) {
            return FolderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}