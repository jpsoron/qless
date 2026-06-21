package com.qless.di

import android.content.Context
import com.qless.data.local.QLessDatabase
import com.qless.data.repository.CartRepositoryImpl
import com.qless.data.repository.LocalesRepositoryImpl
import com.qless.data.repository.MenuRepositoryImpl
import com.qless.data.repository.OrderRepositoryImpl
import com.qless.data.repository.PaymentMethodRepositoryImpl
import com.qless.data.repository.ThemeRepositoryImpl
import com.qless.data.repository.UserRepositoryImpl
import com.qless.domain.repository.CartRepository
import com.qless.domain.repository.LocalesRepository
import com.qless.domain.repository.MenuRepository
import com.qless.domain.repository.OrderRepository
import com.qless.domain.repository.PaymentMethodRepository
import com.qless.domain.repository.ThemeRepository
import com.qless.domain.repository.UserRepository
import com.qless.domain.usecase.AddCartItemUseCase
import com.qless.domain.usecase.AddPaymentMethodUseCase
import com.qless.domain.usecase.ClearCartUseCase
import com.qless.domain.usecase.ClearSessionUseCase
import com.qless.domain.usecase.DeleteAccountUseCase
import com.qless.domain.usecase.EnsureDefaultPaymentMethodsUseCase
import com.qless.domain.usecase.GetActiveLocalOrdersUseCase
import com.qless.domain.usecase.GetCompletedLocalOrdersUseCase
import com.qless.domain.usecase.GetFavoritosUseCase
import com.qless.domain.usecase.GetLocalesUseCase
import com.qless.domain.usecase.GetMenuUseCase
import com.qless.domain.usecase.GetUserOrdersUseCase
import com.qless.domain.usecase.LoginUseCase
import com.qless.domain.usecase.LogoutUseCase
import com.qless.domain.usecase.ObserveCartUseCase
import com.qless.domain.usecase.ObserveDarkModeUseCase
import com.qless.domain.usecase.ObserveOnboardingCompletedUseCase
import com.qless.domain.usecase.ObservePaymentMethodsUseCase
import com.qless.domain.usecase.PlaceOrderUseCase
import com.qless.domain.usecase.RegisterUseCase
import com.qless.domain.usecase.RemovePaymentMethodUseCase
import com.qless.domain.usecase.RestoreSessionUseCase
import com.qless.domain.usecase.SetDarkModeUseCase
import com.qless.domain.usecase.SetOnboardingCompletedUseCase
import com.qless.domain.usecase.ToggleFavoritoUseCase
import com.qless.domain.usecase.UpdateCartItemQuantityUseCase
import com.qless.domain.usecase.UpdateOrderStatusUseCase

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

    // --- Casos de uso: pedidos (dominio principal) ---
    val placeOrder by lazy { PlaceOrderUseCase(orderRepository) }
    val getUserOrders by lazy { GetUserOrdersUseCase(orderRepository) }
    val getActiveLocalOrders by lazy { GetActiveLocalOrdersUseCase(orderRepository) }
    val getCompletedLocalOrders by lazy { GetCompletedLocalOrdersUseCase(orderRepository) }
    val updateOrderStatus by lazy { UpdateOrderStatusUseCase(orderRepository) }

    // --- Menú ---
    val getMenu by lazy { GetMenuUseCase(menuRepository) }

    // --- Locales ---
    val getLocales by lazy { GetLocalesUseCase(localesRepository) }
    val getFavoritos by lazy { GetFavoritosUseCase(localesRepository) }

    // --- Carrito ---
    val observeCart by lazy { ObserveCartUseCase(cartRepository) }
    val addCartItem by lazy { AddCartItemUseCase(cartRepository) }
    val updateCartItemQuantity by lazy { UpdateCartItemQuantityUseCase(cartRepository) }
    val clearCart by lazy { ClearCartUseCase(cartRepository) }

    // --- Medios de pago ---
    val observePaymentMethods by lazy { ObservePaymentMethodsUseCase(paymentRepository) }
    val ensureDefaultPaymentMethods by lazy { EnsureDefaultPaymentMethodsUseCase(paymentRepository) }
    val addPaymentMethod by lazy { AddPaymentMethodUseCase(paymentRepository) }
    val removePaymentMethod by lazy { RemovePaymentMethodUseCase(paymentRepository) }

    // --- Autenticación ---
    val login by lazy { LoginUseCase(userRepository) }
    val register by lazy { RegisterUseCase(userRepository) }
    val logout by lazy { LogoutUseCase(userRepository) }
    val restoreSession by lazy { RestoreSessionUseCase(userRepository) }
    val clearSession by lazy { ClearSessionUseCase(userRepository) }
    val toggleFavorito by lazy { ToggleFavoritoUseCase(userRepository) }
    val deleteAccount by lazy { DeleteAccountUseCase(userRepository) }

    // --- Tema / onboarding ---
    val observeDarkMode by lazy { ObserveDarkModeUseCase(themeRepository) }
    val observeOnboardingCompleted by lazy { ObserveOnboardingCompletedUseCase(themeRepository) }
    val setDarkMode by lazy { SetDarkModeUseCase(themeRepository) }
    val setOnboardingCompleted by lazy { SetOnboardingCompletedUseCase(themeRepository) }
}
