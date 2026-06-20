package com.qless.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qless.domain.model.MenuItem

/**
 * Caché local del menú de un local (RF4 — modo offline).
 * Guarda [orden] (el índice ya ordenado que llegó de la red) para reconstruir
 * la carta en el mismo orden sin depender de Supabase.
 */
@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val localId: String,
    val emoji: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String,
    val esPopular: Boolean,
    val disponible: Boolean,
    val orden: Int,
)

fun MenuItemEntity.toDomain() = MenuItem(
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

fun MenuItem.toEntity(orden: Int) = MenuItemEntity(
    id = id,
    localId = localId,
    emoji = emoji,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    categoria = categoria,
    esPopular = esPopular,
    disponible = disponible,
    orden = orden,
)
