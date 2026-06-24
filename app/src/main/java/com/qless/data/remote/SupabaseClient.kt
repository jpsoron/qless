package com.qless.data.remote

import com.qless.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import kotlin.time.Duration.Companion.seconds

object SupabaseClient {
    val instance = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        requestTimeout = 30.seconds
        install(Auth) {
            // Deep link de recuperación de contraseña: qless://reset-password.
            // Debe coincidir con el intent-filter del manifest, con la URL en
            // AuthRemoteDataSource.PASSWORD_RESET_REDIRECT y con la allowlist de
            // Redirect URLs del proyecto Supabase. Flujo implícito: el token llega
            // en el fragment del link, sin code-verifier que deba sobrevivir al
            // cierre de la app (más robusto cuando el mail se abre con la app cerrada).
            scheme = "qless"
            host = "reset-password"
            flowType = FlowType.IMPLICIT
        }
        install(Postgrest)
        install(Realtime)
    }
}
