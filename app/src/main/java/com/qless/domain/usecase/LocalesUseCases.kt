package com.qless.domain.usecase

import com.qless.domain.model.Local
import com.qless.domain.repository.LocalesRepository

class GetLocalesUseCase(private val repository: LocalesRepository) {
    suspend operator fun invoke(): Result<List<Local>> = repository.getLocales()
}

class GetFavoritosUseCase(private val repository: LocalesRepository) {
    suspend operator fun invoke(ids: List<String>): Result<List<Local>> =
        repository.getFavoritos(ids)
}
