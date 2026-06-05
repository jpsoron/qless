package com.qless.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qless.ui.viewmodel.AuthViewModel
import com.qless.ui.viewmodel.CartViewModel
import com.qless.ui.viewmodel.MenuViewModel
import com.qless.ui.viewmodel.MisLocalesViewModel
import com.qless.ui.viewmodel.PaymentMethodViewModel
import com.qless.ui.screens.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object GoogleLogin : Screen("google_login")
    object Register : Screen("register")
    object Home : Screen("home")
    object MisLocales : Screen("mis_locales")
    object LocationDetected : Screen("location_detected")
    object Menu : Screen("menu")
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
    object EliminarCuenta : Screen("eliminar_cuenta")
    object BackOffice : Screen("back_office")
    object BackOfficeHistory : Screen("back_office_history")
    object BackOfficeUpdateOrder : Screen("back_office_update_order")
    object BackOfficeAjustes : Screen("back_office_ajustes")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val paymentViewModel: PaymentMethodViewModel = viewModel()
    val menuViewModel: MenuViewModel = viewModel()
    val misLocalesViewModel: MisLocalesViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
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
                    navController.navigate(Screen.LocationDetected.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToBackOffice = {
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
                onNavigateToHistory = {
                    navController.navigate(Screen.BackOfficeHistory.route)
                },
                onUpdateOrder = {
                    navController.navigate(Screen.BackOfficeUpdateOrder.route)
                },
                onNavigateToAjustes = {
                    navController.navigate(Screen.BackOfficeAjustes.route)
                }
            )
        }

        composable(Screen.BackOfficeUpdateOrder.route) {
            BackOfficeUpdateOrderScreen(
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
            BackOfficeAjustesScreen(
                userName = authViewModel.uiState.value.currentUserName,
                userEmail = authViewModel.uiState.value.currentUserEmail,
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
                    navController.navigate(Screen.LocationDetected.route) {
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
            HomeScreen(
                userName = authViewModel.uiState.value.currentUserName,
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToTracking = { navController.navigate(Screen.Tracking.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToAjustes = { navController.navigate(Screen.Ajustes.route) }
            )
        }

        composable(Screen.MisLocales.route) {
            MisLocalesScreen(
                misLocalesViewModel = misLocalesViewModel,
                onLocalSelected = { navController.navigate(Screen.Menu.route) },
                onBack = { navController.popBackStack() },
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToLocationDetected = { navController.navigate(Screen.LocationDetected.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.MisPedidos.route) },
                onNavigateToAjustes = { navController.navigate(Screen.Ajustes.route) }
            )
        }

        composable(Screen.LocationDetected.route) {
            LocationDetectedScreen(
                onConfirmLocation = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.LocationDetected.route) { inclusive = true }
                    }
                },
                onRejectLocation = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSearchAnother = {
                    navController.navigate(Screen.MisLocales.route) {
                        popUpTo(Screen.LocationDetected.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ScanQr.route) {
            ScanearQrScreen(
                onBack = { navController.popBackStack() },
                onQrDetected = { qrData ->
                    if (qrData == "error") {
                        navController.navigate(Screen.QrNoReconocido.route)
                    } else {
                        navController.navigate(Screen.Menu.route) {
                            popUpTo(Screen.ScanQr.route) { inclusive = true }
                        }
                    }
                }
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
            AjustesScreen(
                userName = authViewModel.uiState.value.currentUserName,
                userEmail = authViewModel.uiState.value.currentUserEmail,
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

        composable(Screen.Menu.route) {
            MenuScreen(
                cartViewModel = cartViewModel,
                menuViewModel = menuViewModel,
                onViewCart = { navController.navigate(Screen.Cart.route) },
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
            CartScreen(
                cartViewModel = cartViewModel,
                onConfirm = { navController.navigate(Screen.Payment.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Payment.route) {
            PaymentScreen(
                cartViewModel = cartViewModel,
                onPaymentSuccess = {
                    navController.navigate(Screen.OrderConfirmed.route) {
                        popUpTo(Screen.Menu.route) { inclusive = false }
                    }
                },
                onNavigateToAgregarMetodo = { navController.navigate(Screen.AgregarMetodoDePago.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.OrderConfirmed.route) {
            OrderConfirmedScreen(
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
            TrackingScreen(
                onGoHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToOrderReady = {
                    navController.navigate(Screen.OrderReady.route)
                }
            )
        }

        @Suppress("DEPRECATION")
        composable(Screen.OrderReady.route) {
            OrderReadyScreen(
                onConfirmPickup = {
                    navController.navigate(Screen.PickupSuccess.route) {
                        popUpTo(Screen.Tracking.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PickupSuccess.route) {
            PickupSuccessScreen(
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
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToAjustes = { navController.navigate(Screen.Ajustes.route) },
                onViewActiveOrder = { navController.navigate(Screen.OrderReady.route) },
                onViewOrderSummary = { navController.navigate(Screen.OrderSummary.route) }
            )
        }

        composable(Screen.OrderSummary.route) {
            OrderSummaryScreen(
                onBack = {
                    if (!navController.popBackStack(Screen.MisPedidos.route, inclusive = false)) {
                        navController.navigate(Screen.MisPedidos.route)
                    }
                }
            )
        }
    }
}
