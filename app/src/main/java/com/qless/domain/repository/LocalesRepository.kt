package com.qless.domain.repository

import com.qless.domain.model.CachedResult
import com.qless.domain.model.Local

interface LocalesRepository {
    suspend fun getLocales(): Result<CachedResult<List<Local>>>
    suspend fun getFavoritos(ids: List<String>): Result<CachedResult<List<Local>>>

    /**
     * Busca un local por su id. `data == null` significa que el id no corresponde
     * a ningún local registrado (no es un error de red). Network-first con fallback
     * a la caché Room (RF4): solo propaga el error si falla la red **y** no hay caché.
     */
    suspend fun getLocalById(id: String): Result<CachedResult<Local?>>
}
