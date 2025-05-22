package com.example.memori.preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferences {
    private val Context.dataStore by preferencesDataStore(name = "settings")

    private val IS_SYNC_ENABLED = booleanPreferencesKey("sync_is_enabled")

    suspend fun setSyncEnabled(context: Context, isEnabled: Boolean) {
        context.dataStore.edit { it[IS_SYNC_ENABLED] = isEnabled }
    }

    fun isSyncEnabled(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { it[IS_SYNC_ENABLED] ?: false }
    }
}