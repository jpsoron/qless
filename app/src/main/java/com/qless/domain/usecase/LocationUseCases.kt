package com.qless.domain.usecase

import com.qless.domain.location.LocationProvider
import com.qless.domain.model.Coordinates
import com.qless.domain.model.Local
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** Distancia máxima para considerar que el usuario está "en" un local. */
const val NEARBY_THRESHOLD_METERS = 50.0

/** ¿El local tiene coordenadas cargadas? (0,0 ⇒ sin geocodificar). */
fun Local.hasLocation(): Boolean = latitud != 0.0 || longitud != 0.0

/**
 * Distancia en metros entre dos coordenadas (fórmula de Haversine).
 * Kotlin puro, sin dependencias de Android → testeable en JVM.
 */
fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusM = 6_371_000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return 2 * earthRadiusM * asin(min(1.0, sqrt(a)))
}

/** Obtiene la ubicación actual del dispositivo a través del [LocationProvider]. */
class GetCurrentLocationUseCase(private val provider: LocationProvider) {
    suspend operator fun invoke(): Coordinates? = provider.currentLocation()
}

/**
 * Anota cada local con su [Local.distanciaMetros] respecto del usuario y los
 * ordena por cercanía. Los locales sin ubicación quedan con `null` y al final.
 */
class RankLocalsByDistanceUseCase {
    operator fun invoke(userLat: Double, userLng: Double, locales: List<Local>): List<Local> =
        locales
            .map { local ->
                val distance = if (local.hasLocation()) {
                    haversineMeters(userLat, userLng, local.latitud, local.longitud)
                } else {
                    null
                }
                local.copy(distanciaMetros = distance)
            }
            .sortedBy { it.distanciaMetros ?: Double.MAX_VALUE }
}
