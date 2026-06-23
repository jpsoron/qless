package com.qless.data.repository

import com.qless.data.local.dao.CartItemDao
import com.qless.data.local.entity.CartItemEntity
import com.qless.data.local.entity.toDomain
import com.qless.data.local.entity.toEntity
import com.qless.domain.model.CartItem
import com.qless.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(private val dao: CartItemDao) : CartRepository {

    override fun getItems(): Flow<List<CartItem>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun addItem(
        emoji: String,
        name: String,
        detail: String,
        unitPrice: Int,
        currentQuantity: Int,
        menuItemId: String,
        localId: String,
    ) {
        dao.upsert(
            CartItemEntity(
                name = name,
                emoji = emoji,
                detail = detail,
                unitPrice = unitPrice,
                quantity = currentQuantity + 1,
                menuItemId = menuItemId,
                localId = localId,
            )
        )
    }

    override suspend fun updateQuantity(item: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            dao.deleteByName(item.name)
        } else {
            dao.upsert(item.copy(quantity = newQuantity).toEntity())
        }
    }

    override suspend fun clearCart() = dao.deleteAll()
}
