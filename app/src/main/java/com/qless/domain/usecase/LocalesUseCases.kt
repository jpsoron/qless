package com.qless.domain.usecase

import com.qless.domain.model.CachedResult
import com.qless.domain.model.Local
import com.qless.domain.repository.LocalesRepository

class GetLocalesUseCase(private val repository: LocalesRepository) {
    suspend operator fun invoke(): Result<CachedResult<List<Local>>> = repository.getLocales()
}

class GetFavoritosUseCase(private val repository: LocalesRepository) {
    suspend operator fun invoke(ids: List<String>): Result<CachedResult<List<Local>>> =
        repository.getFavoritos(ids)
}

class GetLocalByIdUseCase(private val repository: LocalesRepository) {
    suspend operator fun invoke(id: String): Result<CachedResult<Local?>> =
        repository.getLocalById(id)
}
