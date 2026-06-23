package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.Local
import com.qless.domain.usecase.GetCurrentLocationUseCase
import com.qless.domain.usecase.GetFavoritosUseCase
import com.qless.domain.usecase.GetLocalesUseCase
import com.qless.domain.usecase.RankLocalsByDistanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val favoritos: List<Local> = emptyList(),
    val closestLocal: Local? = null,
    val isOffline: Boolean = false,
)

class HomeViewModel(
    private val getFavoritosUseCase: GetFavoritosUseCase,
    private val getLocalesUseCase: GetLocalesUseCase,
    private val getCurrentLocation: GetCurrentLocationUseCase,
    private val rankLocalsByDistance: RankLocalsByDistanceUseCase,
) : ViewModel() {

    /** Constructor sin args para `viewModel()` en producción: toma el grafo de [AppModule]. */
    constructor() : this(
        AppModule.getFavoritos,
        AppModule.getLocales,
        AppModule.getCurrentLocation,
        AppModule.rankLocalsByDistance,
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadFavoritos(ids: List<String>) {
        if (ids.isEmpty()) {
            _uiState.update { it.copy(isLoading = false, favoritos = emptyList(), isOffline = false) }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getFavoritosUseCase(ids)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(isLoading = false, favoritos = result.data, isOffline = result.fromCache)
                    }
                }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    /**
     * Obtiene la ubicación del dispositivo y calcula el local más cercano entre
     * todos los locales. Llamar cuando ya se tiene permiso de ubicación.
     */
    fun refreshNearestLocal() {
        viewModelScope.launch {
            val coords = getCurrentLocation() ?: return@launch
            getLocalesUseCase().onSuccess { result ->
                val ranked = rankLocalsByDistance(coords.lat, coords.lng, result.data)
                _uiState.update {
                    it.copy(closestLocal = ranked.firstOrNull { local -> local.distanciaMetros != null })
                }
            }
        }
    }
}
