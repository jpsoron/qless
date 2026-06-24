package com.qless.domain.model

/**
 * Preferencias de avisos del cliente. Por defecto todo activado.
 * - [orderStatus]: cambios de estado del pedido (confirmado, en preparación, retirado, cancelado).
 * - [orderReady]: aviso específico de "listo para retirar".
 * - [soundVibration]: si los avisos del sistema suenan/vibran o son silenciosos.
 */
data class NotificationPreferences(
    val orderStatus: Boolean = true,
    val orderReady: Boolean = true,
    val soundVibration: Boolean = true,
)
