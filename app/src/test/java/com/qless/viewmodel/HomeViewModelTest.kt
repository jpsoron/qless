package com.qless.viewmodel

import com.qless.domain.model.CachedResult
import com.qless.domain.usecase.GetCurrentLocationUseCase
import com.qless.domain.usecase.GetFavoritosUseCase
import com.qless.domain.usecase.GetLocalesUseCase
import com.qless.domain.usecase.RankLocalsByDistanceUseCase
import com.qless.fakes.FakeLocalesRepository
import com.qless.fakes.FakeLocationProvider
import com.qless.fakes.sampleLocal
import com.qless.ui.viewmodel.HomeViewModel
import com.qless.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildVm(repo: FakeLocalesRepository) =
        HomeViewModel(
            GetFavoritosUseCase(repo),
            GetLocalesUseCase(repo),
            GetCurrentLocationUseCase(FakeLocationProvider()),
            RankLocalsByDistanceUseCase(),
        )

    @Test
    fun `sin favoritos no llama al caso de uso y limpia el estado`() {
        val repo = FakeLocalesRepository()
        val vm = buildVm(repo)

        vm.loadFavoritos(emptyList())

        assertTrue(vm.uiState.value.favoritos.isEmpty())
        assertFalse(vm.uiState.value.isOffline)
        assertFalse(vm.uiState.value.isLoading)
        assertNull(repo.lastFavoritosIds)
    }

    @Test
    fun `con favoritos publica la lista y propaga el origen cache`() {
        val repo = FakeLocalesRepository(
            favoritosResult = Result.success(CachedResult(listOf(sampleLocal()), fromCache = true))
        )
        val vm = buildVm(repo)

        vm.loadFavoritos(listOf("l1"))

        assertEquals(1, vm.uiState.value.favoritos.size)
        assertTrue(vm.uiState.value.isOffline)
        assertEquals(listOf("l1"), repo.lastFavoritosIds)
    }
}
