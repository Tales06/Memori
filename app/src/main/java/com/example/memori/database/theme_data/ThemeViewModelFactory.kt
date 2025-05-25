/**
 * Factory class for creating instances of [ThemeViewModel] with a provided [Application] context.
 *
 * This factory is used to supply the [ThemeViewModel] with the application context,
 * which is required for accessing resources or other application-level operations.
 *
 * @property app The [Application] instance to be passed to the [ThemeViewModel].
 */
package com.example.memori.database.theme_data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ThemeViewModelFactory(private val app: Application):ViewModelProvider.Factory {

    /**
     * Creates a new instance of the specified [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of [ThemeViewModel] cast to the specified type [T].
     * @throws IllegalArgumentException if the modelClass is not assignable from [ThemeViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ThemeViewModel(app) as T
    }


}