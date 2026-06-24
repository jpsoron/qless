package com.qless.domain.repository

import com.qless.domain.model.NotificationPreferences
import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesRepository {
    val preferences: Flow<NotificationPreferences>
    /** Snapshot actual (para decidir si disparar un aviso en el momento). */
    suspend fun current(): NotificationPreferences
    suspend fun setOrderStatus(enabled: Boolean)
    suspend fun setOrderReady(enabled: Boolean)
    suspend fun setSoundVibration(enabled: Boolean)
}
