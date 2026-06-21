package com.qless.data.remote

import com.qless.domain.model.MenuItem
import com.qless.data.remote.dto.MenuItemDto
import com.qless.data.remote.dto.toDomain
import io.github.jan.supabase.postgrest.from

class MenuRemoteDataSource {
    suspend fun fetchMenuForLocal(localId: String): Result<List<MenuItem>> = runCatching {
        SupabaseClient.instance
            .from("menu_items")
            .select {
                filter { eq("local_id", localId) }
            }
            .decodeList<MenuItemDto>()
            .sortedBy { it.orden }
            .map { it.toDomain() }
    }
}
