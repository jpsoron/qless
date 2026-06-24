package com.qless.fakes

import com.qless.domain.location.LocationProvider
import com.qless.domain.model.AppNotification
import com.qless.domain.model.CachedResult
import com.qless.domain.model.CartItem
import com.qless.domain.model.Coordinates
import com.qless.domain.model.Local
import com.qless.domain.model.MenuItem
import com.qless.domain.model.NotificationPreferences
import com.qless.domain.model.Order
import com.qless.domain.notification.SystemNotifier
import com.qless.domain.repository.CartRepository
import com.qless.domain.repository.LocalesRepository
import com.qless.domain.repository.MenuRepository
import com.qless.domain.repository.NotificationPreferencesRepository
import com.qless.domain.repository.NotificationRepository
import com.qless.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Fakes en memoria de los contratos de dominio. Se prefieren a un framework de
 * mocking: son deterministas, legibles y no agregan dependencias a resolver.
 */

class FakeOrderRepository(
    var userOrders: Result<List<Order>> = Result.success(emptyList()),
    var activeLocalOrders: Result<List<Order>> = Result.success(emptyList()),
    var completedLocalOrders: Result<List<Order>> = Result.success(emptyList()),
    var createResult: ((List<CartItem>, String) -> Result<Order>)? = null,
    var userOrderChanges: Flow<Unit> = emptyFlow(),
    var localOrderChanges: Flow<Unit> = emptyFlow(),
) : OrderRepository {

    val createdOrders = mutableListOf<Pair<List<CartItem>, String>>()
    val statusUpdates = mutableListOf<Pair<String, String>>()
    var updateResult: Result<Unit> = Result.success(Unit)

    override suspend fun createOrder(
        items: List<CartItem>,
        localId: String,
        applyFirstOrderDiscount: Boolean,
    ): Result<Order> {
        createdOrders += items to localId
        return createResult?.invoke(items, localId)
            ?: Result.success(sampleOrder(localId = localId, totalAmount = items.sumOf { it.unitPrice * it.quantity }))
    }

    override suspend fun getOrdersByUser(): Result<List<Order>> = userOrders
    override suspend fun getActiveOrdersForLocal(): Result<List<Order>> = activeLocalOrders
    override suspend fun getCompletedOrdersForLocal(): Result<List<Order>> = completedLocalOrders

    override suspend fun updateStatus(orderId: String, status: String): Result<Unit> {
        statusUpdates += orderId to status
        return updateResult
    }

    override fun observeUserOrderChanges(): Flow<Unit> = userOrderChanges
    override fun observeLocalOrderChanges(): Flow<Unit> = localOrderChanges
}

class FakeNotificationRepository : NotificationRepository {
    val added = mutableListOf<AppNotification>()
    private val items = MutableStateFlow<List<AppNotification>>(emptyList())

    override fun observe(userId: String): Flow<List<AppNotification>> = items
    override fun unreadCount(userId: String): Flow<Int> =
        MutableStateFlow(added.count { it.userId == userId && !it.read })

    override suspend fun add(notification: AppNotification) {
        added += notification
        items.value = items.value + notification
    }

    override suspend fun markAllRead(userId: String) {}
    override suspend fun clear(userId: String) {
        added.clear()
        items.value = emptyList()
    }
}

class FakeSystemNotifier : SystemNotifier {
    val notified = mutableListOf<AppNotification>()
    override fun notify(notification: AppNotification, sound: Boolean) { notified += notification }
}

class FakeNotificationPreferencesRepository(
    private var prefs: NotificationPreferences = NotificationPreferences(),
) : NotificationPreferencesRepository {
    override val preferences = MutableStateFlow(prefs)
    override suspend fun current(): NotificationPreferences = prefs
    override suspend fun setOrderStatus(enabled: Boolean) { prefs = prefs.copy(orderStatus = enabled); preferences.value = prefs }
    override suspend fun setOrderReady(enabled: Boolean) { prefs = prefs.copy(orderReady = enabled); preferences.value = prefs }
    override suspend fun setSoundVibration(enabled: Boolean) { prefs = prefs.copy(soundVibration = enabled); preferences.value = prefs }
}

