package com.qless.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qless.domain.model.AppNotification

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val orderId: String,
    val orderNumero: Int,
    val localNombre: String,
    val title: String,
    val body: String,
    val status: String,
    val createdAt: Long,
    val read: Boolean,
)

fun NotificationEntity.toDomain() = AppNotification(
    id = id,
    userId = userId,
    orderId = orderId,
    orderNumero = orderNumero,
    localNombre = localNombre,
    title = title,
    body = body,
    status = status,
    createdAt = createdAt,
    read = read,
)

fun AppNotification.toEntity() = NotificationEntity(
    id = id,
    userId = userId,
    orderId = orderId,
    orderNumero = orderNumero,
    localNombre = localNombre,
    title = title,
    body = body,
    status = status,
    createdAt = createdAt,
    read = read,
)
