package com.qless.domain.repository

import com.qless.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getItems(): Flow<List<CartItem>>
    suspend fun addItem(
        emoji: String,
        name: String,
        detail: String,
        unitPrice: Int,
        currentQuantity: Int,
        menuItemId: String,
        localId: String,
    )
    suspend fun updateQuantity(item: CartItem, newQuantity: Int)
    suspend fun clearCart()
}
