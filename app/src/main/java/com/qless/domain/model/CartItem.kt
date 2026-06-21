package com.qless.domain.model

data class CartItem(
    val emoji: String,
    val name: String,
    val detail: String,
    val unitPrice: Int,
    val quantity: Int,
    val menuItemId: String = "",
    val localId: String = "",
)
