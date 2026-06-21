package com.qless.viewmodel

import com.qless.domain.model.CachedResult
import com.qless.domain.usecase.GetMenuUseCase
import com.qless.fakes.FakeMenuRepository
import com.qless.fakes.sampleMenuItem
import com.qless.ui.viewmodel.MenuViewModel
import com.qless.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildVm(repo: FakeMenuRepository) = MenuViewModel(GetMenuUseCase(repo))

    @Test
    fun `loadMenu con localId vacio no llama al caso de uso`() {
        val repo = FakeMenuRepository()
        val vm = buildVm(repo)

        vm.loadMenu("")

        assertNull(repo.lastLocalId)
    }

    @Test
    fun `loadMenu selecciona Popular cuando hay items populares`() {
        val repo = FakeMenuRepository(
            menuResult = Result.success(
                CachedResult(
                    listOf(
                        sampleMenuItem(id = "a", categoria = "Clásicas"),
                        sampleMenuItem(id = "b", categoria = "Especiales", esPopular = true),
                    ),
                    fromCache = false,
                )
            )
        )
        val vm = buildVm(repo)

        vm.loadMenu("l1")

        assertEquals("🔥 Popular", vm.uiState.value.selectedCategory)
        assertEquals(2, vm.uiState.value.items.size)
        assertFalse(vm.uiState.value.isOffline)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `loadMenu sin populares usa la primera categoria y propaga cache`() {
        val repo = FakeMenuRepository(
            menuResult = Result.success(
                CachedResult(listOf(sampleMenuItem(categoria = "Pizzas")), fromCache = true)
            )
        )
        val vm = buildVm(repo)

        vm.loadMenu("l1")

        assertEquals("Pizzas", vm.uiState.value.selectedCategory)
        assertTrue(vm.uiState.value.isOffline)
    }

    @Test
    fun `loadMenu fallido setea error`() {
        val repo = FakeMenuRepository(
            menuResult = Result.failure(RuntimeException("menu no disponible"))
        )
        val vm = buildVm(repo)

        vm.loadMenu("l1")

        assertEquals("menu no disponible", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }
}
