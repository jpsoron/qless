package com.qless.domain.repository

import com.qless.domain.model.AuthUser

/** El correo ya está en uso por otro perfil al editar datos personales. */
class EmailAlreadyInUseException : Exception("Ese correo ya está en uso")

/** La cuenta fue dada de baja (activo = false): no se permite iniciar sesión. */
class AccountInactiveException : Exception("Esta cuenta fue eliminada")

interface UserRepository {
    suspend fun login(email: String, password: String, rememberMe: Boolean = false): Result<AuthUser>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun tryRestoreSession(): Result<AuthUser?>
    suspend fun clearSession()
    suspend fun toggleFavorito(localId: String, currentFavoritos: List<String>): Result<List<String>>
    suspend fun deleteAccount(email: String): Result<Unit>
    /** Marca el descuento de bienvenida como usado tras el primer pedido. */
    suspend fun consumeFirstOrderDiscount(): Result<Unit>
    /** Actualiza nombre y email en la tabla de perfiles (valida email único). */
    suspend fun updateProfile(name: String, email: String): Result<Unit>

    /** Dispara el envío del mail de recuperación de contraseña (link de reset). */
    suspend fun sendPasswordReset(email: String): Result<Unit>

    /**
     * Cambia la contraseña del usuario autenticado por la sesión de recuperación
     * (abierta al volver del link del mail) y cierra esa sesión para forzar un
     * login fresco con la credencial nueva.
     */
    suspend fun updatePassword(newPassword: String): Result<Unit>
}
