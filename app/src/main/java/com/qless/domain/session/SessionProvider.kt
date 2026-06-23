package com.qless.domain.session

/**
 * Acceso a la identidad de la sesión activa, sin acoplar el dominio a Supabase.
 * La implementación (sobre `SupabaseClient.auth`) vive en `data/`.
 */
interface SessionProvider {
    /** Id de Supabase del usuario logueado, o `null` si no hay sesión. */
    fun currentUserId(): String?
}
