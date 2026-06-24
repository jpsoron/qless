package com.qless.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Perfil(
    val id: String,
    val nombre: String,
    val email: String,
    val rol: String,
    @SerialName("favoritos") val favoritos: List<String> = emptyList(),
    // true mientras el usuario no haya usado el 10% de bienvenida. Pasa a false
    // tras su primer pedido. Default true por compatibilidad con perfiles viejos.
    @SerialName("descuento_1ra") val descuento1ra: Boolean = true,
)
