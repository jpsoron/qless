package com.qless.data

import com.qless.data.remote.OrderRemoteDataSource

class OrderRepository {

    private val remoteDataSource = OrderRemoteDataSource()

    suspend fun createOrder(items: List<CartItem>, localId: String): Result<Order> =
        remoteDataSource.createOrder(items, localId)

    suspend fun getOrdersByUser(): Result<List<Order>> =
        remoteDataSource.getOrdersByUser()

    suspend fun getActiveOrdersForLocal(): Result<List<Order>> =
        remoteDataSource.getActiveOrdersForLocal()

    suspend fun getCompletedOrdersForLocal(): Result<List<Order>> =
        remoteDataSource.getCompletedOrdersForLocal()

    suspend fun updateStatus(orderId: String, status: String): Result<Unit> =
        remoteDataSource.updateStatus(orderId, status)
}
