package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.usecase.SendPasswordResetUseCase
import com.qless.domain.usecase.UpdatePasswordUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PasswordResetUiState(
    val isLoading: Boolean = false,
    /** true tras enviar el mail: la pantalla pasa a "revisá tu correo". */
    val emailSent: Boolean = false,
    val error: String? = null,
)

/** Evento one-shot: la contraseña se cambió OK → volver a Login. */
sealed interface PasswordResetEvent {
    data object PasswordChanged : PasswordResetEvent
}

/**
 * Maneja las dos puntas del reset por link, separado de [AuthViewModel] a propósito:
 * así la sesión de recuperación transitoria nunca toca el estado de auth global ni
 * dispara navegación a Home.
 *
 * - [sendResetEmail]: pide el mail con el link (pantalla "Olvidé mi contraseña").
 * - [changePassword]: cambia la contraseña usando la sesión de recuperación que abrió
 *   el deep link (pantalla "Nueva contraseña").
 */
class PasswordResetViewModel(
    private val sendPasswordReset: SendPasswordResetUseCase,
    private val updatePassword: UpdatePasswordUseCase,
) : ViewModel() {

    /** Constructor sin args para `viewModel()` en producción: toma el grafo de [AppModule]. */
    constructor() : this(AppModule.sendPasswordReset, AppModule.updatePassword)

    private val _uiState = MutableStateFlow(PasswordResetUiState())
    val uiState: StateFlow<PasswordResetUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PasswordResetEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PasswordResetEvent> = _events.asSharedFlow()

    fun sendResetEmail(email: String) {
        val trimmed = email.trim()
        if (!isValidEmail(trimmed)) {
            _uiState.update { it.copy(error = "Ingresá un correo válido") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            sendPasswordReset(trimmed)
                // Mensaje neutro al éxito: no revelamos si el correo existe (anti-enumeración).
                .onSuccess { _uiState.update { it.copy(isLoading = false, emailSent = true) } }
                .onFailure { err -> _uiState.update { it.copy(isLoading = false, error = mapError(err)) } }
        }
    }

    fun changePassword(newPassword: String, confirmPassword: String) {
        when {
            newPassword.length < 8 -> {
                _uiState.update { it.copy(error = "La contraseña debe tener al menos 8 caracteres") }; return
            }
            newPassword != confirmPassword -> {
                _uiState.update { it.copy(error = "Las contraseñas no coinciden") }; return
            }
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            updatePassword(newPassword)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.tryEmit(PasswordResetEvent.PasswordChanged)
                }
                .onFailure { err -> _uiState.update { it.copy(isLoading = false, error = mapError(err)) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun isValidEmail(value: String): Boolean =
        value.contains("@") && value.substringAfterLast("@").contains(".")

    private fun mapError(err: Throwable): String {
        val msg = err.message ?: return "Algo salió mal. Intentá de nuevo."
        return when {
            msg.contains("rate", ignoreCase = true) || msg.contains("too many", ignoreCase = true) ->
                "Demasiados intentos. Esperá unos minutos."
            msg.contains("network", ignoreCase = true) || msg.contains("connect", ignoreCase = true) ->
                "Sin conexión a internet"
            msg.contains("expired", ignoreCase = true) ||
                msg.contains("invalid", ignoreCase = true) ||
                msg.contains("session", ignoreCase = true) ->
                "El enlace expiró o no es válido. Pedí uno nuevo."
            else -> "Algo salió mal. Intentá de nuevo."
        }
    }
}
