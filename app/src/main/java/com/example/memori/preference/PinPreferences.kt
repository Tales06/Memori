/**
 * An object that manages storing, retrieving, and deleting a PIN hash using Android's DataStore.
 *
 * This object provides extension functions for [Context] to interact with the PIN hash value,
 * which is stored securely in a preferences DataStore named "pin_preferences".
 *
 * Functions:
 * - [Context.savePinHash]: Saves the provided PIN hash string to the DataStore.
 * - [Context.pinHashFlow]: Returns a [Flow] that emits the current PIN hash value, or null if not set.
 * - [Context.deletePinHash]: Removes the stored PIN hash from the DataStore.
 * - [Context.getPinHash]: Retrieves the current PIN hash value as a suspend function.
 *
 * Usage:
 * ```
 * // To save a PIN hash
 * context.savePinHash(pinHash)
 *
 * // To observe PIN hash changes
 * context.pinHashFlow().collect { pinHash -> ... }
 *
 * // To delete the PIN hash
 * context.deletePinHash()
 *
 * // To get the PIN hash synchronously in a coroutine
 * val pinHash = context.getPinHash()
 * ```
 */
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

    suspend fun Context.getPinHash(): String? {
        return pinHashFlow().first()
    }
}