package com.qless.domain.usecase

import com.qless.domain.model.AppNotification
import com.qless.domain.model.Order
import com.qless.domain.notification.SystemNotifier
import com.qless.domain.repository.NotificationRepository
import com.qless.domain.session.SessionProvider
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Traduce un cambio de estado de un pedido en un aviso: lo persiste en el centro
 * de notificaciones (Room) y lo publica en la bandeja del sistema. Android queda
 * detrás de [SystemNotifier], no dentro del ViewModel.
 */
class NotifyOrderUpdateUseCase(
    private val repository: NotificationRepository,
    private val notifier: SystemNotifier,
) {
    suspend operator fun invoke(order: Order) {
        val text = statusToText(order.status, order.localNombre) ?: return
        val notification = AppNotification(
            id = UUID.randomUUID().toString(),
            userId = order.userId,
            orderId = order.id,
            orderNumero = order.numero,
            localNombre = order.localNombre,
            title = text.title,
            body = text.body,
            status = order.status,
            createdAt = System.currentTimeMillis(),
        )
        repository.add(notification)
        notifier.notify(notification)
    }

    private data class NotificationText(val title: String, val body: String)

    /** Mapeo estado→texto. `null` para estados que no ameritan aviso. */
    private fun statusToText(status: String, local: String): NotificationText? {
        val localLabel = local.ifBlank { "tu local" }
        return when (status) {
            "pending"   -> NotificationText("Pago confirmado", "Tu pedido en $localLabel fue confirmado.")
            "preparing" -> NotificationText("En preparación", "$localLabel está preparando tu pedido.")
            "ready"     -> NotificationText("¡Listo para retirar!", "Tu pedido en $localLabel está listo. Acercate al mostrador.")
            "picked_up" -> NotificationText("Pedido retirado", "Retiraste tu pedido en $localLabel. ¡Buen provecho!")
            "cancelled" -> NotificationText("Pedido cancelado", "Tu pedido en $localLabel fue cancelado.")
            else        -> null
        }
    }
}

class ObserveNotificationsUseCase(private val repository: NotificationRepository) {
    operator fun invoke(userId: String): Flow<List<AppNotification>> = repository.observe(userId)
}

class ObserveUnreadCountUseCase(private val repository: NotificationRepository) {
    operator fun invoke(userId: String): Flow<Int> = repository.unreadCount(userId)
}

class MarkNotificationsReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(userId: String) = repository.markAllRead(userId)
}

class ClearNotificationsUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(userId: String) = repository.clear(userId)
}

class GetCurrentUserIdUseCase(private val sessionProvider: SessionProvider) {
    operator fun invoke(): String? = sessionProvider.currentUserId()
}
