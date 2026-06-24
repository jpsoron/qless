package com.qless.data.remote

import com.qless.data.remote.dto.Perfil
import com.qless.domain.repository.EmailAlreadyInUseException
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable

class ProfileRemoteDataSource {
    suspend fun fetchProfile(): Result<Perfil> = runCatching {
        SupabaseClient.instance.from("perfiles").select().decodeSingle<Perfil>()
    }

    /**
     * Actualiza nombre y email del perfil. Valida que el correo no esté ya tomado
     * por OTRO perfil (chequeo a nivel app; conviene además un índice único en DB).
     * Nota: solo cambia la tabla `perfiles`, no el email de auth (login).
     */
    suspend fun updateProfile(nombre: String, email: String): Result<Unit> = runCatching {
        val userId = SupabaseClient.instance.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        val taken = SupabaseClient.instance.from("perfiles")
            .select(Columns.list("id")) {
                filter {
                    eq("email", email)
                    neq("id", userId)
                }
            }
            .decodeList<ProfileIdRow>()
            .isNotEmpty()
        if (taken) throw EmailAlreadyInUseException()
        SupabaseClient.instance.from("perfiles")
            .update({
                set("nombre", nombre)
                set("email", email)
            }) {
                filter { eq("id", userId) }
            }
    }

    /** Baja lógica: marca el perfil como inactivo (activo = false). */
    suspend fun deactivateAccount(): Result<Unit> = runCatching {
        val userId = SupabaseClient.instance.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        SupabaseClient.instance.from("perfiles")
            .update({ set("activo", false) }) {
                filter { eq("id", userId) }
            }
    }

    @Serializable
    private data class ProfileIdRow(val id: String)

    suspend fun updateFavoritos(newFavoritos: List<String>): Result<Unit> = runCatching {
        val userId = SupabaseClient.instance.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        SupabaseClient.instance.from("perfiles")
            .update({ set("favoritos", newFavoritos) }) {
                filter { eq("id", userId) }
            }
    }

    /** Marca el descuento de bienvenida como consumido (descuento_1ra = false). */
    suspend fun consumeFirstOrderDiscount(): Result<Unit> = runCatching {
        val userId = SupabaseClient.instance.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        SupabaseClient.instance.from("perfiles")
            .update({ set("descuento_1ra", false) }) {
                filter { eq("id", userId) }
            }
    }
}
