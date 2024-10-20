package com.parallelc.mixfliptool

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_scale_preferences")

class AppScaleManager(private val context: Context) {

    private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")

    private var isFirstLaunchChecked: Boolean = false

    private suspend fun checkFirstLaunch() {
        if (isFirstLaunchChecked) {
            return
        }

        val isFirstLaunch = context.dataStore.data
            .map { preferences ->
                preferences[FIRST_LAUNCH_KEY] ?: true
            }
            .first()

        if (isFirstLaunch) {
            resetDefaultPreferences()
        }

        isFirstLaunchChecked = true
    }

    suspend fun getAllPreferences(): Map<String, String> {
        checkFirstLaunch()
        return context.dataStore.data.first().asMap()
            .filterKeys { key -> key != FIRST_LAUNCH_KEY }
            .mapKeys { (key, _) -> key.name }
            .mapValues { (_, value) -> value.toString() }
    }

    suspend fun saveAppScale(packageName: String, scale: String) {
        checkFirstLaunch()
        context.dataStore.edit { preferences ->
            if (scale.isEmpty()) {
                preferences.remove(scaleKeyForApp(packageName))
            } else {
                preferences[scaleKeyForApp(packageName)] = scale
            }
        }
    }

    suspend fun resetDefaultPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
            preferences[scaleKeyForApp("com.android.contacts")] = "0.67"
            preferences[scaleKeyForApp("com.android.calendar")] = "0.7"
            preferences[scaleKeyForApp("com.android.mms")] = "0.8"
            preferences[scaleKeyForApp("com.android.soundrecorder")] = "0.7"
            preferences[scaleKeyForApp("com.miui.calculator")] = "0.7"
            preferences[scaleKeyForApp("com.miui.gallery")] = "0.7"
            preferences[FIRST_LAUNCH_KEY] = false
        }
    }

    private fun scaleKeyForApp(packageName: String): Preferences.Key<String> {
        return stringPreferencesKey(packageName)
    }
}