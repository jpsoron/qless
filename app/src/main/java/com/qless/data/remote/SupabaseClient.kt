package com.qless.data.remote

import com.qless.BuildConfig
import io.github.jan.supabase.auth.Auth
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
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}
