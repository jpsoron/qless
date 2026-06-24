package com.qless

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qless.data.remote.SupabaseClient
import com.qless.di.AppModule
import com.qless.navigation.AppNavigation
import com.qless.ui.theme.QLessTheme
import com.qless.ui.viewmodel.ThemeViewModel
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    // Señal de deep-link desde el tap en una notificación: pedir ir al seguimiento.
    private val openTracking = mutableStateOf(false)

    // Señal de deep-link de recuperación de contraseña (qless://reset-password):
    // Supabase ya abrió la sesión de recuperación, pedir ir a "Nueva contraseña".
    private val openResetPassword = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa el composition root antes de instanciar cualquier ViewModel.
        AppModule.init(applicationContext)
        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            QLessTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    themeViewModel = themeViewModel,
                    openTrackingSignal = openTracking.value,
                    onTrackingSignalConsumed = { openTracking.value = false },
                    openResetPasswordSignal = openResetPassword.value,
                    onResetPasswordSignalConsumed = { openResetPassword.value = false },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_NAVIGATE_TRACKING, false) == true) {
            openTracking.value = true
        }
        // Deja que supabase-kt procese el link de recuperación. Si es válido, abre
        // la sesión de recuperación y recién ahí pedimos navegar a "Nueva contraseña".
        intent?.let {
            SupabaseClient.instance.handleDeeplinks(it) {
                openResetPassword.value = true
            }
        }
    }

    companion object {
        const val EXTRA_NAVIGATE_TRACKING = "com.qless.NAVIGATE_TRACKING"
    }
}
