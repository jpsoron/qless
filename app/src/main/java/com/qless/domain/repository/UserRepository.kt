package com.qless.domain.repository

import com.qless.domain.model.AuthUser

interface UserRepository {
    suspend fun login(email: String, password: String, rememberMe: Boolean = false): Result<AuthUser>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun tryRestoreSession(): Result<AuthUser?>
    suspend fun clearSession()
    suspend fun toggleFavorito(localId: String, currentFavoritos: List<String>): Result<List<String>>
    suspend fun deleteAccount(email: String): Result<Unit>
}
