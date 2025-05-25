/**
 * ViewModel responsible for managing and persisting the application's theme selection.
 *
 * @property application The application context used for accessing preferences.
 * @constructor Initializes the ViewModel and starts observing the stored theme preference.
 *
 * Exposes a [selectedTheme] [StateFlow] representing the current theme selection,
 * which is updated based on persisted user preferences using [ThemePreferences].
 *
 * Provides [setTheme] to update the theme both in memory and in persistent storage.
 */
package com.example.memori.database.theme_data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.memori.preference.ThemePreferences
import com.example.memori.setup.ThemeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(private val application: Application) : AndroidViewModel(application) {

   private val context = application.applicationContext

    private val _selectedTheme = MutableStateFlow(ThemeType.SYSTEM)
    val selectedTheme: StateFlow<ThemeType> = _selectedTheme.asStateFlow()

    /**
     * Initializes the ViewModel by launching a coroutine in the viewModelScope.
     * Collects the current theme preference from [ThemePreferences.getTheme] as a Flow,
     * and updates the [_selectedTheme] state accordingly.
     *
     * The theme is mapped as follows:
     * - "LIGHT" sets [ThemeType.LIGHT]
     * - "DARK" sets [ThemeType.DARK]
     * - Any other value defaults to [ThemeType.SYSTEM]
     *
     * @see ThemePreferences.getTheme
     * @see ThemeType
     */
    init {
        viewModelScope.launch {
            ThemePreferences.getTheme(context).collect { storedTheme ->
                _selectedTheme.value = when (storedTheme) {
                    "LIGHT" -> ThemeType.LIGHT
                    "DARK" -> ThemeType.DARK
                    else -> ThemeType.SYSTEM
                }
            }
        }
    }

    fun setTheme(theme: ThemeType) {
        _selectedTheme.value = theme
        viewModelScope.launch {
            ThemePreferences.saveTheme(context, theme.name)
        }
    }
}
