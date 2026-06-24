package com.qless.viewmodel

import com.qless.domain.model.CartItem
import com.qless.domain.usecase.GetActiveLocalOrdersUseCase
import com.qless.domain.usecase.GetCompletedLocalOrdersUseCase
import com.qless.domain.usecase.GetUserOrdersUseCase
import com.qless.domain.usecase.NotifyOrderUpdateUseCase
import com.qless.domain.usecase.ObserveLocalOrderChangesUseCase
import com.qless.domain.usecase.ObserveUserOrderChangesUseCase
import com.qless.domain.usecase.PlaceOrderUseCase
import com.qless.domain.usecase.UpdateOrderStatusUseCase
import com.qless.fakes.FakeNotificationPreferencesRepository
import com.qless.fakes.FakeNotificationRepository
import com.qless.fakes.FakeOrderRepository
import com.qless.fakes.FakeSystemNotifier
import com.qless.fakes.sampleOrder
import com.qless.ui.viewmodel.OrderFilter
import com.qless.ui.viewmodel.OrderNavEvent
import com.qless.ui.viewmodel.OrderViewModel
import com.qless.ui.viewmodel.activeOrder
import com.qless.util.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildVm(repo: FakeOrderRepository) = OrderViewModel(
        getUserOrders = GetUserOrdersUseCase(repo),
        getActiveLocalOrders = GetActiveLocalOrdersUseCase(repo),
        getCompletedLocalOrders = GetCompletedLocalOrdersUseCase(repo),
        placeOrderUseCase = PlaceOrderUseCase(repo),
        updateOrderStatusUseCase = UpdateOrderStatusUseCase(repo),
        observeUserOrderChanges = ObserveUserOrderChangesUseCase(repo),
        observeLocalOrderChanges = ObserveLocalOrderChangesUseCase(repo),
        notifyOrderUpdate = NotifyOrderUpdateUseCase(FakeNotificationRepository(), FakeSystemNotifier(), FakeNotificationPreferencesRepository()),
    )

    private fun cartItem(price: Int = 500, qty: Int = 2) =
        CartItem("🍔", "Item", "desc", price, qty, "m1", "l1")

    @Test
    fun `loadUserOrders exitoso publica los pedidos`() {
        val repo = FakeOrderRepository(
            userOrders = Result.success(listOf(sampleOrder(id = "o1")))
        )
        val vm = buildVm(repo)

        vm.loadUserOrders()

        assertEquals(1, vm.uiState.value.userOrders.size)
        assertEquals(false, vm.uiState.value.isLoadingUser)
    }

    @Test
    fun `loadUserOrders con error setea mensaje`() {
        val repo = FakeOrderRepository(
            userOrders = Result.failure(RuntimeException("sin red"))
        )
        val vm = buildVm(repo)

        vm.loadUserOrders()

        assertEquals("sin red", vm.uiState.value.error)
        assertEquals(false, vm.uiState.value.isLoadingUser)
    }

    @Test
    fun `filteredUserOrders separa por estado`() {
        val repo = FakeOrderRepository(
            userOrders = Result.success(
                listOf(
                    sampleOrder(id = "a", status = "preparing"),
                    sampleOrder(id = "b", status = "picked_up"),
                    sampleOrder(id = "c", status = "cancelled"),
                    sampleOrder(id = "d", status = "ready"),
                )
            )
        )
        val vm = buildVm(repo)
        vm.loadUserOrders()

        vm.setUserFilter(OrderFilter.ACTIVE)
        assertEquals(setOf("a", "d"), vm.filteredUserOrders().map { it.id }.toSet())

        vm.setUserFilter(OrderFilter.COMPLETED)
        assertEquals(listOf("b"), vm.filteredUserOrders().map { it.id })

        vm.setUserFilter(OrderFilter.CANCELLED)
        assertEquals(listOf("c"), vm.filteredUserOrders().map { it.id })
    }

    @Test
    fun `activeOrder devuelve el primer pedido en curso`() {
        val repo = FakeOrderRepository(
            userOrders = Result.success(
                listOf(
                    sampleOrder(id = "done", status = "picked_up"),
                    sampleOrder(id = "live", status = "preparing"),
                )
            )
        )
        val vm = buildVm(repo)
        vm.loadUserOrders()

        assertEquals("live", vm.uiState.value.activeOrder()?.id)
    }

    @Test
    fun `placeOrder con carrito vacio no crea pedido`() {
        val repo = FakeOrderRepository()
        val vm = buildVm(repo)

        vm.placeOrder(emptyList(), "l1")

        assertTrue(repo.createdOrders.isEmpty())
        assertNull(vm.uiState.value.lastCreatedOrder)
    }

    @Test
    fun `placeOrder exitoso guarda lastCreatedOrder y emite CheckoutSuccess`() {
        val repo = FakeOrderRepository()
        val vm = buildVm(repo)
        val events = collectEvents(vm)

        vm.placeOrder(listOf(cartItem()), "l1")

        assertEquals(1, repo.createdOrders.size)
        assertNotNull(vm.uiState.value.lastCreatedOrder)
        assertTrue(events.any { it is OrderNavEvent.CheckoutSuccess })
    }

    @Test
    fun `placeOrder fallido emite CheckoutError`() {
        val repo = FakeOrderRepository(
            createResult = { _, _ -> Result.failure(RuntimeException("pago rechazado")) }
        )
        val vm = buildVm(repo)
        val events = collectEvents(vm)

        vm.placeOrder(listOf(cartItem()), "l1")

        val error = events.filterIsInstance<OrderNavEvent.CheckoutError>().firstOrNull()
        assertNotNull(error)
        assertEquals("pago rechazado", error!!.message)
    }

    @Test
    fun `confirmPickup actualiza el estado a picked_up`() {
        val repo = FakeOrderRepository(
            userOrders = Result.success(listOf(sampleOrder(id = "o1", status = "ready")))
        )
        val vm = buildVm(repo)
        vm.loadUserOrders()

        vm.confirmPickup("o1")

        assertTrue(repo.statusUpdates.contains("o1" to "picked_up"))
    }

    @Test
    fun `updateOrderStatus delega en el repo y recarga pedidos del local`() {
        val repo = FakeOrderRepository(
            activeLocalOrders = Result.success(listOf(sampleOrder(id = "x", status = "preparing")))
        )
        val vm = buildVm(repo)

        vm.updateOrderStatus("x", "ready")

        assertTrue(repo.statusUpdates.contains("x" to "ready"))
        assertEquals(1, vm.uiState.value.localOrders.size)
    }

    /** Recolecta el SharedFlow de navegación de forma ansiosa (Unconfined). */
    private fun collectEvents(vm: OrderViewModel): List<OrderNavEvent> {
        val events = mutableListOf<OrderNavEvent>()
        CoroutineScope(Dispatchers.Unconfined).launch { vm.navEvent.collect { events += it } }
        return events
    }
}
