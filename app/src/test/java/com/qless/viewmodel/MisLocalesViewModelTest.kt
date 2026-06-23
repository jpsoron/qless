package com.qless.viewmodel

import com.qless.domain.model.CachedResult
import com.qless.domain.model.Coordinates
import com.qless.domain.usecase.GetCurrentLocationUseCase
import com.qless.domain.usecase.GetLocalesUseCase
import com.qless.domain.usecase.RankLocalsByDistanceUseCase
import com.qless.fakes.FakeLocalesRepository
import com.qless.fakes.FakeLocationProvider
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
        MisLocalesViewModel(
            GetLocalesUseCase(repo),
            GetCurrentLocationUseCase(FakeLocationProvider()),
            RankLocalsByDistanceUseCase(),
        )

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

    @Test
    fun `refreshNearestLocal reordena por cercania y setea closestLocal`() {
        val repo = FakeLocalesRepository(
            localesResult = Result.success(
                CachedResult(
                    listOf(
                        sampleLocal(id = "lejos", latitud = -34.6037, longitud = -58.3816),
                        sampleLocal(id = "cerca", latitud = -34.4720, longitud = -58.5126),
                    ),
                    fromCache = false,
                )
            )
        )
        val provider = FakeLocationProvider(Coordinates(-34.4718, -58.5124))
        val vm = MisLocalesViewModel(
            GetLocalesUseCase(repo),
            GetCurrentLocationUseCase(provider),
            RankLocalsByDistanceUseCase(),
        )

        vm.refreshNearestLocal()

        assertEquals("cerca", vm.uiState.value.locales.first().id)
        assertEquals("cerca", vm.uiState.value.closestLocal?.id)
    }

    @Test
    fun `refreshNearestLocal sin ubicacion no cambia el estado`() {
        val repo = FakeLocalesRepository(
            localesResult = Result.success(CachedResult(listOf(sampleLocal(id = "a")), fromCache = false))
        )
        val provider = FakeLocationProvider(coordinates = null) // sin permiso / sin fix
        val vm = MisLocalesViewModel(
            GetLocalesUseCase(repo),
            GetCurrentLocationUseCase(provider),
            RankLocalsByDistanceUseCase(),
        )

        vm.refreshNearestLocal()

        assertEquals(null, vm.uiState.value.closestLocal)
    }
}
