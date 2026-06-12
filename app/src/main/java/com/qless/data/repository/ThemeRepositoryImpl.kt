package com.qless.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.qless.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "qless_settings")

class ThemeRepositoryImpl(private val context: Context) : ThemeRepository {

    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")

    override val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[darkModeKey] ?: false }

    override val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[onboardingCompletedKey] ?: false }

    override suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[darkModeKey] = enabled }
    }

    override suspend fun setOnboardingCompleted() {
        context.dataStore.edit { prefs -> prefs[onboardingCompletedKey] = true }
    }
}
