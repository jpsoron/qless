package com.qless.di

import android.content.Context
import com.qless.data.local.QLessDatabase
import com.qless.data.location.FusedLocationProvider
import com.qless.domain.location.LocationProvider
import com.qless.data.notification.AndroidSystemNotifier
import com.qless.data.repository.CartRepositoryImpl
import com.qless.data.repository.LocalesRepositoryImpl
import com.qless.data.repository.MenuRepositoryImpl
import com.qless.data.repository.NotificationPreferencesRepositoryImpl
import com.qless.data.repository.NotificationRepositoryImpl
import com.qless.data.repository.OrderRepositoryImpl
import com.qless.data.repository.PaymentMethodRepositoryImpl
import com.qless.data.repository.ThemeRepositoryImpl
import com.qless.data.repository.UserRepositoryImpl
import com.qless.data.session.SupabaseSessionProvider
import com.qless.domain.notification.SystemNotifier
import com.qless.domain.repository.CartRepository
import com.qless.domain.repository.LocalesRepository
import com.qless.domain.repository.MenuRepository
import com.qless.domain.repository.NotificationPreferencesRepository
import com.qless.domain.repository.NotificationRepository
import com.qless.domain.repository.OrderRepository
import com.qless.domain.repository.PaymentMethodRepository
import com.qless.domain.repository.ThemeRepository
import com.qless.domain.repository.UserRepository
import com.qless.domain.session.SessionProvider
import com.qless.domain.usecase.AddCartItemUseCase
import com.qless.domain.usecase.AddPaymentMethodUseCase
import com.qless.domain.usecase.ClearCartUseCase
import com.qless.domain.usecase.ClearSessionUseCase
import com.qless.domain.usecase.ConsumeFirstOrderDiscountUseCase
import com.qless.domain.usecase.DeleteAccountUseCase
import com.qless.domain.usecase.GetActiveLocalOrdersUseCase
import com.qless.domain.usecase.GetCompletedLocalOrdersUseCase
import com.qless.domain.usecase.GetCurrentLocationUseCase
import com.qless.domain.usecase.GetFavoritosUseCase
import com.qless.domain.usecase.GetLocalByIdUseCase
import com.qless.domain.usecase.GetLocalesUseCase
import com.qless.domain.usecase.GetMenuUseCase
import com.qless.domain.usecase.GetUserOrdersUseCase
import com.qless.domain.usecase.ClearNotificationsUseCase
import com.qless.domain.usecase.GetCurrentUserIdUseCase
import com.qless.domain.usecase.LoginUseCase
import com.qless.domain.usecase.LoginWithGoogleUseCase
import com.qless.domain.usecase.LogoutUseCase
import com.qless.domain.usecase.MarkNotificationsReadUseCase
import com.qless.domain.usecase.NotifyOrderUpdateUseCase
import com.qless.domain.usecase.ObserveCartUseCase
import com.qless.domain.usecase.ObserveLocalOrderChangesUseCase
import com.qless.domain.usecase.ObserveNotificationPreferencesUseCase
import com.qless.domain.usecase.ObserveNotificationsUseCase
import com.qless.domain.usecase.SetOrderReadyNotificationUseCase
import com.qless.domain.usecase.SetOrderStatusNotificationUseCase
import com.qless.domain.usecase.SetSoundVibrationNotificationUseCase
import com.qless.domain.usecase.ObserveUnreadCountUseCase
import com.qless.domain.usecase.ObserveUserOrderChangesUseCase
import com.qless.domain.usecase.ObserveDarkModeUseCase
import com.qless.domain.usecase.ObserveOnboardingCompletedUseCase
import com.qless.domain.usecase.ObservePaymentMethodsUseCase
import com.qless.domain.usecase.PlaceOrderUseCase
import com.qless.domain.usecase.RankLocalsByDistanceUseCase
import com.qless.domain.usecase.RegisterUseCase
import com.qless.domain.usecase.RemovePaymentMethodUseCase
import com.qless.domain.usecase.RestoreSessionUseCase
import com.qless.domain.usecase.SendPasswordResetUseCase
import com.qless.domain.usecase.SetDarkModeUseCase
import com.qless.domain.usecase.SetOnboardingCompletedUseCase
import com.qless.domain.usecase.ToggleFavoritoUseCase
import com.qless.domain.usecase.UpdateProfileUseCase
import com.qless.domain.usecase.UpdateCartItemQuantityUseCase
import com.qless.domain.usecase.UpdateOrderStatusUseCase
import com.qless.domain.usecase.UpdatePasswordUseCase

/**
 * Composition root manual (sin Hilt). Arma el grafo de dependencias:
 * implementaciones de data → contratos de dominio → casos de uso.
 *
 * Los ViewModels piden sus casos de uso acá, dependiendo de abstracciones de
 * dominio y no de clases concretas de la capa de datos.
 *
 * Debe inicializarse una vez con el contexto de aplicación
 * (ver MainActivity.onCreate) antes de crear cualquier ViewModel.
 */
object AppModule {

    private lateinit var appContext: Context

    fun init(context: Context) {
        if (::appContext.isInitialized) return
        appContext = context.applicationContext
    }

    private val database by lazy { QLessDatabase.getInstance(appContext) }

