package com.qless.domain.usecase

import com.qless.domain.model.PaymentMethod
import com.qless.domain.repository.PaymentMethodRepository
import kotlinx.coroutines.flow.Flow

class ObservePaymentMethodsUseCase(private val repository: PaymentMethodRepository) {
    operator fun invoke(): Flow<List<PaymentMethod>> = repository.getMethods()
}

class AddPaymentMethodUseCase(private val repository: PaymentMethodRepository) {
    suspend operator fun invoke(
        nombre: String,
        numero: String,
        vencimiento: String,
        esPrincipal: Boolean,
        esBilletera: Boolean,
    ) = repository.add(nombre, numero, vencimiento, esPrincipal, esBilletera)
}

class RemovePaymentMethodUseCase(private val repository: PaymentMethodRepository) {
    suspend operator fun invoke(id: String) = repository.remove(id)
}
