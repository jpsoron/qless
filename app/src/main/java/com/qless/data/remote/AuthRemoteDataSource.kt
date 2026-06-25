package com.qless.data.remote

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// Cambiar a false cuando se habilite "Confirm email" en Supabase
// (Authentication → Configuration → Email → "Confirm email" ON).
// Ver DISENO_DATOS_Y_ARQUITECTURA.md § 7 para el procedimiento completo.
private const val AUTO_SIGNIN_AFTER_REGISTER = true

class AuthRemoteDataSource {

    private val auth get() = SupabaseClient.instance.auth

    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signUp(email: String, password: String, name: String): Result<Unit> = runCatching {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("name", name)
                put("role", "USER")
            }
        }
        if (AUTO_SIGNIN_AFTER_REGISTER) {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
    }

    /**
     * Canjea el ID token de Google por una sesión de Supabase. El [rawNonce] debe ser
     * el nonce sin hashear (Supabase compara su hash contra el nonce del token).
     */
    suspend fun signInWithGoogle(idToken: String, rawNonce: String): Result<Unit> = runCatching {
        auth.signInWith(IDToken) {
            this.idToken = idToken
            this.provider = Google
            this.nonce = rawNonce
        }
    }

    /**
     * Pide a Supabase el mail de recuperación. El link del mail rebota a la app
     * vía el deep link [PASSWORD_RESET_REDIRECT] (debe estar en la allowlist de
     * Redirect URLs del proyecto y declarado en el manifest).
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.resetPasswordForEmail(email, redirectUrl = PASSWORD_RESET_REDIRECT)
    }

    /** Cambia la contraseña del usuario de la sesión de recuperación activa. */
    suspend fun updatePassword(newPassword: String): Result<Unit> = runCatching {
        auth.updateUser { password = newPassword }
    }

    companion object {
        /** Deep link al que Supabase devuelve tras validar el link de reset. */
        const val PASSWORD_RESET_REDIRECT = "qless://reset-password"
    }

    fun getCurrentSessionJson(): String? =
        auth.currentSessionOrNull()?.let { Json.encodeToString(it) }

    suspend fun tryImportSession(json: String): Result<Unit> = runCatching {
        val session = Json { ignoreUnknownKeys = true }.decodeFromString<UserSession>(json)
        auth.importSession(session, autoRefresh = true)
    }
}
