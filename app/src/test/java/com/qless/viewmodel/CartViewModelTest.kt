package com.qless.viewmodel

import com.qless.domain.model.CartItem
import com.qless.domain.usecase.AddCartItemUseCase
import com.qless.domain.usecase.ClearCartUseCase
import com.qless.domain.usecase.ObserveCartUseCase
import com.qless.domain.usecase.UpdateCartItemQuantityUseCase
import com.qless.fakes.FakeCartRepository
import com.qless.ui.viewmodel.CartViewModel
import com.qless.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildVm(repo: FakeCartRepository) = CartViewModel(
        observeCartUseCase = ObserveCartUseCase(repo),
        addCartItemUseCase = AddCartItemUseCase(repo),
        updateCartItemQuantityUseCase = UpdateCartItemQuantityUseCase(repo),
        clearCartUseCase = ClearCartUseCase(repo),
    )

    @Test
    fun `observa el contenido inicial del carrito`() {
        val repo = FakeCartRepository(
            initial = listOf(CartItem("🍔", "Burger", "d", 500, 2, "m1", "l1"))
        )
        val vm = buildVm(repo)

        assertEquals(1, vm.uiState.value.items.size)
        assertEquals(2, vm.getQuantity("Burger"))
    }

    @Test
    fun `addItem incrementa la cantidad del producto`() {
        val vm = buildVm(FakeCartRepository())

        vm.addItem("🍔", "Burger", "d", 500, "m1", "l1")
        assertEquals(1, vm.getQuantity("Burger"))

        vm.addItem("🍔", "Burger", "d", 500, "m1", "l1")
        assertEquals(2, vm.getQuantity("Burger"))
    }

    @Test
    fun `cartLocalId refleja el local del carrito`() {
        val vm = buildVm(FakeCartRepository())
        assertEquals("", vm.cartLocalId)

        vm.addItem("🍔", "Burger", "d", 500, "m1", "l99")
        assertEquals("l99", vm.cartLocalId)
    }

    @Test
    fun `removeItem baja la cantidad y elimina al llegar a cero`() {
        val repo = FakeCartRepository(
            initial = listOf(CartItem("🍔", "Burger", "d", 500, 1, "m1", "l1"))
        )
        val vm = buildVm(repo)

        vm.removeItem("Burger")

        assertTrue(vm.uiState.value.items.none { it.name == "Burger" })
    }

    @Test
    fun `clearCart vacia el carrito`() {
        val repo = FakeCartRepository(
            initial = listOf(CartItem("🍔", "Burger", "d", 500, 1, "m1", "l1"))
        )
        val vm = buildVm(repo)

        vm.clearCart()

        assertTrue(vm.uiState.value.items.isEmpty())
        assertEquals(1, repo.clearCount)
    }
}
