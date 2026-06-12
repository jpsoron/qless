package com.qless.data.repository

import com.qless.data.remote.MenuRemoteDataSource
import com.qless.domain.model.MenuItem
import com.qless.domain.repository.MenuRepository

class MenuRepositoryImpl(
    private val remoteDataSource: MenuRemoteDataSource = MenuRemoteDataSource(),
) : MenuRepository {
    override suspend fun getMenu(localId: String): Result<List<MenuItem>> =
        remoteDataSource.fetchMenuForLocal(localId)
}
