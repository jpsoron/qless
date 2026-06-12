package com.qless.domain.model

data class MenuItem(
    val id: String,
    val localId: String,
    val emoji: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String,
    val esPopular: Boolean = false,
    val disponible: Boolean = true,
)
