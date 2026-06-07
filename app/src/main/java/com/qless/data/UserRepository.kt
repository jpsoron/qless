package com.qless.data

import com.qless.data.local.dao.UserDao
import com.qless.data.remote.AuthRemoteDataSource
import com.qless.data.remote.ProfileRemoteDataSource

data class RemoteUser(val name: String, val email: String, val role: String)

// El usuario BackOffice debe crearse en el dashboard de Supabase.
// El trigger handle_new_user() inserta automáticamente en perfiles con rol='BACK_OFFICE'
// si raw_user_meta_data contiene { "role": "BACK_OFFICE" }.
class UserRepository(
    @Suppress("unused") private val dao: UserDao,
    private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource(),
    private val profileRemoteDataSource: ProfileRemoteDataSource = ProfileRemoteDataSource()
) {
    suspend fun login(email: String, password: String): Result<RemoteUser> =
        authRemoteDataSource.signIn(email, password).mapCatching {
            val perfil = profileRemoteDataSource.fetchProfile().getOrThrow()
            RemoteUser(name = perfil.nombre, email = perfil.email, role = perfil.rol)
        }

    suspend fun register(name: String, email: String, password: String): Result<Unit> =
        authRemoteDataSource.signUp(email, password, name)

    suspend fun logout(): Result<Unit> =
        authRemoteDataSource.signOut()

    // La eliminación real requiere service role key (solo backend).
    // Por ahora cierra sesión y limpia el estado local.
    suspend fun deleteAccount(@Suppress("UNUSED_PARAMETER") email: String): Result<Unit> =
        authRemoteDataSource.signOut()

    suspend fun seedBackOffice() = Unit
}
