package com.qless.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qless.data.PaymentMethod
import com.qless.data.PaymentMethodRepository
import com.qless.data.local.QLessDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaymentMethodUiState(
    val methods: List<PaymentMethod> = emptyList(),
)

class PaymentMethodViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = PaymentMethodRepository(
        dao = QLessDatabase.getInstance(app).paymentMethodDao()
    )

    private val _uiState = MutableStateFlow(PaymentMethodUiState())
    val uiState: StateFlow<PaymentMethodUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (repository.isEmpty()) repository.seedDefaults()
            repository.getMethods().collect { list ->
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
            repository.add(nombre, numero, vencimiento, esPrincipal, esBilletera)
        }
    }

    fun removeMethod(id: String) {
        viewModelScope.launch { repository.remove(id) }
    }
}
