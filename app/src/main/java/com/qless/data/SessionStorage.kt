package com.qless.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "qless_session")
private val SESSION_JSON_KEY = stringPreferencesKey("session_json")

class SessionStorage(private val context: Context) {
    suspend fun save(json: String) {
        context.sessionDataStore.edit { it[SESSION_JSON_KEY] = json }
    }

    suspend fun load(): String? =
        context.sessionDataStore.data.map { it[SESSION_JSON_KEY] }.first()

    suspend fun clear() {
        context.sessionDataStore.edit { it.remove(SESSION_JSON_KEY) }
    }
}
