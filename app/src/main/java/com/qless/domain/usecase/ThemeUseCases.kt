package com.qless.domain.usecase

import com.qless.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class ObserveDarkModeUseCase(private val repository: ThemeRepository) {
    operator fun invoke(): Flow<Boolean> = repository.isDarkMode
}

class ObserveOnboardingCompletedUseCase(private val repository: ThemeRepository) {
    operator fun invoke(): Flow<Boolean> = repository.isOnboardingCompleted
}

class SetDarkModeUseCase(private val repository: ThemeRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDarkMode(enabled)
}

class SetOnboardingCompletedUseCase(private val repository: ThemeRepository) {
    suspend operator fun invoke() = repository.setOnboardingCompleted()
}
