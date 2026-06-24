package com.qless.data.repository

import android.content.Context
import com.qless.data.SessionStorage
import com.qless.data.local.dao.UserDao
import com.qless.data.remote.AuthRemoteDataSource
import com.qless.data.remote.ProfileRemoteDataSource
import com.qless.domain.model.AuthUser
import com.qless.domain.repository.AccountInactiveException
import com.qless.domain.repository.UserRepository

// El usuario BackOffice debe crearse en el dashboard de Supabase.
// El trigger handle_new_user() inserta automáticamente en perfiles con rol='BACK_OFFICE'
// si raw_user_meta_data contiene { "role": "BACK_OFFICE" }.
class UserRepositoryImpl(
    @Suppress("unused") private val dao: UserDao,
    private val context: Context,
    private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource(),
    private val profileRemoteDataSource: ProfileRemoteDataSource = ProfileRemoteDataSource(),
) : UserRepository {

    private val sessionStorage = SessionStorage(context)

    override suspend fun login(email: String, password: String, rememberMe: Boolean): Result<AuthUser> =
        authRemoteDataSource.signIn(email, password).mapCatching {
            val perfil = profileRemoteDataSource.fetchProfile().getOrThrow()
            // Cuenta dada de baja: no se permite entrar. Cerramos la sesión recién abierta.
            if (!perfil.activo) {
                authRemoteDataSource.signOut()
                throw AccountInactiveException()
            }
            if (rememberMe) {
                authRemoteDataSource.getCurrentSessionJson()?.let { sessionStorage.save(it) }
            }
            AuthUser(name = perfil.nombre, email = perfil.email, role = perfil.rol, favoritos = perfil.favoritos, firstOrderDiscount = perfil.descuento1ra)
        }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> =
        authRemoteDataSource.signUp(email, password, name)

    override suspend fun logout(): Result<Unit> = runCatching {
        authRemoteDataSource.signOut().getOrThrow()
        sessionStorage.clear()
    }

    override suspend fun tryRestoreSession(): Result<AuthUser?> = runCatching {
        val json = sessionStorage.load() ?: return@runCatching null
        authRemoteDataSource.tryImportSession(json).getOrThrow()
        val perfil = profileRemoteDataSource.fetchProfile().getOrThrow()
        // Si la cuenta fue dada de baja, descartamos la sesión guardada.
        if (!perfil.activo) {
            authRemoteDataSource.signOut()
            sessionStorage.clear()
            return@runCatching null
        }
        AuthUser(name = perfil.nombre, email = perfil.email, role = perfil.rol, favoritos = perfil.favoritos, firstOrderDiscount = perfil.descuento1ra)
    }

    override suspend fun clearSession() = sessionStorage.clear()

    override suspend fun toggleFavorito(localId: String, currentFavoritos: List<String>): Result<List<String>> {
        val newFavoritos = if (localId in currentFavoritos) currentFavoritos - localId else currentFavoritos + localId
        return profileRemoteDataSource.updateFavoritos(newFavoritos).map { newFavoritos }
    }

    // Baja lógica: marca el perfil como inactivo (activo = false) y después cierra
    // sesión. El borrado físico de auth.users requiere service role key (backend).
    override suspend fun deleteAccount(email: String): Result<Unit> = runCatching {
        profileRemoteDataSource.deactivateAccount().getOrThrow()
        authRemoteDataSource.signOut().getOrThrow()
        sessionStorage.clear()
    }

    override suspend fun consumeFirstOrderDiscount(): Result<Unit> =
        profileRemoteDataSource.consumeFirstOrderDiscount()

    override suspend fun updateProfile(name: String, email: String): Result<Unit> =
        profileRemoteDataSource.updateProfile(name, email)
}
