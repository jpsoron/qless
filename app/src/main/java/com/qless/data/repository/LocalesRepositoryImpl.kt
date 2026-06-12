package com.qless.data.repository

import com.qless.data.remote.LocalesRemoteDataSource
import com.qless.domain.model.Local
import com.qless.domain.repository.LocalesRepository

class LocalesRepositoryImpl(
    private val remoteDataSource: LocalesRemoteDataSource = LocalesRemoteDataSource(),
) : LocalesRepository {
    override suspend fun getLocales(): Result<List<Local>> = remoteDataSource.fetchLocales()
    override suspend fun getFavoritos(ids: List<String>): Result<List<Local>> =
        remoteDataSource.fetchLocalesByIds(ids)
}
