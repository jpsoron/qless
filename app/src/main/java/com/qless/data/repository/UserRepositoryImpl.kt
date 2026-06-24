package com.qless.data.repository

import android.content.Context
import com.qless.data.SessionStorage
import com.qless.data.local.dao.UserDao
import com.qless.data.remote.AuthRemoteDataSource
import com.qless.data.remote.ProfileRemoteDataSource
import com.qless.domain.model.AuthUser
import com.qless.domain.repository.UserRepository

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
            if (rememberMe) {
                authRemoteDataSource.getCurrentSessionJson()?.let { sessionStorage.save(it) }
            }
            AuthUser(name = perfil.nombre, email = perfil.email, role = perfil.rol, favoritos = perfil.favoritos)
        }

    override suspend fun loginWithGoogle(idToken: String): Result<AuthUser> =
        authRemoteDataSource.signInWithGoogle(idToken).mapCatching {
            val perfil = profileRemoteDataSource.fetchProfile().getOrThrow()
            // Por defecto guardamos la sesión de Google para persistencia
            authRemoteDataSource.getCurrentSessionJson()?.let { sessionStorage.save(it) }
            AuthUser(name = perfil.nombre, email = perfil.email, role = perfil.rol, favoritos = perfil.favoritos)
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
        AuthUser(name = perfil.nombre, email = perfil.email, role = perfil.rol, favoritos = perfil.favoritos)
    }

    override suspend fun clearSession() = sessionStorage.clear()

    override suspend fun toggleFavorito(localId: String, currentFavoritos: List<String>): Result<List<String>> {
        val newFavoritos = if (localId in currentFavoritos) currentFavoritos - localId else currentFavoritos + localId
        return profileRemoteDataSource.updateFavoritos(newFavoritos).map { newFavoritos }
    }

    override suspend fun deleteAccount(email: String): Result<Unit> = runCatching {
        authRemoteDataSource.signOut().getOrThrow()
        sessionStorage.clear()
    }
}
