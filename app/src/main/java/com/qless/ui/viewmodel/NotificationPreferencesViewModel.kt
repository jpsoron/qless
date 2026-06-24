package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.NotificationPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationPreferencesViewModel : ViewModel() {

    private val observePreferences = AppModule.observeNotificationPreferences
    private val setOrderStatusUseCase = AppModule.setOrderStatusNotification
    private val setOrderReadyUseCase = AppModule.setOrderReadyNotification
    private val setSoundVibrationUseCase = AppModule.setSoundVibrationNotification

    val uiState: StateFlow<NotificationPreferences> = observePreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotificationPreferences())

    fun setOrderStatus(enabled: Boolean) {
        viewModelScope.launch { setOrderStatusUseCase(enabled) }
    }

    fun setOrderReady(enabled: Boolean) {
        viewModelScope.launch { setOrderReadyUseCase(enabled) }
    }

    fun setSoundVibration(enabled: Boolean) {
        viewModelScope.launch { setSoundVibrationUseCase(enabled) }
    }
}
