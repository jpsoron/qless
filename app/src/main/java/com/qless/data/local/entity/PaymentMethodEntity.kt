package com.qless.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qless.domain.model.PaymentMethod

@Entity(tableName = "payment_methods")
data class PaymentMethodEntity(
    @PrimaryKey val id: String,
    val tipo: String,
    val nombre: String,
    val ultimosDigitos: String,
    val vencimiento: String,
    val esPrincipal: Boolean,
    val esBilletera: Boolean,
)

fun PaymentMethodEntity.toDomain() = PaymentMethod(
    id = id,
    tipo = tipo,
    nombre = nombre,
    ultimosDigitos = ultimosDigitos,
    vencimiento = vencimiento,
    esPrincipal = esPrincipal,
    esBilletera = esBilletera,
)

fun PaymentMethod.toEntity() = PaymentMethodEntity(
    id = id,
    tipo = tipo,
    nombre = nombre,
    ultimosDigitos = ultimosDigitos,
    vencimiento = vencimiento,
    esPrincipal = esPrincipal,
    esBilletera = esBilletera,
)
