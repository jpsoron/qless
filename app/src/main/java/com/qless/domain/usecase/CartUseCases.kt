package com.qless.domain.usecase

import com.qless.domain.model.CartItem
import com.qless.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class ObserveCartUseCase(private val repository: CartRepository) {
    operator fun invoke(): Flow<List<CartItem>> = repository.getItems()
}

class AddCartItemUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(
        emoji: String,
        name: String,
        detail: String,
        unitPrice: Int,
        currentQuantity: Int,
        menuItemId: String,
        localId: String,
    ) = repository.addItem(emoji, name, detail, unitPrice, currentQuantity, menuItemId, localId)
}

class UpdateCartItemQuantityUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(item: CartItem, newQuantity: Int) =
        repository.updateQuantity(item, newQuantity)
}

class ClearCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke() = repository.clearCart()
}
