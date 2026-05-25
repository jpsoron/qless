package com.qless.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qless.ui.screens.CartScreen
import com.qless.ui.screens.HomeScreen
import com.qless.ui.screens.LoginScreen
import com.qless.ui.screens.MenuScreen
import com.qless.ui.screens.MisLocalesScreen
import com.qless.ui.screens.OnboardingScreen
import com.qless.ui.screens.OrderConfirmedScreen
import com.qless.ui.screens.OrderReadyScreen
import com.qless.ui.screens.PaymentScreen
import com.qless.ui.screens.PickupSuccessScreen
import com.qless.ui.screens.RegisterScreen
import com.qless.ui.screens.SplashScreen
import com.qless.ui.screens.TrackingScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object MisLocales : Screen("mis_locales")
    object Menu : Screen("menu")
    object Cart : Screen("cart")
    object Payment : Screen("payment")
    object OrderConfirmed : Screen("order_confirmed")
    object Tracking : Screen("tracking")
    object OrderReady : Screen("order_ready")
    object PickupSuccess : Screen("pickup_success")
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
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
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
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToMisLocales = { navController.navigate(Screen.MisLocales.route) },
                onNavigateToTracking = { navController.navigate(Screen.Tracking.route) }
            )
        }

        composable(Screen.MisLocales.route) {
            MisLocalesScreen(
                onLocalSelected = { navController.navigate(Screen.Menu.route) },
                onBack = { navController.popBackStack() }
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
                    // Acción opcional, por ahora vuelve al inicio
                    navController.navigate(Screen.Home.route)
                }
            )
        }
    }
}