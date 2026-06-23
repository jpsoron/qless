package com.qless.domain.usecase

import com.qless.domain.model.CartItem
import com.qless.domain.model.Order
import com.qless.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

/**
 * Casos de uso del dominio principal (pedidos). Orquestan reglas de negocio
 * sobre el contrato [OrderRepository] sin conocer de dónde salen los datos.
 */

class PlaceOrderUseCase(private val repository: OrderRepository) {
    /** Regla de negocio: no se puede crear un pedido sin ítems. */
    suspend operator fun invoke(items: List<CartItem>, localId: String): Result<Order> {
        if (items.isEmpty()) {
            return Result.failure(IllegalArgumentException("El carrito está vacío"))
        }
        return repository.createOrder(items, localId)
    }
}

class GetUserOrdersUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(): Result<List<Order>> = repository.getOrdersByUser()
}

class GetActiveLocalOrdersUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(): Result<List<Order>> = repository.getActiveOrdersForLocal()
}

class GetCompletedLocalOrdersUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(): Result<List<Order>> = repository.getCompletedOrdersForLocal()
}

class UpdateOrderStatusUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(orderId: String, status: String): Result<Unit> =
        repository.updateStatus(orderId, status)
}

/** Señal en vivo: emite cada vez que conviene recargar los pedidos del usuario. */
class ObserveUserOrderChangesUseCase(private val repository: OrderRepository) {
    operator fun invoke(): Flow<Unit> = repository.observeUserOrderChanges()
}

/** Señal en vivo: emite cada vez que conviene recargar los pedidos del local. */
class ObserveLocalOrderChangesUseCase(private val repository: OrderRepository) {
    operator fun invoke(): Flow<Unit> = repository.observeLocalOrderChanges()
}
