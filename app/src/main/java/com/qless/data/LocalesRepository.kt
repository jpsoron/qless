package com.qless.data

import com.qless.data.remote.LocalesRemoteDataSource

class LocalesRepository(
    private val remoteDataSource: LocalesRemoteDataSource = LocalesRemoteDataSource()
) {
    suspend fun getLocales(): Result<List<Local>> = remoteDataSource.fetchLocales()
}
