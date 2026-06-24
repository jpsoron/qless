package com.qless.viewmodel

import com.qless.domain.model.CachedResult
import com.qless.domain.usecase.GetLocalByIdUseCase
import com.qless.fakes.FakeLocalesRepository
import com.qless.fakes.sampleLocal
import com.qless.ui.viewmodel.QrScanEvent
import com.qless.ui.viewmodel.QrScanViewModel
import com.qless.util.MainDispatcherRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QrScanViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val validId = "11111111-1111-1111-1111-111111111111"

    /** Suscribe un colector ansioso antes de disparar la acción y devuelve el primer evento emitido. */
    private fun firstEvent(vm: QrScanViewModel, action: () -> Unit): QrScanEvent {
        val received = mutableListOf<QrScanEvent>()
        val job = CoroutineScope(UnconfinedTestDispatcher()).launch {
            vm.events.collect { received += it }
        }
        action()
        job.cancel()
        return received.first()
    }

    @Test
    fun `qr con id de un local existente resuelve a ese local`() {
        val repo = FakeLocalesRepository(
            localByIdResult = Result.success(CachedResult(sampleLocal(id = validId), false))
        )
        val vm = QrScanViewModel(GetLocalByIdUseCase(repo))

        val event = firstEvent(vm) { vm.onQrScanned(validId) }

        assertEquals(QrScanEvent.Resolved(validId), event)
        assertEquals(validId, repo.lastLocalByIdQuery)
    }

    @Test
    fun `qr con uuid valido pero sin local registrado no se reconoce`() {
        val repo = FakeLocalesRepository(
            localByIdResult = Result.success(CachedResult(null, false))
        )
        val vm = QrScanViewModel(GetLocalByIdUseCase(repo))

        val event = firstEvent(vm) { vm.onQrScanned(validId) }

        assertEquals(QrScanEvent.NotRecognized, event)
    }

    @Test
    fun `qr que no es un uuid se descarta sin tocar la red`() {
        val repo = FakeLocalesRepository()
        val vm = QrScanViewModel(GetLocalByIdUseCase(repo))

        val event = firstEvent(vm) { vm.onQrScanned("https://no-soy-un-id.com") }

        assertEquals(QrScanEvent.NotRecognized, event)
        assertNull(repo.lastLocalByIdQuery)
    }

    @Test
    fun `id valido con espacios alrededor se normaliza antes de validar`() {
        val repo = FakeLocalesRepository(
            localByIdResult = Result.success(CachedResult(sampleLocal(id = validId), false))
        )
        val vm = QrScanViewModel(GetLocalByIdUseCase(repo))

        val event = firstEvent(vm) { vm.onQrScanned("  $validId\n") }

        assertEquals(QrScanEvent.Resolved(validId), event)
        assertEquals(validId, repo.lastLocalByIdQuery)
    }

    @Test
    fun `fallo de red sin cache se reporta como no reconocido`() {
        val repo = FakeLocalesRepository(
            localByIdResult = Result.failure(RuntimeException("sin red"))
        )
        val vm = QrScanViewModel(GetLocalByIdUseCase(repo))

        val event = firstEvent(vm) { vm.onQrScanned(validId) }

        assertEquals(QrScanEvent.NotRecognized, event)
    }
}
