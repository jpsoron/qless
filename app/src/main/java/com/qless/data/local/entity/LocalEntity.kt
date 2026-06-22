package com.qless.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qless.domain.model.Local

/**
 * Caché local de un local gastronómico (RF4 — modo offline).
 * Espeja 1:1 el modelo de dominio [Local] para poder reconstruirlo sin red.
 */
@Entity(tableName = "locales")
data class LocalEntity(
    @PrimaryKey val id: String,
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
)

fun LocalEntity.toDomain() = Local(
    id = id,
    emoji = emoji,
    nombre = nombre,
    categoria = categoria,
    barrio = barrio,
    rating = rating,
    tiempoEntrega = tiempoEntrega,
    abierto = abierto,
    tienePromo = tienePromo,
    destacado = destacado,
    latitud = latitud,
    longitud = longitud,
)

fun Local.toEntity() = LocalEntity(
    id = id,
    emoji = emoji,
    nombre = nombre,
    categoria = categoria,
    barrio = barrio,
    rating = rating,
    tiempoEntrega = tiempoEntrega,
    abierto = abierto,
    tienePromo = tienePromo,
    destacado = destacado,
    latitud = latitud,
    longitud = longitud,
)
