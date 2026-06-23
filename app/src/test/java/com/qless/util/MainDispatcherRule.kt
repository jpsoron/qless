package com.qless.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Reemplaza `Dispatchers.Main` por un dispatcher de test durante cada test,
 * para que `viewModelScope` ejecute en el hilo de test.
 *
 * Usa [UnconfinedTestDispatcher] por defecto: las corrutinas lanzadas en
 * `viewModelScope.launch { ... }` arrancan de forma ansiosa, así el estado
 * queda actualizado apenas vuelve la llamada (sin necesidad de `advanceUntilIdle`).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: kotlinx.coroutines.CoroutineDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
