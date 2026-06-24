package com.qless.ui.screens.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.domain.model.AppNotification
import com.qless.ui.theme.*
import java.util.concurrent.TimeUnit

/**
 * Centro de notificaciones (pantalla nueva, separada de las preferencias de
 * `NotificacionesScreen`). Lista los avisos de cambios de estado del pedido,
 * los marca como leídos al abrir, y permite borrarlos. Tocar un aviso lleva al
 * seguimiento del pedido.
 */
@Composable
fun NotificationCenterScreen(
    notifications: List<AppNotification>,
    onBack: () -> Unit,
    onNotificationClick: (AppNotification) -> Unit,
    onMarkAllRead: () -> Unit,
    onClearAll: () -> Unit,
) {
    // Al abrir, marcamos todo como leído (resetea el badge de la campana).
    LaunchedEffect(Unit) { onMarkAllRead() }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp).statusBarsPadding())

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    "Notificaciones",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                )
                if (notifications.isNotEmpty()) {
                    IconButton(onClick = onClearAll) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = "Borrar todas", tint = Pimentón)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (notifications.isEmpty()) {
                EmptyNotifications()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(notifications, key = { it.id }) { notification ->
                        NotificationCard(notification = notification, onClick = { onNotificationClick(notification) })
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: AppNotification, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val accent = statusAccent(notification.status)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(statusEmoji(notification.status), fontSize = 20.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    notification.title,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    notification.body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Pedido #${notification.orderNumero} · ${relativeTime(notification.createdAt)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (!notification.read) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Pimentón),
                )
            }
        }
    }
}

@Composable
private fun EmptyNotifications() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Outlined.NotificationsNone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Todavía no tenés notificaciones",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            "Te avisamos cuando cambie el estado de tu pedido",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

private fun statusEmoji(status: String): String = when (status) {
    "pending"   -> "✅"
    "preparing" -> "👨‍🍳"
    "ready"     -> "🛎️"
    "picked_up" -> "🎉"
    "cancelled" -> "❌"
    else        -> "🔔"
}

private fun statusAccent(status: String) = when (status) {
    "ready", "picked_up" -> QLessStatusColors.disponible
    "preparing"          -> QLessStatusColors.enPreparacion
    "cancelled"          -> Pimentón
    else                 -> Albahaca
}

private fun relativeTime(createdAt: Long): String {
    val diff = System.currentTimeMillis() - createdAt
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        minutes < 1 -> "recién"
        minutes < 60 -> "hace ${minutes} min"
        hours < 24 -> "hace ${hours} h"
        days < 7 -> "hace ${days} d"
        else -> "hace ${days / 7} sem"
    }
}

@Preview
@Composable
private fun NotificationCenterPreview() {
    QLessTheme {
        NotificationCenterScreen(
            notifications = listOf(
                AppNotification("1", "u1", "o1", 4521, "Big Pons", "¡Listo para retirar!", "Tu pedido en Big Pons está listo.", "ready", System.currentTimeMillis() - 120_000, false),
                AppNotification("2", "u1", "o1", 4521, "Big Pons", "En preparación", "Big Pons está preparando tu pedido.", "preparing", System.currentTimeMillis() - 600_000, true),
            ),
            onBack = {},
            onNotificationClick = {},
            onMarkAllRead = {},
            onClearAll = {},
        )
    }
}
