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

data class MisLocalesUiState(
    val isLoading: Boolean = true,
    val locales: List<Local> = emptyList(),
    val error: String? = null,
)

class MisLocalesViewModel : ViewModel() {

    private val getLocalesUseCase = AppModule.getLocales

    private val _uiState = MutableStateFlow(MisLocalesUiState())
    val uiState: StateFlow<MisLocalesUiState> = _uiState.asStateFlow()

    init {
        loadLocales()
    }

    fun loadLocales() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            getLocalesUseCase()
                .onSuccess { locales ->
                    _uiState.update { it.copy(isLoading = false, locales = locales) }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, error = err.message) }
                }
        }
    }
}
