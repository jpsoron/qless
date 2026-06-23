package com.qless.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val isDarkMode: Flow<Boolean>
    val isOnboardingCompleted: Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setOnboardingCompleted()
}
