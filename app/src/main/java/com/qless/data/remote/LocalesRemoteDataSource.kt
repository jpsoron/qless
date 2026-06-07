package com.qless.data.remote

import com.qless.data.Local
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
}
