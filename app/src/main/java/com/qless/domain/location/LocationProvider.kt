package com.qless.domain.location

import com.qless.domain.model.Coordinates

/**
 * Abstracción de la ubicación del dispositivo. La capa de dominio depende de
 * este contrato; la implementación concreta (FusedLocationProvider, Play
 * Services) vive en `data/` y se inyecta desde `AppModule`.
 */
interface LocationProvider {
    /** Ubicación actual, o `null` si no hay permiso o no se pudo obtener un fix. */
    suspend fun currentLocation(): Coordinates?
}
