package com.qless.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.qless.MainActivity
import com.qless.R
import com.qless.domain.model.AppNotification
import com.qless.domain.notification.SystemNotifier

/**
 * Implementación de [SystemNotifier] sobre `NotificationManager`. Crea el canal una
 * vez y publica cada aviso en la bandeja. El tap abre la app en el seguimiento del
 * pedido. No-op si falta `POST_NOTIFICATIONS` (Android 13+): el centro in-app igual
 * persiste el aviso.
 */
class AndroidSystemNotifier(private val context: Context) : SystemNotifier {

    init {
        createChannel()
    }

    override fun notify(notification: AppNotification) {
        if (!hasPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_NAVIGATE_TRACKING, true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notification.orderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val systemNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_qless_blanco)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Un id por pedido: el aviso nuevo reemplaza al anterior del mismo pedido.
        NotificationManagerCompat.from(context).notify(notification.orderId.hashCode(), systemNotification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Estado de pedidos",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Avisos cuando cambia el estado de tu pedido"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun hasPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val CHANNEL_ID = "order_status"
    }
}
