package com.qless.data

data class Order(
    val id: String,
    val numero: Int,
    val userId: String,
    val localId: String,
    val localNombre: String,
    val localEmoji: String,
    val status: String,
    val totalAmount: Int,
    val createdAt: String,
    val items: List<OrderItem> = emptyList(),
)

data class OrderItem(
    val id: String,
    val nombre: String,
    val unitPrice: Int,
    val quantity: Int,
)
