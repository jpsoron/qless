package com.qless.domain.usecase

import com.qless.domain.model.NotificationPreferences
import com.qless.domain.repository.NotificationPreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveNotificationPreferencesUseCase(private val repository: NotificationPreferencesRepository) {
    operator fun invoke(): Flow<NotificationPreferences> = repository.preferences
}

class SetOrderStatusNotificationUseCase(private val repository: NotificationPreferencesRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.setOrderStatus(enabled)
}

class SetOrderReadyNotificationUseCase(private val repository: NotificationPreferencesRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.setOrderReady(enabled)
}

class SetSoundVibrationNotificationUseCase(private val repository: NotificationPreferencesRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.setSoundVibration(enabled)
}
