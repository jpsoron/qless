package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.model.AppNotification
import com.qless.domain.usecase.ClearNotificationsUseCase
import com.qless.domain.usecase.GetCurrentUserIdUseCase
import com.qless.domain.usecase.MarkNotificationsReadUseCase
import com.qless.domain.usecase.ObserveNotificationsUseCase
import com.qless.domain.usecase.ObserveUnreadCountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val items: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0,
)

/**
 * Centro de notificaciones del cliente. Observa los avisos persistidos del usuario
 * logueado y el contador de no-leídos (badge de la campana en Home).
 */
class NotificationViewModel(
    private val getCurrentUserId: GetCurrentUserIdUseCase,
    private val observeNotifications: ObserveNotificationsUseCase,
    private val observeUnreadCount: ObserveUnreadCountUseCase,
    private val markNotificationsRead: MarkNotificationsReadUseCase,
    private val clearNotifications: ClearNotificationsUseCase,
) : ViewModel() {

    /** Constructor sin args para `viewModel()` en producción: toma el grafo de [AppModule]. */
    constructor() : this(
        AppModule.getCurrentUserId,
        AppModule.observeNotifications,
        AppModule.observeUnreadCount,
        AppModule.markNotificationsRead,
        AppModule.clearNotifications,
    )

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private var started = false

    /** Arranca la observación una sola vez, cuando ya hay sesión. Idempotente. */
    fun start() {
        if (started) return
        val userId = getCurrentUserId() ?: return
        started = true
        viewModelScope.launch {
            observeNotifications(userId).collect { list -> _uiState.update { it.copy(items = list) } }
        }
        viewModelScope.launch {
            observeUnreadCount(userId).collect { count -> _uiState.update { it.copy(unreadCount = count) } }
        }
    }

    fun markAllRead() {
        val userId = getCurrentUserId() ?: return
        viewModelScope.launch { markNotificationsRead(userId) }
    }

    fun clearAll() {
        val userId = getCurrentUserId() ?: return
        viewModelScope.launch { clearNotifications(userId) }
    }
}
