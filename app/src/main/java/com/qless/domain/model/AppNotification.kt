package com.qless.domain.model

/**
 * Aviso in-app sobre el cambio de estado de un pedido. Se persiste en Room (centro
 * de notificaciones) y, en paralelo, se publica en la bandeja del sistema.
 * `userId` es el id de Supabase del dueño, para no mezclar avisos entre usuarios
 * que compartan el mismo dispositivo.
 */
data class AppNotification(
    val id: String,
    val userId: String,
    val orderId: String,
    val orderNumero: Int,
    val localNombre: String,
    val title: String,
    val body: String,
    val status: String,
    val createdAt: Long,
    val read: Boolean = false,
)
