package com.qless.data.repository

import com.qless.data.local.dao.NotificationDao
import com.qless.data.local.entity.toDomain
import com.qless.data.local.entity.toEntity
import com.qless.domain.model.AppNotification
import com.qless.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(private val dao: NotificationDao) : NotificationRepository {

    override fun observe(userId: String): Flow<List<AppNotification>> =
        dao.observe(userId).map { list -> list.map { it.toDomain() } }

    override fun unreadCount(userId: String): Flow<Int> = dao.unreadCount(userId)

    override suspend fun add(notification: AppNotification) = dao.insert(notification.toEntity())

    override suspend fun markAllRead(userId: String) = dao.markAllRead(userId)

    override suspend fun clear(userId: String) = dao.clear(userId)
}
