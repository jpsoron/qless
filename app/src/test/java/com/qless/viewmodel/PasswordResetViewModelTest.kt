package com.qless.viewmodel

import com.qless.domain.usecase.SendPasswordResetUseCase
import com.qless.domain.usecase.UpdatePasswordUseCase
import com.qless.fakes.FakeUserRepository
import com.qless.ui.viewmodel.PasswordResetEvent
import com.qless.ui.viewmodel.PasswordResetViewModel
import com.qless.util.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PasswordResetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildVm(repo: FakeUserRepository) = PasswordResetViewModel(
        SendPasswordResetUseCase(repo),
        UpdatePasswordUseCase(repo),
    )

    @Test
    fun `email invalido no llama a la red y muestra error`() {
        val repo = FakeUserRepository()
        val vm = buildVm(repo)

        vm.sendResetEmail("no-es-un-mail")

        assertEquals("Ingresá un correo válido", vm.uiState.value.error)
        assertFalse(vm.uiState.value.emailSent)
        assertNull(repo.lastResetEmail)
    }

    @Test
    fun `envio exitoso marca emailSent y normaliza el correo`() {
        val repo = FakeUserRepository()
        val vm = buildVm(repo)

        vm.sendResetEmail("  user@mail.com ")

        assertTrue(vm.uiState.value.emailSent)
        assertNull(vm.uiState.value.error)
        assertEquals("user@mail.com", repo.lastResetEmail)
    }

    @Test
    fun `fallo de envio muestra error mapeado`() {
        val repo = FakeUserRepository(
            sendPasswordResetResult = Result.failure(RuntimeException("network error"))
        )
        val vm = buildVm(repo)

        vm.sendResetEmail("user@mail.com")

        assertFalse(vm.uiState.value.emailSent)
        assertEquals("Sin conexión a internet", vm.uiState.value.error)
    }

    @Test
    fun `contrasena corta no llama a la red`() {
        val repo = FakeUserRepository()
        val vm = buildVm(repo)

        vm.changePassword("123", "123")

        assertEquals("La contraseña debe tener al menos 8 caracteres", vm.uiState.value.error)
        assertNull(repo.lastNewPassword)
    }

    @Test
    fun `contrasenas que no coinciden no llaman a la red`() {
        val repo = FakeUserRepository()
        val vm = buildVm(repo)

        vm.changePassword("12345678", "87654321")

        assertEquals("Las contraseñas no coinciden", vm.uiState.value.error)
        assertNull(repo.lastNewPassword)
    }

    @Test
    fun `cambio exitoso emite PasswordChanged`() {
        val repo = FakeUserRepository()
        val vm = buildVm(repo)
        val received = mutableListOf<PasswordResetEvent>()
        val job = CoroutineScope(UnconfinedTestDispatcher()).launch {
            vm.events.collect { received += it }
        }

        vm.changePassword("nuevaClave1", "nuevaClave1")
        job.cancel()

        assertEquals("nuevaClave1", repo.lastNewPassword)
        assertEquals(PasswordResetEvent.PasswordChanged, received.firstOrNull())
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `enlace expirado al cambiar muestra error claro`() {
        val repo = FakeUserRepository(
            updatePasswordResult = Result.failure(RuntimeException("Auth session is invalid"))
        )
        val vm = buildVm(repo)

        vm.changePassword("nuevaClave1", "nuevaClave1")

        assertEquals("El enlace expiró o no es válido. Pedí uno nuevo.", vm.uiState.value.error)
    }
}
