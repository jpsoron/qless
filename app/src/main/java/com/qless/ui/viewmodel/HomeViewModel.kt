package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.Local
import com.qless.domain.usecase.GetFavoritosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val favoritos: List<Local> = emptyList(),
    val isOffline: Boolean = false,
)

class HomeViewModel(
    private val getFavoritosUseCase: GetFavoritosUseCase,
) : ViewModel() {

    /** Constructor sin args para `viewModel()` en producción: toma el grafo de [AppModule]. */
    constructor() : this(AppModule.getFavoritos)

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
}
