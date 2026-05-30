package com.qless.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.qless.data.CartItem
import com.qless.data.CartRepository

class CartViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = CartRepository(app)
    val items = mutableStateListOf<CartItem>()

    init {
        items.addAll(repository.loadCart())
    }

    fun addItem(emoji: String, name: String, detail: String, unitPrice: Int) {
        val index = items.indexOfFirst { it.name == name }
        if (index >= 0) {
            items[index] = items[index].copy(quantity = items[index].quantity + 1)
        } else {
            items.add(CartItem(emoji, name, detail, unitPrice, 1))
        }
        repository.saveCart(items)
    }

    fun removeItem(name: String) {
        val index = items.indexOfFirst { it.name == name }
        if (index < 0) return
        val item = items[index]
        if (item.quantity > 1) {
            items[index] = item.copy(quantity = item.quantity - 1)
        } else {
            items.removeAt(index)
        }
        repository.saveCart(items)
    }

    fun getQuantity(name: String): Int = items.find { it.name == name }?.quantity ?: 0

    fun clearCart() {
        items.clear()
        repository.clearCart()
    }
}
