package com.qless.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qless.data.PaymentMethod
import com.qless.data.PaymentMethodRepository
import com.qless.data.local.QLessDatabase
import kotlinx.coroutines.launch

class PaymentMethodViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = PaymentMethodRepository(
        dao = QLessDatabase.getInstance(app).paymentMethodDao()
    )

    val methods = mutableStateListOf<PaymentMethod>()

    init {
        viewModelScope.launch {
            if (repository.isEmpty()) {
                repository.seedDefaults()
            }
            repository.getMethods().collect { list ->
                methods.clear()
                methods.addAll(list)
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
        viewModelScope.launch {
            repository.remove(id)
        }
    }
}
