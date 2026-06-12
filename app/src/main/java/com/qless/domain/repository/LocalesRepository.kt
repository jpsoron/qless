package com.qless.domain.repository

import com.qless.domain.model.Local

interface LocalesRepository {
    suspend fun getLocales(): Result<List<Local>>
    suspend fun getFavoritos(ids: List<String>): Result<List<Local>>
}
