package com.qless.viewmodel

import com.qless.domain.usecase.RankLocalsByDistanceUseCase
import com.qless.domain.usecase.haversineMeters
import com.qless.fakes.sampleLocal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests puros (sin Android) de la lógica de distancia y ranking por cercanía.
 */
class LocationUseCasesTest {

    @Test
    fun `haversine entre el mismo punto es 0`() {
        val d = haversineMeters(-34.4718, -58.5124, -34.4718, -58.5124)
        assertEquals(0.0, d, 0.0001)
    }

    @Test
    fun `haversine de 0_001 grados de latitud es aprox 111 metros`() {
        // 1° de latitud ≈ 111.32 km ⇒ 0.001° ≈ 111.3 m.
        val d = haversineMeters(0.0, 0.0, 0.001, 0.0)
        assertEquals(111.3, d, 1.0)
    }

    @Test
    fun `rank ordena por cercania y deja los sin ubicacion al final`() {
        val user = sampleLocal(id = "user", latitud = -34.4718, longitud = -58.5124)
        val cerca = sampleLocal(id = "cerca", latitud = -34.4720, longitud = -58.5126) // ~30 m
        val lejos = sampleLocal(id = "lejos", latitud = -34.6037, longitud = -58.3816) // CABA, ~21 km
        val sinUbicacion = sampleLocal(id = "sin") // 0,0

        val ranked = RankLocalsByDistanceUseCase()
            .invoke(user.latitud, user.longitud, listOf(lejos, sinUbicacion, cerca))

        assertEquals(listOf("cerca", "lejos", "sin"), ranked.map { it.id })
        assertNull(ranked.last().distanciaMetros)
        assertTrue(ranked.first().distanciaMetros!! < 100.0)
    }
}
