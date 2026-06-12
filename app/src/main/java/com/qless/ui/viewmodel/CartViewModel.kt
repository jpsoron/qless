package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
)

class CartViewModel : ViewModel() {

    private val observeCartUseCase = AppModule.observeCart
    private val addCartItemUseCase = AppModule.addCartItem
    private val updateCartItemQuantityUseCase = AppModule.updateCartItemQuantity
    private val clearCartUseCase = AppModule.clearCart

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCartUseCase().collect { list ->
                _uiState.update { it.copy(items = list) }
            }
        }
    }

    fun addItem(emoji: String, name: String, detail: String, unitPrice: Int, menuItemId: String = "", localId: String = "") {
        val current = _uiState.value.items.find { it.name == name }?.quantity ?: 0
        viewModelScope.launch { addCartItemUseCase(emoji, name, detail, unitPrice, current, menuItemId, localId) }
    }

    val cartLocalId: String get() = _uiState.value.items.firstOrNull()?.localId ?: ""

    fun removeItem(name: String) {
        val item = _uiState.value.items.find { it.name == name } ?: return
        viewModelScope.launch { updateCartItemQuantityUseCase(item, item.quantity - 1) }
    }

    fun getQuantity(name: String): Int = _uiState.value.items.find { it.name == name }?.quantity ?: 0

    fun clearCart() {
        viewModelScope.launch { clearCartUseCase() }
    }
}
