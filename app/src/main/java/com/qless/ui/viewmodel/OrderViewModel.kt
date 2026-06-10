package com.qless.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qless.data.CartItem
import com.qless.data.Order
import com.qless.data.OrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class OrderFilter { ACTIVE, COMPLETED, CANCELLED }

enum class BackOfficeFilter { RECEIVED, PREPARING, READY }

data class OrdersUiState(
    val userOrders: List<Order> = emptyList(),
    val localOrders: List<Order> = emptyList(),       // activos: pending, preparing, ready
    val historyOrders: List<Order> = emptyList(),     // completados: picked_up
    val lastCreatedOrder: Order? = null,
    val isLoadingUser: Boolean = false,
    val isLoadingLocal: Boolean = false,
    val isLoadingHistory: Boolean = false,
    val isCheckingOut: Boolean = false,
    val userFilter: OrderFilter = OrderFilter.ACTIVE,
    val localFilter: BackOfficeFilter = BackOfficeFilter.RECEIVED,
    val error: String? = null,
)

sealed interface OrderNavEvent {
    data object CheckoutSuccess : OrderNavEvent
    data class CheckoutError(val message: String) : OrderNavEvent
}

private val ACTIVE_STATUSES = setOf("pending", "preparing", "ready")

class OrderViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = OrderRepository()

    private var pollingJob: Job? = null

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<OrderNavEvent>()
    val navEvent: SharedFlow<OrderNavEvent> = _navEvent.asSharedFlow()

    fun filteredUserOrders(): List<Order> {
        val orders = _uiState.value.userOrders
        return when (_uiState.value.userFilter) {
            OrderFilter.ACTIVE -> orders.filter { it.status in ACTIVE_STATUSES }
            OrderFilter.COMPLETED -> orders.filter { it.status == "picked_up" }
            OrderFilter.CANCELLED -> orders.filter { it.status == "cancelled" }
        }
    }

    fun filteredLocalOrders(): List<Order> {
        val orders = _uiState.value.localOrders
        return when (_uiState.value.localFilter) {
            BackOfficeFilter.RECEIVED  -> orders.filter { it.status == "pending" }
            BackOfficeFilter.PREPARING -> orders.filter { it.status == "preparing" }
            BackOfficeFilter.READY     -> orders.filter { it.status == "ready" }
        }
    }

    fun loadUserOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true, error = null) }
            repository.getOrdersByUser()
                .onSuccess { orders -> _uiState.update { it.copy(userOrders = orders, isLoadingUser = false) } }
                .onFailure { err -> _uiState.update { it.copy(isLoadingUser = false, error = err.message) } }
        }
    }

    fun loadLocalOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocal = true, error = null) }
            repository.getActiveOrdersForLocal()
                .onSuccess { orders -> _uiState.update { it.copy(localOrders = orders, isLoadingLocal = false) } }
                .onFailure { err -> _uiState.update { it.copy(isLoadingLocal = false, error = err.message) } }
        }
    }

    fun loadOrderHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true, error = null) }
            repository.getCompletedOrdersForLocal()
                .onSuccess { orders -> _uiState.update { it.copy(historyOrders = orders, isLoadingHistory = false) } }
                .onFailure { err -> _uiState.update { it.copy(isLoadingHistory = false, error = err.message) } }
        }
    }

    fun placeOrder(items: List<CartItem>, localId: String) {
        if (items.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingOut = true) }
            repository.createOrder(items, localId)
                .onSuccess { order ->
                    _uiState.update { it.copy(isCheckingOut = false, lastCreatedOrder = order) }
                    _navEvent.emit(OrderNavEvent.CheckoutSuccess)
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isCheckingOut = false) }
                    _navEvent.emit(OrderNavEvent.CheckoutError(err.message ?: "Error al procesar el pedido"))
                }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateStatus(orderId, newStatus)
                .onSuccess { loadLocalOrders() }
        }
    }

    fun setUserFilter(filter: OrderFilter) {
        _uiState.update { it.copy(userFilter = filter) }
    }

    fun setLocalFilter(filter: BackOfficeFilter) {
        _uiState.update { it.copy(localFilter = filter) }
    }

    /** Inicia polling cada 10 s para actualizar el estado del pedido activo en tiempo real. */
    fun startOrderPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(10_000L)
                repository.getOrdersByUser()
                    .onSuccess { orders -> _uiState.update { it.copy(userOrders = orders) } }
            }
        }
    }

    /** Detiene el polling. Llamar cuando la pantalla de seguimiento sale de pantalla. */
    fun stopOrderPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }
}
