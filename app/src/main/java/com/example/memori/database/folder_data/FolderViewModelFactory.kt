/**
 * Factory class for creating instances of [FolderViewModel] with a specific [FolderRepository].
 *
 * This factory is used to provide the [FolderRepository] dependency to the [FolderViewModel]
 * when it is instantiated by the [ViewModelProvider].
 *
 * @property repository The [FolderRepository] instance to be provided to the [FolderViewModel].
 */
package com.example.memori.database.folder_data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FolderViewModelFactory(private val repository: FolderRepository) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the specified [ViewModel] class.
     *
     * @param T The type of [ViewModel] to create.
     * @param modelClass The [Class] object corresponding to the [ViewModel] subclass.
     * @return A new instance of the requested [ViewModel] subclass.
     * @throws IllegalArgumentException If the [modelClass] is not assignable from [FolderViewModel].
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FolderViewModel::class.java)) {
            return FolderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}