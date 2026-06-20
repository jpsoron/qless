package com.qless.domain.model

data class Local(
    val id: String,
    val emoji: String,
    val nombre: String,
    val categoria: String,
    val barrio: String,
    val rating: String,
    val tiempoEntrega: String?,
    val abierto: Boolean,
    val tienePromo: Boolean,
    val destacado: Boolean,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val distanciaMetros: Double? = null,
)
