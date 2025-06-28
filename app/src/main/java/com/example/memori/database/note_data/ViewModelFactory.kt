/**
 * Factory class for creating instances of [NoteViewModel] with a [NotesRepository] dependency.
 *
 * This factory is used to provide the [NoteViewModel] with the required repository when using
 * the ViewModelProvider. It ensures that the ViewModel is constructed with the necessary
 * dependencies for accessing and managing note data.
 *
 * @property repository The [NotesRepository] instance to be provided to the [NoteViewModel].
 */
package com.example.memori.database.note_data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NoteViewModelFactory(private val repository: NotesRepository, private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

