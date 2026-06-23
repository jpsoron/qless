package com.qless.domain.model

/**
 * Envuelve un dato indicando su procedencia (RF4 — modo offline).
 *
 * - [fromCache] = false → vino fresco de la red y ya se reescribió en la caché.
 * - [fromCache] = true  → la red falló y se sirvió la última copia local.
 *
 * Un `Result.failure` significa que falló la red **y** no había nada cacheado.
 */
data class CachedResult<T>(
    val data: T,
    val fromCache: Boolean,
)
