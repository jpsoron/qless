package com.qless.data.repository

import com.qless.data.local.dao.LocalDao
import com.qless.data.local.entity.toDomain
import com.qless.data.local.entity.toEntity
import com.qless.data.remote.LocalesRemoteDataSource
import com.qless.domain.model.CachedResult
import com.qless.domain.model.Local
import com.qless.domain.repository.LocalesRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Trae los locales de la red y los cachea en Room. Si la red falla, hace
 * fallback a la última copia local (RF4). Solo propaga el error si no hay caché.
 */
class LocalesRepositoryImpl(
    private val localDao: LocalDao,
    private val remoteDataSource: LocalesRemoteDataSource = LocalesRemoteDataSource(),
) : LocalesRepository {

    // Caché en memoria con single-flight: Home (local más cercano) y MisLocales
    // piden la misma lista casi a la vez. El mutex serializa, así el segundo
    // colector reusa lo que trajo el primero en vez de pegarle de nuevo a la red.
    private val getLocalesMutex = Mutex()
    @Volatile private var memoryCache: List<Local>? = null
    @Volatile private var cachedAtMs: Long = 0L

    override suspend fun getLocales(): Result<CachedResult<List<Local>>> = getLocalesMutex.withLock {
        memoryCache?.let { cached ->
            if (System.currentTimeMillis() - cachedAtMs < MEMORY_CACHE_TTL_MS) {
                return@withLock Result.success(CachedResult(cached, fromCache = false))
            }
        }
        remoteDataSource.fetchLocales()
            .map { locales ->
                localDao.replaceAll(locales.map { it.toEntity() })
                memoryCache = locales
                cachedAtMs = System.currentTimeMillis()
                CachedResult(locales, fromCache = false)
            }
            .recoverCatching { error ->
                val cached = localDao.getAll().map { it.toDomain() }
                if (cached.isEmpty()) throw error
                CachedResult(cached, fromCache = true)
            }
    }

    override suspend fun getFavoritos(ids: List<String>): Result<CachedResult<List<Local>>> {
        if (ids.isEmpty()) return Result.success(CachedResult(emptyList(), fromCache = false))
        return remoteDataSource.fetchLocalesByIds(ids)
            .map { locales ->
                localDao.upsertAll(locales.map { it.toEntity() })
                CachedResult(locales, fromCache = false)
            }
            .recoverCatching { error ->
                val byId = localDao.getByIds(ids).associateBy { it.id }
                // Respeta el orden de favoritos del perfil.
                val cached = ids.mapNotNull { byId[it]?.toDomain() }
                if (cached.isEmpty()) throw error
                CachedResult(cached, fromCache = true)
            }
    }

    private companion object {
        // Ventana corta: solo dedupe entre pantallas de la misma sesión de navegación.
        const val MEMORY_CACHE_TTL_MS = 30_000L
    }
}
