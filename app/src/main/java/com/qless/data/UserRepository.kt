package com.qless.data

import android.content.Context
import com.qless.data.local.dao.UserDao
import com.qless.data.remote.AuthRemoteDataSource
import com.qless.data.remote.ProfileRemoteDataSource

data class RemoteUser(val name: String, val email: String, val role: String, val favoritos: List<String> = emptyList())

// El usuario BackOffice debe crearse en el dashboard de Supabase.
// El trigger handle_new_user() inserta automáticamente en perfiles con rol='BACK_OFFICE'
// si raw_user_meta_data contiene { "role": "BACK_OFFICE" }.
class UserRepository(
    @Suppress("unused") private val dao: UserDao,
    private val context: Context,
    private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource(),
    private val profileRemoteDataSource: ProfileRemoteDataSource = ProfileRemoteDataSource()
) {
    private val sessionStorage = SessionStorage(context)

    suspend fun login(email: String, password: String, rememberMe: Boolean = false): Result<RemoteUser> =
        authRemoteDataSource.signIn(email, password).mapCatching {
            val perfil = profileRemoteDataSource.fetchProfile().getOrThrow()
            if (rememberMe) {
                authRemoteDataSource.getCurrentSessionJson()?.let { sessionStorage.save(it) }
            }
            RemoteUser(name = perfil.nombre, email = perfil.email, role = perfil.rol, favoritos = perfil.favoritos)
        }

    suspend fun register(name: String, email: String, password: String): Result<Unit> =
        authRemoteDataSource.signUp(email, password, name)

    suspend fun logout(): Result<Unit> = runCatching {
        authRemoteDataSource.signOut().getOrThrow()
        sessionStorage.clear()
    }

    suspend fun tryRestoreSession(): Result<RemoteUser?> = runCatching {
        val json = sessionStorage.load() ?: return@runCatching null
        authRemoteDataSource.tryImportSession(json).getOrThrow()
        val perfil = profileRemoteDataSource.fetchProfile().getOrThrow()
        RemoteUser(name = perfil.nombre, email = perfil.email, role = perfil.rol, favoritos = perfil.favoritos)
    }

    suspend fun clearSession() = sessionStorage.clear()

    // La eliminación real requiere service role key (solo backend).
    // Por ahora cierra sesión y limpia el estado local.
    suspend fun deleteAccount(@Suppress("UNUSED_PARAMETER") email: String): Result<Unit> = runCatching {
        authRemoteDataSource.signOut().getOrThrow()
        sessionStorage.clear()
    }

    suspend fun seedBackOffice() = Unit
}
