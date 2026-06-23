package com.qless.domain.model

data class PaymentMethod(
    val id: String,
    val tipo: String,            // "VISA", "MC", "AMEX", "MP", "MODO", etc.
    val nombre: String,          // nombre en la tarjeta
    val ultimosDigitos: String,  // últimos 4 dígitos del número
    val vencimiento: String,
    val esPrincipal: Boolean,
    val esBilletera: Boolean,    // false = tarjeta, true = billetera digital
)
