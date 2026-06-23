package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.CartItem
import com.qless.domain.model.Order
import com.qless.domain.usecase.GetActiveLocalOrdersUseCase
import com.qless.domain.usecase.GetCompletedLocalOrdersUseCase
import com.qless.domain.usecase.GetUserOrdersUseCase
import com.qless.domain.usecase.NotifyOrderUpdateUseCase
import com.qless.domain.usecase.ObserveLocalOrderChangesUseCase
import com.qless.domain.usecase.ObserveUserOrderChangesUseCase
import com.qless.domain.usecase.PlaceOrderUseCase
import com.qless.domain.usecase.UpdateOrderStatusUseCase
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
    val selectedOrder: Order? = null,
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

/** Estados que cuentan como "pedido en curso" (no finalizado ni cancelado). */
val ACTIVE_ORDER_STATUSES = setOf("pending", "preparing", "ready")

/** Fuente única de verdad del pedido activo del usuario. */
fun OrdersUiState.activeOrder(): Order? =
    userOrders.firstOrNull { it.status in ACTIVE_ORDER_STATUSES }

class OrderViewModel(
    private val getUserOrders: GetUserOrdersUseCase,
    private val getActiveLocalOrders: GetActiveLocalOrdersUseCase,
    private val getCompletedLocalOrders: GetCompletedLocalOrdersUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val observeUserOrderChanges: ObserveUserOrderChangesUseCase,
    private val observeLocalOrderChanges: ObserveLocalOrderChangesUseCase,
    private val notifyOrderUpdate: NotifyOrderUpdateUseCase,
) : ViewModel() {

    /** Constructor sin args para `viewModel()` en producción: toma el grafo de [AppModule]. */
    constructor() : this(
        AppModule.getUserOrders,
        AppModule.getActiveLocalOrders,
        AppModule.getCompletedLocalOrders,
        AppModule.placeOrder,
        AppModule.updateOrderStatus,
        AppModule.observeUserOrderChanges,
        AppModule.observeLocalOrderChanges,
        AppModule.notifyOrderUpdate,
    )

    // Último estado conocido por pedido, para detectar transiciones y notificar solo
    // cambios reales (no la carga inicial). Vive con la instancia compartida del VM.
    private val lastKnownStatuses = mutableMapOf<String, String>()

    // IDs de pedidos que el cliente confirmó como retirados en esta sesión.
    // Protege el estado optimista contra recargas del server que devuelvan status desactualizado.
    private val locallyPickedUp = mutableSetOf<String>()

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<OrderNavEvent>()
    val navEvent: SharedFlow<OrderNavEvent> = _navEvent.asSharedFlow()

    fun filteredUserOrders(): List<Order> {
        val orders = _uiState.value.userOrders
        return when (_uiState.value.userFilter) {
            OrderFilter.ACTIVE -> orders.filter { it.status in ACTIVE_ORDER_STATUSES }
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
            getUserOrders()
                .onSuccess { orders ->
                    val adjusted = orders.applyLocalPickups()
                    _uiState.update { it.copy(userOrders = adjusted, isLoadingUser = false) }
                }
                .onFailure { err -> _uiState.update { it.copy(isLoadingUser = false, error = err.message) } }
        }
    }

    fun loadLocalOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocal = true, error = null) }
            getActiveLocalOrders()
                .onSuccess { orders -> _uiState.update { it.copy(localOrders = orders, isLoadingLocal = false) } }
                .onFailure { err -> _uiState.update { it.copy(isLoadingLocal = false, error = err.message) } }
        }
    }

    fun loadOrderHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true, error = null) }
            getCompletedLocalOrders()
                .onSuccess { orders -> _uiState.update { it.copy(historyOrders = orders, isLoadingHistory = false) } }
                .onFailure { err -> _uiState.update { it.copy(isLoadingHistory = false, error = err.message) } }
        }
    }

    fun placeOrder(items: List<CartItem>, localId: String) {
        if (items.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingOut = true) }
            placeOrderUseCase(items, localId)
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
            updateOrderStatusUseCase(orderId, newStatus)
                .onSuccess { loadLocalOrders() }
        }
    }

    fun confirmPickup(orderId: String) {
        locallyPickedUp.add(orderId)
        _uiState.update { state ->
            state.copy(
                userOrders = state.userOrders.applyLocalPickups(),
                lastCreatedOrder = state.lastCreatedOrder?.let {
                    if (it.id in locallyPickedUp) it.copy(status = "picked_up") else it
                }
            )
        }
        viewModelScope.launch {
            updateOrderStatusUseCase(orderId, "picked_up")
                .onSuccess {
                    locallyPickedUp.remove(orderId) // server lo confirmó, ya no necesitamos override
                    loadUserOrders()
                }
        }
    }

    fun selectOrder(order: Order) {
        _uiState.update { it.copy(selectedOrder = order) }
    }

    fun setUserFilter(filter: OrderFilter) {
        _uiState.update { it.copy(userFilter = filter) }
    }

    fun setLocalFilter(filter: BackOfficeFilter) {
        _uiState.update { it.copy(localFilter = filter) }
    }

    /**
     * Observa en vivo (Supabase Realtime) los pedidos del usuario y refleja cada cambio
     * en [uiState]. Suspende hasta que se cancele el scope que la invoca: pensada para
     * llamarse desde `repeatOnLifecycle` a nivel app, de modo que el canal viva mientras
     * haya sesión + foreground y se corte (y re-fetchee al volver) al pasar a background.
     */
    suspend fun observeUserOrders() {
        observeUserOrderChanges().collect {
            getUserOrders()
                .onSuccess { orders ->
                    detectAndNotify(orders)
                    _uiState.update { it.copy(userOrders = orders.applyLocalPickups(), isLoadingUser = false) }
                }
                .onFailure { err -> _uiState.update { it.copy(error = err.message) } }
        }
    }

    /**
     * Compara cada pedido contra el último estado conocido y dispara una notificación
     * por cada transición real. No notifica la primera vez que ve un pedido (carga
     * inicial) ni el `picked_up` que el propio cliente confirmó (ya está en [locallyPickedUp]).
     */
    private suspend fun detectAndNotify(orders: List<Order>) {
        for (order in orders) {
            val previous = lastKnownStatuses.put(order.id, order.status)
            if (previous == null || previous == order.status) continue
            if (order.status == "picked_up" && order.id in locallyPickedUp) continue
            notifyOrderUpdate(order)
        }
    }

    /**
     * Observa en vivo los pedidos del local (BackOffice) y refresca tanto los activos
     * como el historial ante cada cambio. Suspende hasta cancelación (ciclo de vida de
     * la pantalla que la invoca).
     */
    suspend fun observeLocalOrders() {
        observeLocalOrderChanges().collect {
            getActiveLocalOrders()
                .onSuccess { orders -> _uiState.update { it.copy(localOrders = orders, isLoadingLocal = false) } }
            getCompletedLocalOrders()
                .onSuccess { orders -> _uiState.update { it.copy(historyOrders = orders, isLoadingHistory = false) } }
        }
    }

    private fun List<Order>.applyLocalPickups(): List<Order> {
        if (locallyPickedUp.isEmpty()) return this
        return map { if (it.id in locallyPickedUp) it.copy(status = "picked_up") else it }
    }
}
