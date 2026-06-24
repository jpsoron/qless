package com.qless.data.repository

import com.qless.data.local.dao.LocalDao
import com.qless.data.local.entity.toDomain
import com.qless.data.local.entity.toEntity
import com.qless.data.remote.LocalesRemoteDataSource
import com.qless.domain.model.CachedResult
import com.qless.domain.model.Local
import com.qless.domain.repository.LocalesRepository

/**
 * Trae los locales de la red y los cachea en Room. Si la red falla, hace
 * fallback a la última copia local (RF4). Solo propaga el error si no hay caché.
 */
class LocalesRepositoryImpl(
    private val localDao: LocalDao,
    private val remoteDataSource: LocalesRemoteDataSource = LocalesRemoteDataSource(),
) : LocalesRepository {

    override suspend fun getLocales(): Result<CachedResult<List<Local>>> =
        remoteDataSource.fetchLocales()
            .map { locales ->
                localDao.replaceAll(locales.map { it.toEntity() })
                CachedResult(locales, fromCache = false)
            }
            .recoverCatching { error ->
                val cached = localDao.getAll().map { it.toDomain() }
                if (cached.isEmpty()) throw error
                CachedResult(cached, fromCache = true)
            }

    override suspend fun getLocalById(id: String): Result<CachedResult<Local?>> {
        if (id.isBlank()) return Result.success(CachedResult(null, fromCache = false))
        return remoteDataSource.fetchLocalesByIds(listOf(id))
            .map { locales ->
                locales.firstOrNull { it.id == id }
                    ?.also { localDao.upsertAll(listOf(it.toEntity())) }
                    .let { CachedResult(it, fromCache = false) }
            }
            .recoverCatching {
                val cached = localDao.getByIds(listOf(id)).firstOrNull { it.id == id }?.toDomain()
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
}
