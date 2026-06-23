package com.qless.domain.notification

import com.qless.domain.model.AppNotification

/**
 * Abstracción de la bandeja de notificaciones del sistema. El dominio depende de
 * este contrato; la implementación con `NotificationManager` vive en `data/` y se
 * inyecta desde `AppModule` (mismo patrón que [com.qless.domain.location.LocationProvider]).
 */
interface SystemNotifier {
    /** Publica [notification] en la bandeja del sistema. No-op si falta el permiso. */
    fun notify(notification: AppNotification)
}
