package com.qless.domain.repository

import com.qless.domain.model.CachedResult
import com.qless.domain.model.Local

interface LocalesRepository {
    suspend fun getLocales(): Result<CachedResult<List<Local>>>
    suspend fun getFavoritos(ids: List<String>): Result<CachedResult<List<Local>>>
}
