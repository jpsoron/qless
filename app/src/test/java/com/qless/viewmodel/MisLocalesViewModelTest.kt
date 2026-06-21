package com.qless.viewmodel

import com.qless.domain.model.CachedResult
import com.qless.domain.usecase.GetLocalesUseCase
import com.qless.fakes.FakeLocalesRepository
import com.qless.fakes.sampleLocal
import com.qless.ui.viewmodel.MisLocalesViewModel
import com.qless.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MisLocalesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildVm(repo: FakeLocalesRepository) =
        MisLocalesViewModel(GetLocalesUseCase(repo))

    @Test
    fun `carga inicial exitosa desde la red marca isOffline false`() {
        val repo = FakeLocalesRepository(
            localesResult = Result.success(CachedResult(listOf(sampleLocal()), fromCache = false))
        )
        val vm = buildVm(repo)

        assertEquals(1, vm.uiState.value.locales.size)
        assertFalse(vm.uiState.value.isOffline)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `datos servidos desde cache marcan isOffline true`() {
        val repo = FakeLocalesRepository(
            localesResult = Result.success(CachedResult(listOf(sampleLocal()), fromCache = true))
        )
        val vm = buildVm(repo)

        assertTrue(vm.uiState.value.isOffline)
    }

    @Test
    fun `fallo de carga setea error`() {
        val repo = FakeLocalesRepository(
            localesResult = Result.failure(RuntimeException("sin red ni cache"))
        )
        val vm = buildVm(repo)

        assertEquals("sin red ni cache", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }
}
