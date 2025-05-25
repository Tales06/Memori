/**
 * An object that manages theme preferences using Android's DataStore.
 *
 * This class provides methods to save and retrieve the user's selected theme.
 *
 * - The theme is stored as a string in the DataStore with the key "theme_preferences".
 * - If no theme is set, the default value returned is "SYSTEM".
 *
 * Functions:
 * - [saveTheme]: Saves the selected theme to DataStore.
 * - [getTheme]: Returns a Flow that emits the current theme preference.
 */
package com.example.memori.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


object ThemePreferences {

    private val Context.dataStore by preferencesDataStore(name = "theme_preferences")

    private val THEME_KEY = stringPreferencesKey("theme_preferences")

    suspend fun saveTheme(context: Context, theme: String) {
        context.dataStore.edit { pref ->
            pref[THEME_KEY] = theme
        }
    }

    fun getTheme(context: Context): Flow<String> {
        return context.dataStore.data.map { pref ->
            pref[THEME_KEY] ?: "SYSTEM"
        }
    }
}