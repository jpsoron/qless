package com.qless.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qless.di.AppModule
import com.qless.domain.usecase.GetLocalByIdUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

/** Resultado one-shot de resolver un QR escaneado. Lo consume [AppNavigation] para navegar. */
sealed interface QrScanEvent {
    /** El QR corresponde a un local registrado: navegar a su menú. */
    data class Resolved(val localId: String) : QrScanEvent
    /** El QR no es un id válido o no corresponde a ningún local de QLess. */
    data object NotRecognized : QrScanEvent
}

/**
 * Valida el contenido crudo de un QR (que debe ser el id del local) y resuelve a
 * dónde navegar. La pantalla de cámara solo reporta el valor leído; la regla de
 * "¿este local existe?" vive acá (dominio + caso de uso), no en la navegación.
 */
class QrScanViewModel(
    private val getLocalById: GetLocalByIdUseCase,
) : ViewModel() {

    /** Constructor sin args para `viewModel()` en producción: toma el grafo de [AppModule]. */
    constructor() : this(AppModule.getLocalById)

    private val _events = MutableSharedFlow<QrScanEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<QrScanEvent> = _events.asSharedFlow()

    // ML Kit dispara el callback por cada frame; nos quedamos solo con la primera lectura válida.
    private var resolving = false

    fun onQrScanned(rawValue: String) {
        if (resolving) return
        resolving = true

        val id = rawValue.trim()
        if (!isValidLocalId(id)) {
            emit(QrScanEvent.NotRecognized)
            return
        }

        viewModelScope.launch {
            getLocalById(id)
                .onSuccess { result ->
                    emit(
                        if (result.data != null) QrScanEvent.Resolved(result.data.id)
                        else QrScanEvent.NotRecognized
                    )
                }
                .onFailure { emit(QrScanEvent.NotRecognized) }
        }
    }

    private fun emit(event: QrScanEvent) {
        _events.tryEmit(event)
    }

    /** Los ids de local son UUID (PK en Postgres); descartamos cualquier otro contenido sin tocar la red. */
    private fun isValidLocalId(value: String): Boolean =
        runCatching { UUID.fromString(value) }.isSuccess
}
