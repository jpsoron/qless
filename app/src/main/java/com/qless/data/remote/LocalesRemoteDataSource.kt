package com.qless.data.remote

import com.qless.domain.model.Local
import com.qless.data.remote.dto.LocalDto
import com.qless.data.remote.dto.toDomain
import io.github.jan.supabase.postgrest.from

class LocalesRemoteDataSource {
    suspend fun fetchLocales(): Result<List<Local>> = runCatching {
        SupabaseClient.instance
            .from("locales")
            .select()
            .decodeList<LocalDto>()
            .map { it.toDomain() }
    }

    suspend fun fetchLocalesByIds(ids: List<String>): Result<List<Local>> = runCatching {
        if (ids.isEmpty()) return@runCatching emptyList()
        SupabaseClient.instance
            .from("locales")
            .select {
                filter {
                    isIn("id", ids)
                }
            }
            .decodeList<LocalDto>()
            .map { it.toDomain() }
    }
}
