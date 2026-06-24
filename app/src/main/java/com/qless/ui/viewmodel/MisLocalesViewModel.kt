package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.Local
import com.qless.domain.usecase.GetCurrentLocationUseCase
import com.qless.domain.usecase.GetLocalesUseCase
import com.qless.domain.usecase.RankLocalsByDistanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MisLocalesUiState(
    val isLoading: Boolean = true,
    val locales: List<Local> = emptyList(),
    val closestLocal: Local? = null,
    val error: String? = null,
    val isOffline: Boolean = false,
    /** True mientras se resuelve el GPS para ubicar el local más cercano. */
    val isLocating: Boolean = false,
)

class MisLocalesViewModel(
    private val getLocalesUseCase: GetLocalesUseCase,
    private val getCurrentLocation: GetCurrentLocationUseCase,
    private val rankLocalsByDistance: RankLocalsByDistanceUseCase,
) : ViewModel() {

    /** Constructor sin args para `viewModel()` en producción: toma el grafo de [AppModule]. */
    constructor() : this(
        AppModule.getLocales,
        AppModule.getCurrentLocation,
        AppModule.rankLocalsByDistance,
    )

    private val _uiState = MutableStateFlow(MisLocalesUiState())
    val uiState: StateFlow<MisLocalesUiState> = _uiState.asStateFlow()

    init {
        loadLocales()
    }

    fun loadLocales() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            getLocalesUseCase()
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(isLoading = false, locales = result.data, isOffline = result.fromCache)
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, error = err.message) }
                }
        }
    }

    /**
     * Obtiene la ubicación del dispositivo, calcula la distancia a cada local y
     * reordena la lista por cercanía. Llamar cuando ya se tiene permiso.
     */
    fun refreshNearestLocal() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocating = true) }
            val coords = getCurrentLocation()
            if (coords == null) {
                _uiState.update { it.copy(isLocating = false) }
                return@launch
            }
            val ranked = rankLocalsByDistance(coords.lat, coords.lng, _uiState.value.locales)
            _uiState.update {
                it.copy(
                    locales = ranked,
                    closestLocal = ranked.firstOrNull { local -> local.distanciaMetros != null },
                    isLocating = false,
                )
            }
        }
    }
}
