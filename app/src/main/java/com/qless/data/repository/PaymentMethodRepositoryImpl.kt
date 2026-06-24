package com.qless.data.repository

import com.qless.data.local.dao.PaymentMethodDao
import com.qless.data.local.entity.PaymentMethodEntity
import com.qless.data.local.entity.toDomain
import com.qless.domain.model.PaymentMethod
import com.qless.domain.repository.PaymentMethodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class PaymentMethodRepositoryImpl(private val dao: PaymentMethodDao) : PaymentMethodRepository {

    override fun getMethods(): Flow<List<PaymentMethod>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun add(
        nombre: String,
        numero: String,
        vencimiento: String,
        esPrincipal: Boolean,
        esBilletera: Boolean,
    ) {
        val sanitized = numero.filter { it.isDigit() }
        val ultimosDigitos = if (sanitized.length >= 4) sanitized.takeLast(4) else sanitized
        val tipo = when {
            esBilletera               -> "MP"
            sanitized.startsWith("4") -> "VISA"
            sanitized.startsWith("5") -> "MC"
            sanitized.startsWith("3") -> "AMEX"
            else                      -> "CARD"
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

    override suspend fun remove(id: String) = dao.deleteById(id)
}