    // --- Repositorios: contrato de dominio, implementación de data ---
    private val orderRepository: OrderRepository by lazy { OrderRepositoryImpl() }
    private val menuRepository: MenuRepository by lazy { MenuRepositoryImpl(database.menuItemDao()) }
    private val localesRepository: LocalesRepository by lazy { LocalesRepositoryImpl(database.localDao()) }
    private val cartRepository: CartRepository by lazy { CartRepositoryImpl(database.cartItemDao()) }
    private val paymentRepository: PaymentMethodRepository by lazy { PaymentMethodRepositoryImpl(database.paymentMethodDao()) }
    private val userRepository: UserRepository by lazy { UserRepositoryImpl(database.userDao(), appContext) }
    private val themeRepository: ThemeRepository by lazy { ThemeRepositoryImpl(appContext) }
    private val locationProvider: LocationProvider by lazy { FusedLocationProvider(appContext) }
    private val notificationRepository: NotificationRepository by lazy { NotificationRepositoryImpl(database.notificationDao()) }
    private val notificationPreferencesRepository: NotificationPreferencesRepository by lazy { NotificationPreferencesRepositoryImpl(appContext) }
    private val systemNotifier: SystemNotifier by lazy { AndroidSystemNotifier(appContext) }
    private val sessionProvider: SessionProvider by lazy { SupabaseSessionProvider() }

    // --- Casos de uso: pedidos (dominio principal) ---
    val placeOrder by lazy { PlaceOrderUseCase(orderRepository) }
    val getUserOrders by lazy { GetUserOrdersUseCase(orderRepository) }
    val getActiveLocalOrders by lazy { GetActiveLocalOrdersUseCase(orderRepository) }
    val getCompletedLocalOrders by lazy { GetCompletedLocalOrdersUseCase(orderRepository) }
    val updateOrderStatus by lazy { UpdateOrderStatusUseCase(orderRepository) }
    val observeUserOrderChanges by lazy { ObserveUserOrderChangesUseCase(orderRepository) }
    val observeLocalOrderChanges by lazy { ObserveLocalOrderChangesUseCase(orderRepository) }

    // --- Notificaciones ---
    val notifyOrderUpdate by lazy { NotifyOrderUpdateUseCase(notificationRepository, systemNotifier, notificationPreferencesRepository) }
    val observeNotifications by lazy { ObserveNotificationsUseCase(notificationRepository) }
    val observeUnreadCount by lazy { ObserveUnreadCountUseCase(notificationRepository) }
    val markNotificationsRead by lazy { MarkNotificationsReadUseCase(notificationRepository) }
    val clearNotifications by lazy { ClearNotificationsUseCase(notificationRepository) }
    val getCurrentUserId by lazy { GetCurrentUserIdUseCase(sessionProvider) }

    // --- Preferencias de notificaciones ---
    val observeNotificationPreferences by lazy { ObserveNotificationPreferencesUseCase(notificationPreferencesRepository) }
    val setOrderStatusNotification by lazy { SetOrderStatusNotificationUseCase(notificationPreferencesRepository) }
    val setOrderReadyNotification by lazy { SetOrderReadyNotificationUseCase(notificationPreferencesRepository) }
    val setSoundVibrationNotification by lazy { SetSoundVibrationNotificationUseCase(notificationPreferencesRepository) }

    // --- Menú ---
    val getMenu by lazy { GetMenuUseCase(menuRepository) }

    // --- Locales ---
    val getLocales by lazy { GetLocalesUseCase(localesRepository) }
    val getFavoritos by lazy { GetFavoritosUseCase(localesRepository) }
    val getLocalById by lazy { GetLocalByIdUseCase(localesRepository) }

    // --- Ubicación (GPS) ---
    val getCurrentLocation by lazy { GetCurrentLocationUseCase(locationProvider) }
    val rankLocalsByDistance by lazy { RankLocalsByDistanceUseCase() }

    // --- Carrito ---
    val observeCart by lazy { ObserveCartUseCase(cartRepository) }
    val addCartItem by lazy { AddCartItemUseCase(cartRepository) }
    val updateCartItemQuantity by lazy { UpdateCartItemQuantityUseCase(cartRepository) }
    val clearCart by lazy { ClearCartUseCase(cartRepository) }

    // --- Medios de pago ---
    val observePaymentMethods by lazy { ObservePaymentMethodsUseCase(paymentRepository) }
    val addPaymentMethod by lazy { AddPaymentMethodUseCase(paymentRepository) }
    val removePaymentMethod by lazy { RemovePaymentMethodUseCase(paymentRepository) }

    // --- Autenticación ---
    val login by lazy { LoginUseCase(userRepository) }
    val loginWithGoogle by lazy { LoginWithGoogleUseCase(userRepository) }
    val register by lazy { RegisterUseCase(userRepository) }
    val logout by lazy { LogoutUseCase(userRepository) }
    val restoreSession by lazy { RestoreSessionUseCase(userRepository) }
    val clearSession by lazy { ClearSessionUseCase(userRepository) }
    val toggleFavorito by lazy { ToggleFavoritoUseCase(userRepository) }
    val deleteAccount by lazy { DeleteAccountUseCase(userRepository) }
    val sendPasswordReset by lazy { SendPasswordResetUseCase(userRepository) }
    val updatePassword by lazy { UpdatePasswordUseCase(userRepository) }
    val consumeFirstOrderDiscount by lazy { ConsumeFirstOrderDiscountUseCase(userRepository) }
    val updateProfile by lazy { UpdateProfileUseCase(userRepository) }

    // --- Tema / onboarding ---
    val observeDarkMode by lazy { ObserveDarkModeUseCase(themeRepository) }
    val observeOnboardingCompleted by lazy { ObserveOnboardingCompletedUseCase(themeRepository) }
    val setDarkMode by lazy { SetDarkModeUseCase(themeRepository) }
    val setOnboardingCompleted by lazy { SetOnboardingCompletedUseCase(themeRepository) }
}
