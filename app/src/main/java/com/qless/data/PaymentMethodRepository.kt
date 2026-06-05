package com.qless.data

import com.qless.data.local.dao.PaymentMethodDao
import com.qless.data.local.entity.PaymentMethodEntity
import com.qless.data.local.entity.toDomain
import com.qless.data.local.entity.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class PaymentMethodRepository(private val dao: PaymentMethodDao) {

    fun getMethods(): Flow<List<PaymentMethod>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun isEmpty(): Boolean = dao.count() == 0

    suspend fun add(
        nombre: String,
        numero: String,
        vencimiento: String,
        esPrincipal: Boolean,
        esBilletera: Boolean,
    ) {
        val sanitized = numero.filter { it.isDigit() }
        val ultimosDigitos = if (sanitized.length >= 4) sanitized.takeLast(4) else sanitized
        val tipo = when {
            esBilletera          -> "MP"
            sanitized.startsWith("4") -> "VISA"
            sanitized.startsWith("5") -> "MC"
            sanitized.startsWith("3") -> "AMEX"
            else                 -> "CARD"
        }
        if (esPrincipal) dao.clearPrincipal()
        dao.insert(
            PaymentMethodEntity(
                id = UUID.randomUUID().toString(),
                tipo = tipo,
                nombre = nombre,
                ultimosDigitos = ultimosDigitos,
                vencimiento = vencimiento,
                esPrincipal = esPrincipal,
                esBilletera = esBilletera,
            )
        )
    }

    suspend fun remove(id: String) = dao.deleteById(id)

    suspend fun seedDefaults() {
        dao.insertAll(DEFAULT_METHODS.map { it.toEntity() })
    }

    companion object {
        private val DEFAULT_METHODS = listOf(
            PaymentMethod("default-visa", "VISA", "María González", "4242", "08/29", true, false),
            PaymentMethod("default-mp",   "MP",   "Mercado Pago",   "",     "",      false, true),
            PaymentMethod("default-mc",   "MC",   "María González", "1034", "03/28", false, false),
        )
    }
}
