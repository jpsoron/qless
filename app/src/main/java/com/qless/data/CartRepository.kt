package com.qless.data

import com.qless.data.local.dao.CartItemDao
import com.qless.data.local.entity.CartItemEntity
import com.qless.data.local.entity.toDomain
import com.qless.data.local.entity.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepository(private val dao: CartItemDao) {

    fun getItems(): Flow<List<CartItem>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun addItem(emoji: String, name: String, detail: String, unitPrice: Int, currentQuantity: Int, localId: String) {
        dao.upsert(
            CartItemEntity(
                name = name,
                emoji = emoji,
                detail = detail,
                unitPrice = unitPrice,
                quantity = currentQuantity + 1,
                localId = localId,
            )
        )
    }

    suspend fun updateQuantity(item: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            dao.deleteByName(item.name)
        } else {
            dao.upsert(item.copy(quantity = newQuantity).toEntity())
        }
    }

    suspend fun clearCart() = dao.deleteAll()
}
