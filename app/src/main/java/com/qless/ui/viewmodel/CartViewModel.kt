package com.qless.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qless.data.CartItem
import com.qless.data.CartRepository
import com.qless.data.local.QLessDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
)

class CartViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = CartRepository(
        dao = QLessDatabase.getInstance(app).cartItemDao()
    )

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getItems().collect { list ->
                _uiState.update { it.copy(items = list) }
            }
        }
    }

    fun addItem(emoji: String, name: String, detail: String, unitPrice: Int) {
        val current = _uiState.value.items.find { it.name == name }?.quantity ?: 0
        viewModelScope.launch { repository.addItem(emoji, name, detail, unitPrice, current) }
    }

    fun removeItem(name: String) {
        val item = _uiState.value.items.find { it.name == name } ?: return
        viewModelScope.launch { repository.updateQuantity(item, item.quantity - 1) }
    }

    fun getQuantity(name: String): Int = _uiState.value.items.find { it.name == name }?.quantity ?: 0

    fun clearCart() {
        viewModelScope.launch { repository.clearCart() }
    }
}
