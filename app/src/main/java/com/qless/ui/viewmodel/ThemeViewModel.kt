package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel : ViewModel() {

    private val observeDarkModeUseCase = AppModule.observeDarkMode
    private val observeOnboardingCompletedUseCase = AppModule.observeOnboardingCompleted
    private val setDarkModeUseCase = AppModule.setDarkMode
    private val setOnboardingCompletedUseCase = AppModule.setOnboardingCompleted

    val isDarkTheme: StateFlow<Boolean> = observeDarkModeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val isOnboardingCompleted: StateFlow<Boolean> = observeOnboardingCompletedUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { setDarkModeUseCase(enabled) }
    }

    fun setOnboardingCompleted() {
        viewModelScope.launch { setOnboardingCompletedUseCase() }
    }
}
