package com.qless.domain.usecase

import com.qless.domain.model.MenuItem
import com.qless.domain.repository.MenuRepository

class GetMenuUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(localId: String): Result<List<MenuItem>> =
        repository.getMenu(localId)
}
