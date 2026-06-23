package com.qless.data.session

import com.qless.data.remote.SupabaseClient
import com.qless.domain.session.SessionProvider
import io.github.jan.supabase.auth.auth

class SupabaseSessionProvider : SessionProvider {
    override fun currentUserId(): String? =
        SupabaseClient.instance.auth.currentUserOrNull()?.id
}
