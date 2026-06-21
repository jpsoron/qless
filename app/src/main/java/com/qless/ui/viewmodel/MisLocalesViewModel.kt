package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.Local
import com.qless.domain.usecase.GetLocalesUseCase
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
)

class MisLocalesViewModel(
    private val getLocalesUseCase: GetLocalesUseCase,
) : ViewModel() {

    /** Constructor sin args para `viewModel()` en producción: toma el grafo de [AppModule]. */
    constructor() : this(AppModule.getLocales)

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

    fun updateUserLocation(lat: Double, lng: Double) {
        val currentLocales = _uiState.value.locales
        if (currentLocales.isEmpty()) return

        val updatedLocales = currentLocales.map { local ->
            val distance = calculateDistance(lat, lng, local.latitud, local.longitud)
            local.copy(distanciaMetros = distance)
        }.sortedBy { it.distanciaMetros }

        _uiState.update { it.copy(
            locales = updatedLocales,
            closestLocal = updatedLocales.firstOrNull()
        )}
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0].toDouble()
    }
}
