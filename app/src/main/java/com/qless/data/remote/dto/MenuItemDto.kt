package com.qless.data.remote.dto

import com.qless.data.MenuItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuItemDto(
    val id: String,
    @SerialName("local_id") val localId: String,
    val emoji: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String,
    @SerialName("es_popular") val esPopular: Boolean = false,
    val disponible: Boolean = true,
    val orden: Int = 0,
)

fun MenuItemDto.toDomain() = MenuItem(
    id = id,
    localId = localId,
    emoji = emoji,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    categoria = categoria,
    esPopular = esPopular,
    disponible = disponible,
)
