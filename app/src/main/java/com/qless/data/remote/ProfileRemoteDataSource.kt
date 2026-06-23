package com.qless.data.remote

import com.qless.data.remote.dto.Perfil
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class ProfileRemoteDataSource {
    suspend fun fetchProfile(): Result<Perfil> = runCatching {
        SupabaseClient.instance.from("perfiles").select().decodeSingle<Perfil>()
    }

    suspend fun updateFavoritos(newFavoritos: List<String>): Result<Unit> = runCatching {
        val userId = SupabaseClient.instance.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        SupabaseClient.instance.from("perfiles")
            .update({ set("favoritos", newFavoritos) }) {
                filter { eq("id", userId) }
            }
    }
}
