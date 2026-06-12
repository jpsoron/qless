package com.qless.domain.repository

import com.qless.domain.model.PaymentMethod
import kotlinx.coroutines.flow.Flow

interface PaymentMethodRepository {
    fun getMethods(): Flow<List<PaymentMethod>>
    suspend fun isEmpty(): Boolean
    suspend fun add(
        nombre: String,
        numero: String,
        vencimiento: String,
        esPrincipal: Boolean,
        esBilletera: Boolean,
    )
    suspend fun remove(id: String)
    suspend fun seedDefaults()
}
