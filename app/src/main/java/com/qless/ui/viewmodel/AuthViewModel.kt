package com.qless.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qless.data.RemoteUser
import com.qless.data.UserRepository
import com.qless.data.local.QLessDatabase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUserName: String = "",
    val currentUserEmail: String = "",
    val currentUserRole: String = "",
    val currentUserFavoritos: List<String> = emptyList(),
    val sessionCheckDone: Boolean = false,
    val sessionRestored: Boolean = false,
    val loginError: String? = null,
    val registerError: String? = null,
    val isLoading: Boolean = false,
)

sealed interface AuthNavEvent {
    data object LoginSuccess : AuthNavEvent
    data object LoginBackOffice : AuthNavEvent
    data object RegisterSuccess : AuthNavEvent
    data object AccountDeleted : AuthNavEvent
}

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = UserRepository(
        dao = QLessDatabase.getInstance(app).userDao(),
        context = app
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<AuthNavEvent>()
    val navEvent: SharedFlow<AuthNavEvent> = _navEvent.asSharedFlow()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            repository.tryRestoreSession()
                .onSuccess { user ->
                    if (user != null) {
                        _uiState.update {
                            it.copy(
                                currentUserName = user.name,
                                currentUserEmail = user.email,
                                currentUserRole = user.role,
                                currentUserFavoritos = user.favoritos,
                                sessionCheckDone = true,
                                sessionRestored = true
                            )
                        }
                    } else {
                        _uiState.update { it.copy(sessionCheckDone = true, sessionRestored = false) }
                    }
                }
                .onFailure {
                    viewModelScope.launch { repository.clearSession() }
                    _uiState.update { it.copy(sessionCheckDone = true, sessionRestored = false) }
                }
        }
    }

    fun login(email: String, password: String, rememberMe: Boolean = false) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(loginError = "Completa todos los campos") }
            return
        }
        _uiState.update { it.copy(isLoading = true, loginError = null) }
        viewModelScope.launch {
            repository.login(email.trim(), password, rememberMe)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentUserName = user.name,
                            currentUserEmail = user.email,
                            currentUserRole = user.role,
                            currentUserFavoritos = user.favoritos
                        )
                    }
                    if (user.role == "BACK_OFFICE") {
                        _navEvent.emit(AuthNavEvent.LoginBackOffice)
                    } else {
                        _navEvent.emit(AuthNavEvent.LoginSuccess)
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, loginError = mapAuthError(err.message)) }
                }
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        _uiState.update { it.copy(registerError = null) }
        when {
            name.isBlank()              -> { _uiState.update { it.copy(registerError = "El nombre es obligatorio") }; return }
            email.isBlank()             -> { _uiState.update { it.copy(registerError = "El correo es obligatorio") }; return }
            password.length < 8         -> { _uiState.update { it.copy(registerError = "La contraseña debe tener al menos 8 caracteres") }; return }
            password != confirmPassword -> { _uiState.update { it.copy(registerError = "Las contraseñas no coinciden") }; return }
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.register(name.trim(), email.trim(), password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvent.emit(AuthNavEvent.RegisterSuccess)
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, registerError = err.message ?: "null") }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update { AuthUiState(sessionCheckDone = true) }
        }
    }

    fun deleteAccount() {
        val email = _uiState.value.currentUserEmail
        viewModelScope.launch {
            repository.deleteAccount(email)
            _uiState.update { AuthUiState(sessionCheckDone = true) }
            _navEvent.emit(AuthNavEvent.AccountDeleted)
        }
    }

    fun clearErrors() {
        _uiState.update { it.copy(loginError = null, registerError = null) }
    }

    private fun mapAuthError(message: String?): String = when {
        message == null -> "Error de autenticación"
        message.contains("Invalid login credentials", ignoreCase = true) -> "Correo o contraseña incorrectos"
        message.contains("User already registered", ignoreCase = true) -> "El correo ya está registrado"
        message.contains("Email not confirmed", ignoreCase = true) -> "Confirmá tu correo antes de ingresar"
        message.contains("network", ignoreCase = true) || message.contains("connect", ignoreCase = true) -> "Sin conexión a internet"
        else -> "Error de autenticación"
    }
}
