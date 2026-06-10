package com.qless.data.remote.dto

import com.qless.data.Order
import com.qless.data.OrderItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String,
    val numero: Int = 0,
    @SerialName("user_id") val userId: String,
    @SerialName("local_id") val localId: String,
    val status: String,
    @SerialName("total_amount") val totalAmount: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("order_items") val items: List<OrderItemDto> = emptyList(),
    val locales: LocalSnapshotDto? = null,
)

@Serializable
data class OrderItemDto(
    val id: String,
    @SerialName("order_id") val orderId: String,
    @SerialName("menu_item_id") val menuItemId: String,
    val nombre: String,
    @SerialName("unit_price") val unitPrice: Int,
    val quantity: Int,
)

@Serializable
data class LocalSnapshotDto(
    val nombre: String,
    val emoji: String,
)

@Serializable
internal data class NewOrderDto(
    @SerialName("user_id") val userId: String,
    @SerialName("local_id") val localId: String,
    @SerialName("total_amount") val totalAmount: Int,
    val status: String = "pending",
)

@Serializable
internal data class NewOrderItemDto(
    @SerialName("order_id") val orderId: String,
    @SerialName("menu_item_id") val menuItemId: String,
    val nombre: String,
    @SerialName("unit_price") val unitPrice: Int,
    val quantity: Int,
)

@Serializable
internal data class OrderIdDto(val id: String)

@Serializable
internal data class PerfilLocalDto(
    @SerialName("local_id") val localId: String?,
)

fun OrderDto.toDomain() = Order(
    id = id,
    numero = numero,
    userId = userId,
    localId = localId,
    localNombre = locales?.nombre ?: "",
    localEmoji = locales?.emoji ?: "",
    status = status,
    totalAmount = totalAmount,
    createdAt = createdAt,
    items = items.map { it.toDomain() },
)

fun OrderItemDto.toDomain() = OrderItem(
    id = id,
    nombre = nombre,
    unitPrice = unitPrice,
    quantity = quantity,
)
