package com.qless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qless.di.AppModule
import com.qless.navigation.AppNavigation
import com.qless.ui.theme.QLessTheme
import com.qless.ui.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa el composition root antes de instanciar cualquier ViewModel.
        AppModule.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            QLessTheme(darkTheme = isDarkTheme) {
                AppNavigation(themeViewModel = themeViewModel)
            }
        }
    }
}