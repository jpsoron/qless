package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaymentMethodUiState(
    val methods: List<PaymentMethod> = emptyList(),
)

class PaymentMethodViewModel : ViewModel() {

    private val observePaymentMethodsUseCase = AppModule.observePaymentMethods
    private val ensureDefaultPaymentMethodsUseCase = AppModule.ensureDefaultPaymentMethods
    private val addPaymentMethodUseCase = AppModule.addPaymentMethod
    private val removePaymentMethodUseCase = AppModule.removePaymentMethod

    private val _uiState = MutableStateFlow(PaymentMethodUiState())
    val uiState: StateFlow<PaymentMethodUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            ensureDefaultPaymentMethodsUseCase()
            observePaymentMethodsUseCase().collect { list ->
                _uiState.update { it.copy(methods = list) }
            }
        }
    }

    fun addMethod(
        nombre: String,
        numero: String,
        vencimiento: String,
        esPrincipal: Boolean,
        esBilletera: Boolean,
    ) {
        viewModelScope.launch {
            addPaymentMethodUseCase(nombre, numero, vencimiento, esPrincipal, esBilletera)
        }
    }

    fun removeMethod(id: String) {
        viewModelScope.launch { removePaymentMethodUseCase(id) }
    }
}
