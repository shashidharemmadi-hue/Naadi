package com.example.nadi.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    private object PreferencesKeys {
        val SA_FREQUENCY = doublePreferencesKey("sa_frequency")
        val SELECTED_RAGA_NAME = stringPreferencesKey("selected_raga_name")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val VOLUME = doublePreferencesKey("volume")
    }

    val saFrequencyFlow: Flow<Double> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SA_FREQUENCY] ?: 240.0
        }

    val selectedRagaNameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_RAGA_NAME] ?: "Hamsadhwani"
        }

    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] ?: true
        }

    val volumeFlow: Flow<Double> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.VOLUME] ?: 0.7
        }

    suspend fun saveSaFrequency(frequency: Double) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SA_FREQUENCY] = frequency
        }
    }

    suspend fun saveSelectedRagaName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_RAGA_NAME] = name
        }
    }

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] = isDarkMode
        }
    }

    suspend fun saveVolume(volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOLUME] = volume.toDouble()
        }
    }
}
