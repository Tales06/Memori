/**
 * Utility functions and keys for managing user preferences related to the setup flow using DataStore.
 *
 * This file defines:
 * - A DataStore instance for storing preferences.
 * - A key for tracking whether the user has seen the setup screen.
 * - Extension functions to get and set the setup completion state.
 *
 * Usage:
 * - Call [Context.hasSeenSetup] to check if the setup has been completed.
 * - Call [Context.setHasSeenSetup] to update the setup completion state.
 */
package com.example.memori.preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "memori_setup")

object PreferencesKeys {
    val SETUP = booleanPreferencesKey("setup")
}

suspend fun Context.hasSeenSetup(): Boolean {
    val preferences = dataStore.data.first()
    return preferences[PreferencesKeys.SETUP] ?: false
}

suspend fun Context.setHasSeenSetup(hasSeen: Boolean) {
    dataStore.edit { it[PreferencesKeys.SETUP] = hasSeen }
}
