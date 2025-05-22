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
