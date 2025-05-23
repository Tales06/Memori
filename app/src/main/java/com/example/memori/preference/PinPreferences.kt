package com.example.memori.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object PinPreferences {

    private val Context.pinDataStore by preferencesDataStore(name = "pin_preferences")
    private val PIN_HASH_KEY = stringPreferencesKey("pin_hash_key")

    suspend fun Context.savePinHash(pinHash: String?) {
        pinDataStore.edit { preferences ->
            preferences[PIN_HASH_KEY] = pinHash.toString()
        }
    }

    fun Context.pinHashFlow(): Flow<String?> =
        pinDataStore.data.map { prefs ->
            prefs[PIN_HASH_KEY]
        }

    suspend fun Context.deletePinHash() {
        pinDataStore.edit { preferences ->
            preferences.remove(PIN_HASH_KEY)
        }
    }
}