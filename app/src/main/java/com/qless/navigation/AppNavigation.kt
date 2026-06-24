package com.qless.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.LaunchedEffect
import com.qless.ui.viewmodel.AuthViewModel
import com.qless.ui.viewmodel.CartViewModel
import com.qless.ui.viewmodel.HomeViewModel
import com.qless.ui.viewmodel.MenuViewModel
import com.qless.ui.viewmodel.MisLocalesViewModel
import com.qless.ui.viewmodel.NotificationViewModel
import com.qless.ui.viewmodel.OrderNavEvent
import com.qless.ui.viewmodel.OrderViewModel
import com.qless.ui.viewmodel.activeOrder
import com.qless.ui.viewmodel.PaymentMethodViewModel
import com.qless.ui.viewmodel.QrScanEvent
import com.qless.ui.viewmodel.QrScanViewModel
import com.qless.ui.viewmodel.ThemeViewModel
import com.qless.domain.model.Local
import com.qless.domain.usecase.NEARBY_THRESHOLD_METERS
import com.qless.ui.components.ActiveCartUi
import com.qless.ui.screens.*
import com.qless.ui.screens.backoffice.BackOfficeAjustesScreen
import com.qless.ui.screens.backoffice.BackOfficeHistoryScreen
import com.qless.ui.screens.backoffice.BackOfficeScreen
import com.qless.ui.screens.backoffice.BackOfficeUpdateOrderScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object GoogleLogin : Screen("google_login")
    object Register : Screen("register")
    object Home : Screen("home")
    object MisLocales : Screen("mis_locales")
    object LocationDetected : Screen("location_detected")
    object Menu : Screen("menu/{localId}") {
        fun route(localId: String) = "menu/$localId"
    }
    object Cart : Screen("cart")
    object Payment : Screen("payment")
    object OrderConfirmed : Screen("order_confirmed")
    object Tracking : Screen("tracking")
    object MisPedidos : Screen("mis_pedidos")
    object OrderSummary : Screen("order_summary")
    object ScanQr : Screen("scan_qr")
    object QrNoReconocido : Screen("qr_no_reconocido")
    object OrderReady : Screen("order_ready")
    object PickupSuccess : Screen("pickup_success")
    object Ajustes : Screen("ajustes")
    object MetodosDePago : Screen("metodos_de_pago")
    object AgregarMetodoDePago : Screen("agregar_metodo_pago")
    object CerrarSesion : Screen("cerrar_sesion")
    object Notificaciones : Screen("notificaciones")
    object NotificationCenter : Screen("notification_center")
    object EliminarCuenta : Screen("eliminar_cuenta")
    object BackOffice : Screen("back_office")
    object BackOfficeHistory : Screen("back_office_history")
    object BackOfficeUpdateOrder : Screen("back_office_update_order/{orderId}") {
        fun route(orderId: String) = "back_office_update_order/$orderId"
    }
    object BackOfficeAjustes : Screen("back_office_ajustes")
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    themeViewModel: ThemeViewModel,
    openTrackingSignal: Boolean = false,
    onTrackingSignalConsumed: () -> Unit = {},
) {
    val authViewModel: AuthViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()
    val paymentViewModel: PaymentMethodViewModel = viewModel()
    val misLocalesViewModel: MisLocalesViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    // Local detectado por GPS a ≤50 m, para la pantalla "¿Estás en X?".
    var detectedLocal by remember { mutableStateOf<Local?>(null) }
    // Se muestra una vez por sesión (se reinicia al relanzar la app).
    var locationPromptShown by rememberSaveable { mutableStateOf(false) }
    var pendingLocalId by remember { mutableStateOf<String?>(null) }
    var pendingPopUpRoute by remember { mutableStateOf<String?>(null) }

    fun navigateToMenu(localId: String, popUpRoute: String? = null) {
        val cartLocalId = cartViewModel.cartLocalId
        if (cartLocalId.isNotEmpty() && cartLocalId != localId && cartViewModel.uiState.value.items.isNotEmpty()) {
            pendingLocalId = localId
            pendingPopUpRoute = popUpRoute
        } else {
            if (popUpRoute != null) {
                navController.navigate(Screen.Menu.route(localId)) {
                    popUpTo(popUpRoute) { inclusive = true }
                }
            } else {
                navController.navigate(Screen.Menu.route(localId))
            }
        }
    }

    // Carrito activo (resuelto contra la lista de locales). Se muestra en inicio,
    // mis locales y mis pedidos; "Ver" lleva al menú de ese local.
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val localesStateForCart by misLocalesViewModel.uiState.collectAsStateWithLifecycle()
    val activeCart: ActiveCartUi? = cartState.items.let { items ->
        val first = items.firstOrNull()
        if (first == null || first.localId.isEmpty()) {
            null
        } else {
            val local = localesStateForCart.locales.firstOrNull { it.id == first.localId }
            ActiveCartUi(
                localId = first.localId,
                localNombre = local?.nombre ?: "Tu pedido",
                localEmoji = local?.emoji ?: first.emoji,
                itemCount = items.sumOf { it.quantity },
                totalAmount = items.sumOf { it.unitPrice * it.quantity },
            )
        }
    }
    val onViewCart: () -> Unit = { activeCart?.let { navigateToMenu(it.localId) } }

    val onboardingCompleted by themeViewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

    // Tiempo real (Supabase Realtime) a nivel app: mientras haya sesión de cliente y la app
    // esté en foreground, el canal de pedidos del usuario vive una vez para toda la app
    // (lo lee Tracking y, más adelante, las notificaciones). Se corta en background y
    // re-fetchea al volver. El BackOffice tiene su propio canal scopeado a sus pantallas.
    val authStateForRealtime by authViewModel.uiState.collectAsStateWithLifecycle()
    val isClientSession = authStateForRealtime.currentUserEmail.isNotEmpty() &&
        authStateForRealtime.currentUserRole != "BACK_OFFICE"
    LaunchedEffect(isClientSession) {
        if (!isClientSession) return@LaunchedEffect
        ProcessLifecycleOwner.get().lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            orderViewModel.observeUserOrders()
        }
    }

    // Centro de notificaciones: arranca la observación de avisos del usuario (lista +
    // badge) cuando hay sesión de cliente, y pide el permiso POST_NOTIFICATIONS (13+).
    val notificationsPermission = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS
    )
    LaunchedEffect(isClientSession) {
        if (!isClientSession) return@LaunchedEffect
        notificationViewModel.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !notificationsPermission.status.isGranted
        ) {
            notificationsPermission.launchPermissionRequest()
        }
    }
    val notificationsState by notificationViewModel.uiState.collectAsStateWithLifecycle()

    // Deep-link desde el tap en una notificación → seguimiento del pedido.
    LaunchedEffect(openTrackingSignal) {
        if (openTrackingSignal) {
            navController.navigate(Screen.Tracking.route)
            onTrackingSignalConsumed()
        }
    }

    if (pendingLocalId != null) {
        AlertDialog(
            onDismissRequest = { pendingLocalId = null; pendingPopUpRoute = null },
            title = { Text("Ya tenés un pedido activo") },
            text = { Text("Tenés ítems en el carrito de otro local. ¿Querés limpiar ese pedido y empezar uno nuevo acá?") },
            confirmButton = {
                Button(onClick = {
                    cartViewModel.clearCart()
                    val localId = pendingLocalId!!
                    val popUp = pendingPopUpRoute
                    pendingLocalId = null
                    pendingPopUpRoute = null
                    if (popUp != null) {
                        navController.navigate(Screen.Menu.route(localId)) {
                            popUpTo(popUp) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Menu.route(localId))
                    }
                }) { Text("Nuevo pedido") }
            },
            dismissButton = {
                OutlinedButton(onClick = { pendingLocalId = null; pendingPopUpRoute = null }) {
                    Text("Mantener pedido")
                }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            val authState by authViewModel.uiState.collectAsStateWithLifecycle()
            var splashAnimDone by remember { mutableStateOf(false) }

            LaunchedEffect(splashAnimDone, authState.sessionCheckDone) {
                if (!splashAnimDone || !authState.sessionCheckDone) return@LaunchedEffect
                val destination = when {
                    authState.sessionRestored && authState.currentUserRole == "BACK_OFFICE" -> Screen.BackOffice.route
                    authState.sessionRestored -> Screen.Home.route
                    onboardingCompleted -> Screen.Login.route
                    else -> Screen.Onboarding.route
                }
                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }

            SplashScreen(onSplashComplete = { splashAnimDone = true })
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    themeViewModel.setOnboardingCompleted()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToBackOffice = {
                    themeViewModel.setOnboardingCompleted()
                    navController.navigate(Screen.BackOffice.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToGoogleLogin = {
                    navController.navigate(Screen.GoogleLogin.route)
                }
            )
        }

        composable(Screen.BackOffice.route) {
            BackOfficeScreen(
                orderViewModel = orderViewModel,
                onNavigateToHistory = {
                    navController.navigate(Screen.BackOfficeHistory.route)
                },
                onUpdateOrder = { orderId ->
                    navController.navigate(Screen.BackOfficeUpdateOrder.route(orderId))
                },
                onNavigateToAjustes = {
                    navController.navigate(Screen.BackOfficeAjustes.route)
                }
            )
        }

        composable(
            route = Screen.BackOfficeUpdateOrder.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            BackOfficeUpdateOrderScreen(
                orderId = orderId,
                orderViewModel = orderViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToOrders = {
                    navController.navigate(Screen.BackOffice.route) {
                        popUpTo(Screen.BackOffice.route) { inclusive = true }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.BackOfficeHistory.route) {
                        popUpTo(Screen.BackOffice.route) { inclusive = false }
                    }
                },
                onNavigateToAjustes = {
                    navController.navigate(Screen.BackOfficeAjustes.route)
                }
            )
        }

        composable(Screen.BackOfficeHistory.route) {
            BackOfficeHistoryScreen(
                orderViewModel = orderViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToOrders = {
                    navController.navigate(Screen.BackOffice.route) {
                        popUpTo(Screen.BackOffice.route) { inclusive = true }
                    }
                },
                onNavigateToAjustes = {
                    navController.navigate(Screen.BackOfficeAjustes.route)
                }
            )
        }

        composable(Screen.BackOfficeAjustes.route) {
            val authState by authViewModel.uiState.collectAsStateWithLifecycle()
            BackOfficeAjustesScreen(
                userName = authState.currentUserName,
                userEmail = authState.currentUserEmail,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onNavigateToOrders = {
                    navController.navigate(Screen.BackOffice.route) {
                        popUpTo(Screen.BackOffice.route) { inclusive = true }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.BackOfficeHistory.route) {
                        popUpTo(Screen.BackOffice.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.GoogleLogin.route) {
            GoogleLoginScreen(
                onBack = { navController.popBackStack() },
                onContinueWithGoogle = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onUseEmail = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.GoogleLogin.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onNavigateToGoogleLogin = {
                    navController.navigate(Screen.GoogleLogin.route)
                }
            )
        }

        composable(Screen.Home.route) {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val authState by authViewModel.uiState.collectAsStateWithLifecycle()
            val orderState by orderViewModel.uiState.collectAsStateWithLifecycle()
            val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(authState.currentUserFavoritos) {
                homeViewModel.loadFavoritos(authState.currentUserFavoritos)
            }
            LaunchedEffect(Unit) {
                orderViewModel.loadUserOrders()
            }

            // Si el local más cercano está a ≤50 m, mostrar "¿Estás en X?" (una vez por sesión).
            // Con un pedido activo en curso no interrumpimos: el foco es el seguimiento.
            LaunchedEffect(homeState.closestLocal) {
                val nearby = homeState.closestLocal?.takeIf { local ->
                    local.distanciaMetros?.let { it <= NEARBY_THRESHOLD_METERS } == true
                }
                if (nearby != null && !locationPromptShown && orderState.activeOrder() == null) {
                    locationPromptShown = true
                    detectedLocal = nearby
                    navController.navigate(Screen.LocationDetected.route)
                }
            }

            val activeOrder = orderState.activeOrder()

            HomeScreen(
                homeViewModel = homeViewModel,
                userName = authState.currentUserName,
                activeOrder = activeOrder,
                activeCart = activeCart,
                onViewCart = onViewCart,
                isDarkTheme = isDarkTheme,
                unreadNotifications = notificationsState.unreadCount,
                onNavigateToNotifications = { navController.navigate(Screen.NotificationCenter.route) },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onLocalSelected = { localId -> navigateToMenu(localId) },
                onNavigateToTracking = { navController.navigate(Screen.Tracking.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToAjustes = { navController.navigate(Screen.Ajustes.route) }
            )
        }

        composable(Screen.MisLocales.route) {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            MisLocalesScreen(
                misLocalesViewModel = misLocalesViewModel,
                isDarkTheme = isDarkTheme,
                activeCart = activeCart,
                onViewCart = onViewCart,
                onLocalSelected = { localId -> navigateToMenu(localId) },
                onBack = { navController.popBackStack() },
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToLocationDetected = {
                    val localId = misLocalesViewModel.uiState.value.locales
                        .firstOrNull { it.nombre.contains("Big Pons", ignoreCase = true) }?.id
                        ?: misLocalesViewModel.uiState.value.locales.firstOrNull()?.id ?: ""
                    navigateToMenu(localId)
                },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) },
                onNavigateToAjustes = { navController.navigate(Screen.Ajustes.route) }
            )
        }

        composable(Screen.LocationDetected.route) {
            val local = detectedLocal
            if (local == null) {
                // Sin local detectado (entrada directa): volver a Home.
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LocationDetected.route) { inclusive = true }
                    }
                }
            } else {
                LocationDetectedScreen(
                    local = local,
                    distanceMeters = local.distanciaMetros,
                    onConfirmLocation = {
                        navigateToMenu(local.id, Screen.LocationDetected.route)
                    },
                    onRejectLocation = {
                        navController.popBackStack()
                    },
                    onSearchAnother = {
                        navController.navigate(Screen.MisLocales.route) {
                            popUpTo(Screen.LocationDetected.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Screen.ScanQr.route) {
            // Scopeado al entry: cada visita a "Escanear QR" arranca con un ViewModel limpio.
            val qrScanViewModel: QrScanViewModel = viewModel()
            LaunchedEffect(Unit) {
                qrScanViewModel.events.collect { event ->
                    when (event) {
                        is QrScanEvent.Resolved -> navigateToMenu(event.localId, Screen.ScanQr.route)
                        QrScanEvent.NotRecognized -> navController.navigate(Screen.QrNoReconocido.route)
                    }
                }
            }
            ScanearQrScreen(
                onBack = { navController.popBackStack() },
                onQrDetected = qrScanViewModel::onQrScanned,
            )
        }

        composable(Screen.QrNoReconocido.route) {
            QrNoReconocidoScreen(
                onRetry = {
                    navController.navigate(Screen.ScanQr.route) {
                        popUpTo(Screen.QrNoReconocido.route) { inclusive = true }
                    }
                },
                onManualInput = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Ajustes.route) {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val authState by authViewModel.uiState.collectAsStateWithLifecycle()
            AjustesScreen(
                userName = authState.currentUserName,
                userEmail = authState.currentUserEmail,
                isDarkTheme = isDarkTheme,
                onDarkModeToggle = themeViewModel::setDarkMode,
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) },
                onNavigateToNotificaciones = { navController.navigate(Screen.Notificaciones.route) },
                onNavigateToMetodosDePago = { navController.navigate(Screen.MetodosDePago.route) },
                onNavigateToEliminarCuenta = { navController.navigate(Screen.EliminarCuenta.route) },
                onLogout = {
                    navController.navigate(Screen.CerrarSesion.route)
                }
            )
        }

        composable(Screen.MetodosDePago.route) {
            MetodosDePagoScreen(
                paymentViewModel = paymentViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToAgregarMetodo = { navController.navigate(Screen.AgregarMetodoDePago.route) },
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) },
                onNavigateToAjustes = {
                    navController.navigate(Screen.Ajustes.route) {
                        popUpTo(Screen.Ajustes.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AgregarMetodoDePago.route) {
            AgregarMetodoDePagoScreen(
                paymentViewModel = paymentViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) },
                onNavigateToAjustes = {
                    navController.navigate(Screen.Ajustes.route) {
                        popUpTo(Screen.Ajustes.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Notificaciones.route) {
            NotificacionesScreen(
                onBack = { navController.popBackStack() },
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) },
                onNavigateToAjustes = {
                    navController.navigate(Screen.Ajustes.route) {
                        popUpTo(Screen.Ajustes.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.NotificationCenter.route) {
            NotificationCenterScreen(
                notifications = notificationsState.items,
                onBack = { navController.popBackStack() },
                onNotificationClick = {
                    navController.navigate(Screen.Tracking.route)
                },
                onMarkAllRead = { notificationViewModel.markAllRead() },
                onClearAll = { notificationViewModel.clearAll() },
            )
        }

        composable(Screen.CerrarSesion.route) {
            CerrarSesionScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onConfirmLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) }
            )
        }

        composable(Screen.EliminarCuenta.route) {
            EliminarCuentaScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onConfirmDelete = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) },
                onNavigateToAjustes = {
                    navController.navigate(Screen.Ajustes.route) {
                        popUpTo(Screen.Ajustes.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Menu.route,
            arguments = listOf(navArgument("localId") { type = NavType.StringType })
        ) { backStackEntry ->
            val localId = backStackEntry.arguments?.getString("localId") ?: ""
            val misLocalesState by misLocalesViewModel.uiState.collectAsStateWithLifecycle()
            val local = misLocalesState.locales.firstOrNull { it.id == localId }
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val authState by authViewModel.uiState.collectAsStateWithLifecycle()
            val isFavorito = localId in authState.currentUserFavoritos
            val orderState by orderViewModel.uiState.collectAsStateWithLifecycle()
            // Bloqueo de carrito nuevo mientras haya un pedido en curso.
            val blockNewCart = orderState.activeOrder() != null
            val menuViewModel: MenuViewModel = viewModel()
            LaunchedEffect(Unit) {
                menuViewModel.loadMenu(localId)
                orderViewModel.loadUserOrders()
            }
            MenuScreen(
                cartViewModel = cartViewModel,
                menuViewModel = menuViewModel,
                local = local,
                isDarkTheme = isDarkTheme,
                isFavorito = isFavorito,
                blockNewCart = blockNewCart,
                onToggleFavorito = { authViewModel.toggleFavorito(localId) },
                onViewCart = { navController.navigate(Screen.Cart.route) },
                onViewActiveOrder = { navController.navigate(Screen.Tracking.route) },
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(Screen.Cart.route) {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            CartScreen(
                cartViewModel = cartViewModel,
                isDarkTheme = isDarkTheme,
                onConfirm = { navController.navigate(Screen.Payment.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Payment.route) {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                orderViewModel.navEvent.collect { event ->
                    when (event) {
                        is OrderNavEvent.CheckoutSuccess -> {
                            cartViewModel.clearCart()
                            navController.navigate(Screen.OrderConfirmed.route) {
                                popUpTo(Screen.Menu.route) { inclusive = false }
                            }
                        }
                        is OrderNavEvent.CheckoutError -> Unit
                    }
                }
            }

            PaymentScreen(
                cartViewModel = cartViewModel,
                isDarkTheme = isDarkTheme,
                onPaymentSuccess = {
                    orderViewModel.placeOrder(
                        items = cartViewModel.uiState.value.items,
                        localId = cartViewModel.cartLocalId,
                    )
                },
                onNavigateToAgregarMetodo = { navController.navigate(Screen.AgregarMetodoDePago.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.OrderConfirmed.route) {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val orderState by orderViewModel.uiState.collectAsStateWithLifecycle()
            val lastOrder = orderState.lastCreatedOrder
            OrderConfirmedScreen(
                orderCode = lastOrder?.numero?.toString() ?: "----",
                localNombre = lastOrder?.localNombre ?: "",
                isDarkTheme = isDarkTheme,
                onViewTracking = {
                    navController.navigate(Screen.Tracking.route) {
                        popUpTo(Screen.OrderConfirmed.route) { inclusive = true }
                    }
                },
                onGoHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Tracking.route) {
            val orderState by orderViewModel.uiState.collectAsStateWithLifecycle()
            val lastOrder = orderState.lastCreatedOrder

            // Pedido activo: preferimos el de userOrders (actualizado por polling),
            // fallback a lastCreatedOrder (disponible incluso antes del primer poll)
            val activeOrder = orderState.activeOrder() ?: lastOrder

            // El canal Realtime del cliente ya vive a nivel app (ver AppNavigation),
            // así que userOrders se mantiene al día solo. Disparamos una carga inmediata
            // por si la pantalla se abre antes del primer evento del canal.
            LaunchedEffect(Unit) { orderViewModel.loadUserOrders() }

            // Navegación automática cuando el status pasa a "ready"
            LaunchedEffect(activeOrder?.status) {
                if (activeOrder?.status == "ready") {
                    navController.navigate(Screen.OrderReady.route) {
                        popUpTo(Screen.Tracking.route) { inclusive = true }
                    }
                }
            }

            TrackingScreen(
                orderCode = activeOrder?.numero?.toString() ?: "----",
                localNombre = activeOrder?.localNombre ?: "",
                status = activeOrder?.status ?: "preparing",
                onGoHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) },
                onNavigateToAjustes = { navController.navigate(Screen.Ajustes.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) }
            )
        }

        @Suppress("DEPRECATION")
        composable(Screen.OrderReady.route) {
            val orderState by orderViewModel.uiState.collectAsStateWithLifecycle()
            val activeOrder = orderState.userOrders.firstOrNull { it.status == "ready" }
                ?: orderState.lastCreatedOrder?.takeIf { it.status == "ready" }
            OrderReadyScreen(
                orderCode = activeOrder?.numero?.toString() ?: "----",
                localNombre = activeOrder?.localNombre ?: "",
                onConfirmPickup = {
                    val orderId = activeOrder?.id
                    if (orderId != null) orderViewModel.confirmPickup(orderId)
                    navController.navigate(Screen.PickupSuccess.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.PickupSuccess.route) {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val authState by authViewModel.uiState.collectAsStateWithLifecycle()
            val orderState by orderViewModel.uiState.collectAsStateWithLifecycle()
            // El pedido recién retirado: preferimos lastCreatedOrder; fallback al picked_up más reciente.
            val pickedUpOrder = orderState.lastCreatedOrder?.takeIf { it.status == "picked_up" }
                ?: orderState.userOrders.firstOrNull { it.status == "picked_up" }
            PickupSuccessScreen(
                userName = authState.currentUserName,
                order = pickedUpOrder,
                isDarkTheme = isDarkTheme,
                onGoHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onViewSummary = {
                    navController.navigate(Screen.OrderSummary.route)
                }
            )
        }

        composable(Screen.MisPedidos.route) {
            MisPedidosScreen(
                orderViewModel = orderViewModel,
                activeCart = activeCart,
                onViewCart = onViewCart,
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToAjustes = { navController.navigate(Screen.Ajustes.route) },
                onViewActiveOrder = {
                    val order = orderViewModel.uiState.value.activeOrder()
                    if (order?.status == "ready") {
                        navController.navigate(Screen.OrderReady.route)
                    } else {
                        navController.navigate(Screen.Tracking.route)
                    }
                },
                onViewOrderSummary = { navController.navigate(Screen.OrderSummary.route) }
            )
        }

        composable(Screen.OrderSummary.route) {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val orderState by orderViewModel.uiState.collectAsStateWithLifecycle()
            OrderSummaryScreen(
                order = orderState.selectedOrder,
                isDarkTheme = isDarkTheme,
                onBack = {
                    if (!navController.popBackStack(Screen.MisPedidos.route, inclusive = false)) {
                        navController.navigate(Screen.MisPedidos.route)
                    }
                }
            )
        }
    }
}
