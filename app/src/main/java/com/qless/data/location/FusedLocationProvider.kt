package com.qless.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.qless.domain.location.LocationProvider
import com.qless.domain.model.Coordinates
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Implementación de [LocationProvider] sobre FusedLocationProvider (Play Services).
 * Encapsula el SDK de Android para que los ViewModels/Composables no dependan de él.
 */
class FusedLocationProvider(private val context: Context) : LocationProvider {

    @SuppressLint("MissingPermission") // El permiso se chequea en hasLocationPermission().
    override suspend fun currentLocation(): Coordinates? {
        if (!hasLocationPermission()) return null
        val client = LocationServices.getFusedLocationProviderClient(context)
        return suspendCancellableCoroutine { cont ->
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    cont.resume(location?.let { Coordinates(it.latitude, it.longitude) })
                }
                .addOnFailureListener { cont.resume(null) }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }
}
