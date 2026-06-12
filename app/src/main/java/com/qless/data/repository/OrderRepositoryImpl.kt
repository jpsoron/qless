package com.qless.data.repository

import com.qless.data.remote.OrderRemoteDataSource
import com.qless.domain.model.CartItem
import com.qless.domain.model.Order
import com.qless.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteDataSource: OrderRemoteDataSource = OrderRemoteDataSource(),
) : OrderRepository {

    override suspend fun createOrder(items: List<CartItem>, localId: String): Result<Order> =
        remoteDataSource.createOrder(items, localId)

    override suspend fun getOrdersByUser(): Result<List<Order>> =
        remoteDataSource.getOrdersByUser()

    override suspend fun getActiveOrdersForLocal(): Result<List<Order>> =
        remoteDataSource.getActiveOrdersForLocal()

    override suspend fun getCompletedOrdersForLocal(): Result<List<Order>> =
        remoteDataSource.getCompletedOrdersForLocal()

    override suspend fun updateStatus(orderId: String, status: String): Result<Unit> =
        remoteDataSource.updateStatus(orderId, status)
}
