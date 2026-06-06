package com.qless.data.remote

import com.qless.data.remote.dto.Perfil
import io.github.jan.supabase.postgrest.from

class ProfileRemoteDataSource {
    suspend fun fetchProfile(): Result<Perfil> = runCatching {
        SupabaseClient.instance.from("perfiles").select().decodeSingle<Perfil>()
    }
}
