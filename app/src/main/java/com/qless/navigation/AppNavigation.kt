package com.qless.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qless.ui.screens.*
import com.qless.ui.screens.BackOfficeHistoryScreen
import com.qless.ui.screens.BackOfficeScreen
import com.qless.ui.screens.BackOfficeUpdateOrderScreen
import com.qless.ui.screens.CartScreen
import com.qless.ui.screens.GoogleLoginScreen
import com.qless.ui.screens.HomeScreen
import com.qless.ui.screens.LoginScreen
import com.qless.ui.screens.MenuScreen
import com.qless.ui.screens.MisLocalesScreen
import com.qless.ui.screens.OnboardingScreen
import com.qless.ui.screens.OrderConfirmedScreen
import com.qless.ui.screens.OrderReadyScreen
import com.qless.ui.screens.PaymentScreen
import com.qless.ui.screens.QrNoReconocidoScreen
import com.qless.ui.screens.PickupSuccessScreen
import com.qless.ui.screens.RegisterScreen
import com.qless.ui.screens.ScanearQrScreen
import com.qless.ui.screens.SplashScreen
import com.qless.ui.screens.TrackingScreen


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object GoogleLogin : Screen("google_login")
    object Register : Screen("register")
    object Home : Screen("home")
    object MisLocales : Screen("mis_locales")
    object Menu : Screen("menu")
    object Cart : Screen("cart")
    object Payment : Screen("payment")
    object OrderConfirmed : Screen("order_confirmed")
    object Tracking : Screen("tracking")
    object ScanQr : Screen("scan_qr")
    object QrNoReconocido : Screen("qr_no_reconocido")
    object OrderReady : Screen("order_ready")
    object PickupSuccess : Screen("pickup_success")
    object Ajustes : Screen("ajustes")
    object CerrarSesion : Screen("cerrar_sesion")
    object BackOffice : Screen("back_office")
    object BackOfficeHistory : Screen("back_office_history")
    object BackOfficeUpdateOrder : Screen("back_office_update_order")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
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
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
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
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
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
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToTracking = { navController.navigate(Screen.Tracking.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToAjustes = { navController.navigate(Screen.Ajustes.route) }
            )
        }

        composable(Screen.MisLocales.route) {
            MisLocalesScreen(
                onLocalSelected = { navController.navigate(Screen.Menu.route) },
                onBack = { navController.popBackStack() },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToAjustes = { navController.navigate(Screen.Ajustes.route) }
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
                onNavigateToInicio = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToScanQr = { navController.navigate(Screen.ScanQr.route) },
                onNavigateToMisPedidos = { navController.navigate(Screen.Tracking.route) },
                onLogout = {
                    navController.navigate(Screen.CerrarSesion.route)
                }
            )
        }

        composable(Screen.CerrarSesion.route) {
            CerrarSesionScreen(
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
                onNavigateToMisPedidos = { navController.navigate(Screen.Tracking.route) }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                onViewCart = { navController.navigate(Screen.Cart.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onConfirm = { navController.navigate(Screen.Payment.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Payment.route) {
            PaymentScreen(
                onPaymentSuccess = {
                    navController.navigate(Screen.OrderConfirmed.route) {
                        popUpTo(Screen.Menu.route) { inclusive = false }
                    }
                },
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
                    navController.navigate(Screen.Home.route)
                }
            )
        }
    }
}
