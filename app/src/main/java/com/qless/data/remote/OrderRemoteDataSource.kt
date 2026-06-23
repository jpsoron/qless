package com.qless.data.remote

import com.qless.domain.model.CartItem
import com.qless.domain.model.Order
import com.qless.data.remote.dto.NewOrderDto
import io.github.jan.supabase.auth.auth
import com.qless.data.remote.dto.NewOrderItemDto
import com.qless.data.remote.dto.OrderDto
import com.qless.data.remote.dto.OrderIdDto
import com.qless.data.remote.dto.PerfilLocalDto
import com.qless.data.remote.dto.toDomain
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class OrderRemoteDataSource {

    private val client = SupabaseClient.instance

    suspend fun createOrder(items: List<CartItem>, localId: String): Result<Order> = runCatching {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        val total = items.sumOf { it.unitPrice * it.quantity }

        val created = client.from("orders")
            .insert(NewOrderDto(userId = userId, localId = localId, totalAmount = total)) {
                select(Columns.list("id"))
            }
            .decodeSingle<OrderIdDto>()

        val itemsPayload = items.map { item ->
            NewOrderItemDto(
                orderId = created.id,
                menuItemId = item.menuItemId,
                nombre = item.name,
                unitPrice = item.unitPrice,
                quantity = item.quantity,
            )
        }
        client.from("order_items").insert(itemsPayload)

        client.from("orders")
            .select(Columns.raw("*, order_items(*), locales(nombre, emoji)")) {
                filter { eq("id", created.id) }
            }
            .decodeSingle<OrderDto>()
            .toDomain()
    }

    suspend fun getOrdersByUser(): Result<List<Order>> = runCatching {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        client.from("orders")
            .select(Columns.raw("*, order_items(*), locales(nombre, emoji)")) {
                filter { eq("user_id", userId) }
            }
            .decodeList<OrderDto>()
            .sortedByDescending { it.createdAt }
            .map { it.toDomain() }
    }

    /** Obtiene el local_id del perfil BackOffice logueado. */
    private suspend fun getLocalIdForCurrentUser(): String {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        val perfil = client.from("perfiles")
            .select(Columns.list("local_id")) {
                filter { eq("id", userId) }
            }
            .decodeSingle<PerfilLocalDto>()
        return perfil.localId ?: error("Este usuario BackOffice no tiene un local asignado")
    }

    /** Pedidos activos del local: pending, preparing, ready. */
    suspend fun getActiveOrdersForLocal(): Result<List<Order>> = runCatching {
        val localId = getLocalIdForCurrentUser()
        client.from("orders")
            .select(Columns.raw("*, order_items(*), locales(nombre, emoji)")) {
                filter {
                    eq("local_id", localId)
                    neq("status", "picked_up")
                    neq("status", "cancelled")
                }
            }
            .decodeList<OrderDto>()
            .sortedByDescending { it.createdAt }
            .map { it.toDomain() }
    }

    /** Pedidos completados del local: picked_up. */
    suspend fun getCompletedOrdersForLocal(): Result<List<Order>> = runCatching {
        val localId = getLocalIdForCurrentUser()
        client.from("orders")
            .select(Columns.raw("*, order_items(*), locales(nombre, emoji)")) {
                filter {
                    eq("local_id", localId)
                    eq("status", "picked_up")
                }
            }
            .decodeList<OrderDto>()
            .sortedByDescending { it.createdAt }
            .map { it.toDomain() }
    }

    suspend fun updateStatus(orderId: String, newStatus: String): Result<Unit> = runCatching {
        client.from("orders")
            .update({ set("status", newStatus) }) {
                filter { eq("id", orderId) }
            }
    }

    /**
     * Señal de cambios en vivo de los pedidos del usuario logueado (canal Realtime).
     * Emite `Unit` al (re)suscribirse y ante cada INSERT/UPDATE/DELETE sobre `orders`
     * filtrado por `user_id`. La señal NO trae los embeds (`order_items`/`locales`);
     * el llamador re-ejecuta la query REST que sí los trae.
     */
    fun observeUserOrderChanges(): Flow<Unit> = flow {
        val userId = client.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        emitOrderChanges(channelId = "orders-user-$userId", column = "user_id", value = userId)
    }

    /** Igual que [observeUserOrderChanges] pero filtrado por el local del BackOffice logueado. */
    fun observeLocalOrderChanges(): Flow<Unit> = flow {
        val localId = getLocalIdForCurrentUser()
        emitOrderChanges(channelId = "orders-local-$localId", column = "local_id", value = localId)
    }

    /**
     * Crea y suscribe un canal de Postgres Changes sobre `orders`, emitiendo una señal
     * inmediata (para el fetch inicial, independiente de que el WebSocket conecte) y una
     * por cada evento. Al cancelarse el colector, desuscribe y libera el canal.
     */
    private suspend fun FlowCollector<Unit>.emitOrderChanges(
        channelId: String,
        column: String,
        value: String,
    ) {
        emit(Unit) // fetch inicial inmediato
        val channel = client.channel(channelId)
        val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "orders"
            filter(column, FilterOperator.EQ, value)
        }
        try {
            channel.subscribe()
            changes.collect { emit(Unit) }
        } finally {
            withContext(NonCancellable) {
                channel.unsubscribe()
                client.realtime.removeChannel(channel)
            }
        }
    }
}
