package com.qless.data.repository

import com.qless.data.local.dao.MenuItemDao
import com.qless.data.local.entity.toDomain
import com.qless.data.local.entity.toEntity
import com.qless.data.remote.MenuRemoteDataSource
import com.qless.domain.model.CachedResult
import com.qless.domain.model.MenuItem
import com.qless.domain.repository.MenuRepository

/**
 * Trae el menú de la red y lo cachea en Room. Si la red falla, hace fallback a
 * la última copia local (RF4). Solo propaga el error si tampoco hay caché.
 */
class MenuRepositoryImpl(
    private val menuItemDao: MenuItemDao,
    private val remoteDataSource: MenuRemoteDataSource = MenuRemoteDataSource(),
) : MenuRepository {

    override suspend fun getMenu(localId: String): Result<CachedResult<List<MenuItem>>> =
        remoteDataSource.fetchMenuForLocal(localId)
            .map { items ->
                menuItemDao.replaceForLocal(
                    localId,
                    items.mapIndexed { index, item -> item.toEntity(index) },
                )
                CachedResult(items, fromCache = false)
            }
            .recoverCatching { error ->
                val cached = menuItemDao.getForLocal(localId).map { it.toDomain() }
                if (cached.isEmpty()) throw error
                CachedResult(cached, fromCache = true)
            }
}
