package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.repository.EmailAlreadyInUseException
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
    val firstOrderDiscount: Boolean = false,
    val sessionCheckDone: Boolean = false,
    val sessionRestored: Boolean = false,
    val loginError: String? = null,
    val registerError: String? = null,
    val isLoading: Boolean = false,
    val profileError: String? = null,
    val isSavingProfile: Boolean = false,
)

sealed interface AuthNavEvent {
    data object LoginSuccess : AuthNavEvent
    data object LoginBackOffice : AuthNavEvent
    data object RegisterSuccess : AuthNavEvent
    data object AccountDeleted : AuthNavEvent
    data object ProfileUpdated : AuthNavEvent
}

class AuthViewModel : ViewModel() {

    private val loginUseCase = AppModule.login
    private val registerUseCase = AppModule.register
    private val logoutUseCase = AppModule.logout
    private val restoreSessionUseCase = AppModule.restoreSession
    private val clearSessionUseCase = AppModule.clearSession
    private val toggleFavoritoUseCase = AppModule.toggleFavorito
    private val deleteAccountUseCase = AppModule.deleteAccount
    private val consumeFirstOrderDiscountUseCase = AppModule.consumeFirstOrderDiscount
    private val updateProfileUseCase = AppModule.updateProfile

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<AuthNavEvent>()
    val navEvent: SharedFlow<AuthNavEvent> = _navEvent.asSharedFlow()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            restoreSessionUseCase()
                .onSuccess { user ->
                    if (user != null) {
                        _uiState.update {
                            it.copy(
                                currentUserName = user.name,
                                currentUserEmail = user.email,
                                currentUserRole = user.role,
                                currentUserFavoritos = user.favoritos,
                                firstOrderDiscount = user.firstOrderDiscount,
                                sessionCheckDone = true,
                                sessionRestored = true
                            )
                        }
                    } else {
                        _uiState.update { it.copy(sessionCheckDone = true, sessionRestored = false) }
                    }
                }
                .onFailure {
                    viewModelScope.launch { clearSessionUseCase() }
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
            loginUseCase(email.trim(), password, rememberMe)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentUserName = user.name,
                            currentUserEmail = user.email,
                            currentUserRole = user.role,
                            currentUserFavoritos = user.favoritos,
                            firstOrderDiscount = user.firstOrderDiscount
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
            registerUseCase(name.trim(), email.trim(), password)
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
            logoutUseCase()
            _uiState.update { AuthUiState(sessionCheckDone = true) }
        }
    }

    fun deleteAccount() {
        val email = _uiState.value.currentUserEmail
        viewModelScope.launch {
            deleteAccountUseCase(email)
            _uiState.update { AuthUiState(sessionCheckDone = true) }
            _navEvent.emit(AuthNavEvent.AccountDeleted)
        }
    }

    fun toggleFavorito(localId: String) {
        val current = _uiState.value.currentUserFavoritos
        viewModelScope.launch {
            toggleFavoritoUseCase(localId, current)
                .onSuccess { newFavoritos ->
                    _uiState.update { it.copy(currentUserFavoritos = newFavoritos) }
                }
        }
    }

    /**
     * Consume el descuento de bienvenida tras el primer pedido: actualiza el estado
     * local de forma optimista y persiste descuento_1ra = false en el perfil.
     * No hace nada si el beneficio ya estaba usado.
     */
    fun consumeFirstOrderDiscount() {
        if (!_uiState.value.firstOrderDiscount) return
        _uiState.update { it.copy(firstOrderDiscount = false) }
        viewModelScope.launch { consumeFirstOrderDiscountUseCase() }
    }

    /**
     * Actualiza nombre y email del perfil en la base. Valida formato y delega la
     * unicidad del correo al repositorio. Emite [AuthNavEvent.ProfileUpdated] al éxito.
     */
    fun updateProfile(name: String, email: String) {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        when {
            trimmedName.isBlank() -> { _uiState.update { it.copy(profileError = "El nombre es obligatorio") }; return }
            !trimmedEmail.contains("@") || !trimmedEmail.substringAfterLast("@").contains(".") -> {
                _uiState.update { it.copy(profileError = "Ingresá un correo válido") }; return
            }
        }
        _uiState.update { it.copy(isSavingProfile = true, profileError = null) }
        viewModelScope.launch {
            updateProfileUseCase(trimmedName, trimmedEmail)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSavingProfile = false,
                            profileError = null,
                            currentUserName = trimmedName,
                            currentUserEmail = trimmedEmail,
                        )
                    }
                    _navEvent.emit(AuthNavEvent.ProfileUpdated)
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isSavingProfile = false, profileError = mapProfileError(err)) }
                }
        }
    }

    fun clearProfileError() {
        _uiState.update { it.copy(profileError = null) }
    }

    private fun mapProfileError(err: Throwable): String = when {
        err is EmailAlreadyInUseException -> "Ese correo ya está registrado"
        err.message?.contains("network", ignoreCase = true) == true ||
            err.message?.contains("connect", ignoreCase = true) == true -> "Sin conexión a internet"
        else -> "No se pudo guardar. Intentá de nuevo."
    }

    fun clearErrors() {
        _uiState.update { it.copy(loginError = null, registerError = null) }
    }

    private fun mapAuthError(message: String?): String = when {
        message == null -> "Error de autenticación"
        message.contains("Invalid login credentials", ignoreCase = true) -> "Correo o contraseña incorrectos"
        message.contains("User already registered", ignoreCase = true) -> "El correo ya está registrado"
        message.contains("Email not confirmed", ignoreCase = true) -> "Confirmá tu correo antes de ingresar"
        message.contains("eliminada", ignoreCase = true) -> "Esta cuenta fue eliminada"
        message.contains("network", ignoreCase = true) || message.contains("connect", ignoreCase = true) -> "Sin conexión a internet"
        else -> "Error de autenticación"
    }
}
