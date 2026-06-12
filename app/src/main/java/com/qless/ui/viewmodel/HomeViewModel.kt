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
)

class HomeViewModel : ViewModel() {

    private val getFavoritosUseCase = AppModule.getFavoritos

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
}
