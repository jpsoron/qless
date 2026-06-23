package com.qless.domain.repository

import com.qless.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    /** Avisos del usuario, más nuevos primero. */
    fun observe(userId: String): Flow<List<AppNotification>>

    /** Cantidad de avisos no leídos del usuario (para el badge de la campana). */
    fun unreadCount(userId: String): Flow<Int>

    suspend fun add(notification: AppNotification)
    suspend fun markAllRead(userId: String)
    suspend fun clear(userId: String)
}
