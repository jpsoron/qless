package com.qless.data.remote

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
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

    fun getCurrentSessionJson(): String? =
        auth.currentSessionOrNull()?.let { Json.encodeToString(it) }

    suspend fun tryImportSession(json: String): Result<Unit> = runCatching {
        val session = Json { ignoreUnknownKeys = true }.decodeFromString<UserSession>(json)
        auth.importSession(session, autoRefresh = true)
    }
}
