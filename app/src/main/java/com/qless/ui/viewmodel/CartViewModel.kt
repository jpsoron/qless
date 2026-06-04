package com.qless.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qless.data.CartItem
import com.qless.data.CartRepository
import com.qless.data.local.QLessDatabase
import kotlinx.coroutines.launch

class CartViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = CartRepository(
        dao = QLessDatabase.getInstance(app).cartItemDao()
    )

    val items = mutableStateListOf<CartItem>()

    init {
        viewModelScope.launch {
            repository.getItems().collect { list ->
                items.clear()
                items.addAll(list)
            }
        }
    }

    fun addItem(emoji: String, name: String, detail: String, unitPrice: Int) {
        val current = items.find { it.name == name }?.quantity ?: 0
        viewModelScope.launch {
            repository.addItem(emoji, name, detail, unitPrice, current)
        }
    }

    fun removeItem(name: String) {
        val item = items.find { it.name == name } ?: return
        viewModelScope.launch {
            repository.updateQuantity(item, item.quantity - 1)
        }
    }

    fun getQuantity(name: String): Int = items.find { it.name == name }?.quantity ?: 0

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}
