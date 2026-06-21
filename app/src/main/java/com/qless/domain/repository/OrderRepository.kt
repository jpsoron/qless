package com.qless.domain.repository

import com.qless.domain.model.CartItem
import com.qless.domain.model.Order

interface OrderRepository {
    suspend fun createOrder(items: List<CartItem>, localId: String): Result<Order>
    suspend fun getOrdersByUser(): Result<List<Order>>
    suspend fun getActiveOrdersForLocal(): Result<List<Order>>
    suspend fun getCompletedOrdersForLocal(): Result<List<Order>>
    suspend fun updateStatus(orderId: String, status: String): Result<Unit>
}
