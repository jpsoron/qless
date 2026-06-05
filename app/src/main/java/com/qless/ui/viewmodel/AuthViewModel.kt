package com.qless.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qless.data.UserRepository
import com.qless.data.local.QLessDatabase
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = UserRepository(
        dao = QLessDatabase.getInstance(app).userDao()
    )

    var loginError by mutableStateOf<String?>(null)
        private set

    var registerError by mutableStateOf<String?>(null)
        private set

    var currentUserName by mutableStateOf("")
        private set

    var currentUserEmail by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            repository.seedBackOffice()
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onBackOffice: () -> Unit,
    ) {
        loginError = null
        if (email.isBlank() || password.isBlank()) {
            loginError = "Completa todos los campos"
            return
        }
        viewModelScope.launch {
            val user = repository.login(email.trim(), password)
            when {
                user == null               -> loginError = "Correo o contraseña incorrectos"
                user.role == "BACK_OFFICE" -> { currentUserName = user.name; currentUserEmail = user.email; onBackOffice() }
                else                       -> { currentUserName = user.name; currentUserEmail = user.email; onSuccess() }
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
    ) {
        registerError = null
        when {
            name.isBlank()             -> { registerError = "El nombre es obligatorio"; return }
            email.isBlank()            -> { registerError = "El correo es obligatorio"; return }
            password.length < 8        -> { registerError = "La contraseña debe tener al menos 8 caracteres"; return }
            password != confirmPassword -> { registerError = "Las contraseñas no coinciden"; return }
        }
        viewModelScope.launch {
            repository.register(name.trim(), email.trim(), password)
                .onSuccess { onSuccess() }
                .onFailure { registerError = it.message }
        }
    }

    fun logout() {
        currentUserName = ""
        currentUserEmail = ""
        loginError = null
        registerError = null
    }

    fun deleteAccount(onComplete: () -> Unit) {
        val email = currentUserEmail
        viewModelScope.launch {
            repository.deleteAccount(email)
            currentUserName = ""
            currentUserEmail = ""
            loginError = null
            registerError = null
            onComplete()
        }
    }

    fun clearErrors() {
        loginError = null
        registerError = null
    }
}
