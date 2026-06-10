package com.qless.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qless.data.CartItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val name: String,
    val emoji: String,
    val detail: String,
    val unitPrice: Int,
    val quantity: Int,
    val menuItemId: String = "",
    val localId: String = "",
)

fun CartItemEntity.toDomain() = CartItem(
    emoji = emoji,
    name = name,
    detail = detail,
    unitPrice = unitPrice,
    quantity = quantity,
    menuItemId = menuItemId,
    localId = localId,
)

fun CartItem.toEntity() = CartItemEntity(
    name = name,
    emoji = emoji,
    detail = detail,
    unitPrice = unitPrice,
    quantity = quantity,
    menuItemId = menuItemId,
    localId = localId,
)
