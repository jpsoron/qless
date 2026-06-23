package com.qless.domain.repository

import com.qless.domain.model.CartItem
import com.qless.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun createOrder(items: List<CartItem>, localId: String): Result<Order>
    suspend fun getOrdersByUser(): Result<List<Order>>
    suspend fun getActiveOrdersForLocal(): Result<List<Order>>
    suspend fun getCompletedOrdersForLocal(): Result<List<Order>>
    suspend fun updateStatus(orderId: String, status: String): Result<Unit>

    /** Señal de cambios en vivo (Realtime) de los pedidos del usuario logueado. */
    fun observeUserOrderChanges(): Flow<Unit>

    /** Señal de cambios en vivo (Realtime) de los pedidos del local del BackOffice. */
    fun observeLocalOrderChanges(): Flow<Unit>
}
