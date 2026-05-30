package com.qless.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class PaymentMethodRepository(context: Context) {
    private val prefs = context.getSharedPreferences("qless_payment_methods", Context.MODE_PRIVATE)

    fun loadMethods(): List<PaymentMethod> {
        val json = prefs.getString("methods", null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                PaymentMethod(
                    id = obj.getString("id"),
                    tipo = obj.getString("tipo"),
                    nombre = obj.getString("nombre"),
                    ultimosDigitos = obj.getString("ultimosDigitos"),
                    vencimiento = obj.getString("vencimiento"),
                    esPrincipal = obj.getBoolean("esPrincipal"),
                    esBilletera = obj.getBoolean("esBilletera"),
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveMethods(methods: List<PaymentMethod>) {
        val array = JSONArray()
        methods.forEach { m ->
            array.put(JSONObject().apply {
                put("id", m.id)
                put("tipo", m.tipo)
                put("nombre", m.nombre)
                put("ultimosDigitos", m.ultimosDigitos)
                put("vencimiento", m.vencimiento)
                put("esPrincipal", m.esPrincipal)
                put("esBilletera", m.esBilletera)
            })
        }
        prefs.edit().putString("methods", array.toString()).apply()
    }
}
