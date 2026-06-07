package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.data.Local
import com.qless.data.LocalesRepository
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

    private val repository = LocalesRepository()

    private val _uiState = MutableStateFlow(MisLocalesUiState())
    val uiState: StateFlow<MisLocalesUiState> = _uiState.asStateFlow()

    init {
        loadLocales()
    }

    fun loadLocales() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getLocales()
                .onSuccess { locales ->
                    _uiState.update { it.copy(isLoading = false, locales = locales) }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, error = err.message) }
                }
        }
    }
}
