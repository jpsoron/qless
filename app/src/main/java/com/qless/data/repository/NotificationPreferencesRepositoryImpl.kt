package com.qless.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.qless.domain.model.NotificationPreferences
import com.qless.domain.repository.NotificationPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.notifPrefsDataStore: DataStore<Preferences> by
    preferencesDataStore(name = "qless_notification_prefs")

class NotificationPreferencesRepositoryImpl(private val context: Context) : NotificationPreferencesRepository {

    private val orderStatusKey = booleanPreferencesKey("notif_order_status")
    private val orderReadyKey = booleanPreferencesKey("notif_order_ready")
    private val soundVibrationKey = booleanPreferencesKey("notif_sound_vibration")

    override val preferences: Flow<NotificationPreferences> = context.notifPrefsDataStore.data
        .map { prefs ->
            NotificationPreferences(
                orderStatus = prefs[orderStatusKey] ?: true,
                orderReady = prefs[orderReadyKey] ?: true,
                soundVibration = prefs[soundVibrationKey] ?: true,
            )
        }

    override suspend fun current(): NotificationPreferences = preferences.first()

    override suspend fun setOrderStatus(enabled: Boolean) {
        context.notifPrefsDataStore.edit { it[orderStatusKey] = enabled }
    }

    override suspend fun setOrderReady(enabled: Boolean) {
        context.notifPrefsDataStore.edit { it[orderReadyKey] = enabled }
    }

    override suspend fun setSoundVibration(enabled: Boolean) {
        context.notifPrefsDataStore.edit { it[soundVibrationKey] = enabled }
    }
}
