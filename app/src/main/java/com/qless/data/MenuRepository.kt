package com.qless.data

import com.qless.data.remote.MenuRemoteDataSource

class MenuRepository(
    private val remoteDataSource: MenuRemoteDataSource = MenuRemoteDataSource()
) {
    suspend fun getMenu(localId: String): Result<List<MenuItem>> =
        remoteDataSource.fetchMenuForLocal(localId)
}