class FakeCartRepository(
    initial: List<CartItem> = emptyList(),
) : CartRepository {

    val items = MutableStateFlow(initial)
    val addCalls = mutableListOf<CartItem>()
    val updateCalls = mutableListOf<Pair<CartItem, Int>>()
    var clearCount = 0

    override fun getItems(): Flow<List<CartItem>> = items

    override suspend fun addItem(
        emoji: String,
        name: String,
        detail: String,
        unitPrice: Int,
        currentQuantity: Int,
        menuItemId: String,
        localId: String,
    ) {
        // Reproduce la semántica real: incrementa cantidad respecto de la actual.
        addCalls += CartItem(emoji, name, detail, unitPrice, currentQuantity + 1, menuItemId, localId)
        val newQuantity = currentQuantity + 1
        val existing = items.value.firstOrNull { it.name == name }
        items.value = if (existing == null) {
            items.value + CartItem(emoji, name, detail, unitPrice, newQuantity, menuItemId, localId)
        } else {
            items.value.map { if (it.name == name) it.copy(quantity = newQuantity) else it }
        }
    }

    override suspend fun updateQuantity(item: CartItem, newQuantity: Int) {
        updateCalls += item to newQuantity
        items.value = if (newQuantity <= 0) {
            items.value.filterNot { it.name == item.name }
        } else {
            items.value.map { if (it.name == item.name) it.copy(quantity = newQuantity) else it }
        }
    }

    override suspend fun clearCart() {
        clearCount++
        items.value = emptyList()
    }
}

class FakeLocalesRepository(
    var localesResult: Result<CachedResult<List<Local>>> = Result.success(CachedResult(emptyList(), false)),
    var favoritosResult: Result<CachedResult<List<Local>>> = Result.success(CachedResult(emptyList(), false)),
    var localByIdResult: Result<CachedResult<Local?>> = Result.success(CachedResult(null, false)),
) : LocalesRepository {
    var lastFavoritosIds: List<String>? = null
    var lastLocalByIdQuery: String? = null
    override suspend fun getLocales(): Result<CachedResult<List<Local>>> = localesResult
    override suspend fun getFavoritos(ids: List<String>): Result<CachedResult<List<Local>>> {
        lastFavoritosIds = ids
        return favoritosResult
    }
    override suspend fun getLocalById(id: String): Result<CachedResult<Local?>> {
        lastLocalByIdQuery = id
        return localByIdResult
    }
}

class FakeMenuRepository(
    var menuResult: Result<CachedResult<List<MenuItem>>> = Result.success(CachedResult(emptyList(), false)),
) : MenuRepository {
    var lastLocalId: String? = null
    override suspend fun getMenu(localId: String): Result<CachedResult<List<MenuItem>>> {
        lastLocalId = localId
        return menuResult
    }
}

class FakeLocationProvider(
    var coordinates: Coordinates? = null,
) : LocationProvider {
    override suspend fun currentLocation(): Coordinates? = coordinates
}

// --- Builders de muestra ---

fun sampleOrder(
    id: String = "o1",
    numero: Int = 1,
    status: String = "pending",
    localId: String = "l1",
    localNombre: String = "Local Demo",
    totalAmount: Int = 1000,
    createdAt: String = "2026-06-21T12:00:00Z",
): Order = Order(
    id = id,
    numero = numero,
    userId = "u1",
    localId = localId,
    localNombre = localNombre,
    localEmoji = "🍔",
    status = status,
    totalAmount = totalAmount,
    createdAt = createdAt,
)

fun sampleLocal(
    id: String = "l1",
    nombre: String = "Local Demo",
    latitud: Double = 0.0,
    longitud: Double = 0.0,
): Local = Local(
    id = id,
    emoji = "🍔",
    nombre = nombre,
    categoria = "Hamburguesas",
    barrio = "Centro",
    rating = "4.5",
    tiempoEntrega = "20 min",
    abierto = true,
    tienePromo = false,
    destacado = false,
    latitud = latitud,
    longitud = longitud,
)

fun sampleMenuItem(
    id: String = "m1",
    nombre: String = "Item",
    categoria: String = "General",
    esPopular: Boolean = false,
): MenuItem = MenuItem(
    id = id,
    localId = "l1",
    emoji = "🍔",
    nombre = nombre,
    descripcion = "desc",
    precio = 500,
    categoria = categoria,
    esPopular = esPopular,
)
