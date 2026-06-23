package com.qless.domain.repository

import com.qless.domain.model.CachedResult
import com.qless.domain.model.MenuItem

interface MenuRepository {
    suspend fun getMenu(localId: String): Result<CachedResult<List<MenuItem>>>
}
