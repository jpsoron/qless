package com.qless.domain.model

/**
 * Usuario autenticado, tal como lo expone la capa de dominio al resto de la app.
 * (Antes vivía en la capa de datos como `RemoteUser`.)
 */
data class AuthUser(
    val name: String,
    val email: String,
    val role: String,
    val favoritos: List<String> = emptyList(),
    /** true si todavía no usó el descuento de bienvenida (primer pedido). */
    val firstOrderDiscount: Boolean = false,
)
