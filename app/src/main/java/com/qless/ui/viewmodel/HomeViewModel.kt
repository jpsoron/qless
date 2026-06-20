package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.Local
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val favoritos: List<Local> = emptyList(),
    val closestLocal: Local? = null,
)

class HomeViewModel : ViewModel() {

    private val getFavoritosUseCase = AppModule.getFavoritos
    private val getLocalesUseCase = AppModule.getLocales

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadFavoritos(ids: List<String>) {
        if (ids.isEmpty()) {
            _uiState.update { it.copy(isLoading = false, favoritos = emptyList()) }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getFavoritosUseCase(ids)
                .onSuccess { locales -> _uiState.update { it.copy(isLoading = false, favoritos = locales) } }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        viewModelScope.launch {
            getLocalesUseCase().onSuccess { locales ->
                val closest = locales.map { local ->
                    val distance = calculateDistance(lat, lng, local.latitud, local.longitud)
                    local.copy(distanciaMetros = distance)
                }.minByOrNull { it.distanciaMetros ?: Double.MAX_VALUE }

                _uiState.update { it.copy(closestLocal = closest) }
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0].toDouble()
    }
}
