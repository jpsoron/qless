package com.qless.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class CartRepository(context: Context) {
    private val prefs = context.getSharedPreferences("qless_cart", Context.MODE_PRIVATE)

    fun loadCart(): List<CartItem> {
        val json = prefs.getString("items", null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                CartItem(
                    emoji = obj.getString("emoji"),
                    name = obj.getString("name"),
                    detail = obj.getString("detail"),
                    unitPrice = obj.getInt("unitPrice"),
                    quantity = obj.getInt("quantity"),
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveCart(items: List<CartItem>) {
        val array = JSONArray()
        items.forEach { item ->
            array.put(JSONObject().apply {
                put("emoji", item.emoji)
                put("name", item.name)
                put("detail", item.detail)
                put("unitPrice", item.unitPrice)
                put("quantity", item.quantity)
            })
        }
        prefs.edit().putString("items", array.toString()).apply()
    }

    fun clearCart() {
        prefs.edit().remove("items").apply()
    }
}
