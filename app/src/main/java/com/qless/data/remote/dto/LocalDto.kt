package com.qless.data.remote.dto

import com.qless.domain.model.Local
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalDto(
    val id: String,
    val nombre: String,
    val emoji: String,
    val categoria: String,
    val barrio: String,
    val direccion: String,
    val rating: Double,
    @SerialName("tiempo_entrega") val tiempoEntrega: String? = null,
    val abierto: Boolean,
    @SerialName("tiene_promo") val tienePromo: Boolean = false,
    val destacado: Boolean = false,
)

fun LocalDto.toDomain() = Local(
    id = id,
    emoji = emoji,
    nombre = nombre,
    categoria = categoria,
    barrio = barrio,
    rating = "%.1f".format(rating),
    tiempoEntrega = tiempoEntrega,
    abierto = abierto,
    tienePromo = tienePromo,
    destacado = destacado,
)
