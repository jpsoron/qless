package com.qless.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.qless.data.PaymentMethod
import com.qless.data.PaymentMethodRepository
import java.util.UUID

class PaymentMethodViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = PaymentMethodRepository(app)
    val methods = mutableStateListOf<PaymentMethod>()

    init {
        val saved = repository.loadMethods()
        if (saved.isEmpty()) {
            val defaults = listOf(
                PaymentMethod(
                    id = "default-visa",
                    tipo = "VISA",
                    nombre = "María González",
                    ultimosDigitos = "4242",
                    vencimiento = "08/29",
                    esPrincipal = true,
                    esBilletera = false,
                ),
                PaymentMethod(
                    id = "default-mp",
                    tipo = "MP",
                    nombre = "Mercado Pago",
                    ultimosDigitos = "",
                    vencimiento = "",
                    esPrincipal = false,
                    esBilletera = true,
                ),
                PaymentMethod(
                    id = "default-mc",
                    tipo = "MC",
                    nombre = "María González",
                    ultimosDigitos = "1034",
                    vencimiento = "03/28",
                    esPrincipal = false,
                    esBilletera = false,
                ),
            )
            methods.addAll(defaults)
            repository.saveMethods(methods)
        } else {
            methods.addAll(saved)
        }
    }

    fun addMethod(
        nombre: String,
        numero: String,
        vencimiento: String,
        esPrincipal: Boolean,
        esBilletera: Boolean,
    ) {
        val sanitized = numero.filter { it.isDigit() }
        val ultimosDigitos = if (sanitized.length >= 4) sanitized.takeLast(4) else sanitized

        val tipo = when {
            esBilletera -> "MP"
            sanitized.startsWith("4") -> "VISA"
            sanitized.startsWith("5") -> "MC"
            sanitized.startsWith("3") -> "AMEX"
            else -> "CARD"
        }

        // Si el nuevo es principal, quitar ese flag al resto
        if (esPrincipal) {
            val updated = methods.mapIndexed { _, m -> m.copy(esPrincipal = false) }
            methods.clear()
            methods.addAll(updated)
        }

        methods.add(
            PaymentMethod(
                id = UUID.randomUUID().toString(),
                tipo = tipo,
                nombre = nombre,
                ultimosDigitos = ultimosDigitos,
                vencimiento = vencimiento,
                esPrincipal = esPrincipal,
                esBilletera = esBilletera,
            )
        )
        repository.saveMethods(methods)
    }

    fun removeMethod(id: String) {
        methods.removeIf { it.id == id }
        repository.saveMethods(methods)
    }
}
