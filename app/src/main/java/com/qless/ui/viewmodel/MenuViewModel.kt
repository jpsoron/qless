package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MenuUiState(
    val isLoading: Boolean = true,
    val items: List<MenuItem> = emptyList(),
    val selectedCategory: String = "",
    val error: String? = null,
    val isOffline: Boolean = false,
)

class MenuViewModel : ViewModel() {

    private val getMenuUseCase = AppModule.getMenu
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    fun loadMenu(localId: String) {
        if (localId.isEmpty()) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            getMenuUseCase(localId)
                .onSuccess { result ->
                    val items = result.data
                    val initialCategory = if (items.any { it.esPopular }) "🔥 Popular"
                                          else items.map { it.categoria }.firstOrNull() ?: ""
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = items,
                            selectedCategory = initialCategory,
                            isOffline = result.fromCache,
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, error = err.message) }
                }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
