package com.qless.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// TOKENS PRIMITIVOS
// No referenciar directamente desde composables. Usar siempre
// MaterialTheme.colorScheme.* o QLessStatusColors.*
// ─────────────────────────────────────────────────────────────────────────────

// Base
val CremaCálida    = Color(0xFFFFFBF5)
val Mantequilla    = Color(0xFFFFF8EE)
val Espresso       = Color(0xFF2C1A0E)
val Madera         = Color(0xFF7A5C3E)

// Primario
val Pimentón       = Color(0xFFC44B1B)
val Tomate         = Color(0xFFE8673A)
val Melocotón      = Color(0xFFFFEDE0)

// Variantes dark del primario
val PimentónDark   = Color(0xFFFF7A45)

// Semánticos — estados del pedido
val Albahaca       = Color(0xFF1A7A4A)
val AlbahacaClaro  = Color(0xFFD6F5E3)
val Azafrán        = Color(0xFFD4870E)
val AzafránClaro   = Color(0xFFFFF3D6)
val Arándano       = Color(0xFF1D6FA8)
val ArándanoClaro  = Color(0xFFD6ECFA)
val Borgoña        = Color(0xFF8C2A2A)
val BorgoñaClaro   = Color(0xFFFAD6D6)

// Superficies dark
val EspressoDark   = Color(0xFF1A0F07)
val MaderaOscura   = Color(0xFF3D2B1A)

// ─────────────────────────────────────────────────────────────────────────────
// ESQUEMA CLARO
// ─────────────────────────────────────────────────────────────────────────────
val LightColorScheme = lightColorScheme(
    primary              = Pimentón,
    onPrimary            = Color.White,
    primaryContainer     = Melocotón,
    onPrimaryContainer   = Espresso,

    secondary            = Madera,
    onSecondary          = Color.White,
    secondaryContainer   = Mantequilla,
    onSecondaryContainer = Espresso,

    tertiary             = Azafrán,
    onTertiary           = Color.White,
    tertiaryContainer    = AzafránClaro,
    onTertiaryContainer  = Espresso,

    error                = Borgoña,
    onError              = Color.White,
    errorContainer       = BorgoñaClaro,
    onErrorContainer     = Borgoña,

    background           = CremaCálida,
    onBackground         = Espresso,
    surface              = CremaCálida,
    onSurface            = Espresso,
    surfaceVariant       = Mantequilla,
    onSurfaceVariant     = Madera,

    outline              = Madera,
    outlineVariant       = Melocotón,

    inverseSurface       = Espresso,
    inverseOnSurface     = CremaCálida,
    inversePrimary       = Tomate,
)

// ─────────────────────────────────────────────────────────────────────────────
// ESQUEMA OSCURO
// ─────────────────────────────────────────────────────────────────────────────
val DarkColorScheme = darkColorScheme(
    primary              = PimentónDark,
    onPrimary            = EspressoDark,
    primaryContainer     = Pimentón,
    onPrimaryContainer   = Melocotón,

    secondary            = Color(0xFFD4A98A),
    onSecondary          = MaderaOscura,
    secondaryContainer   = MaderaOscura,
    onSecondaryContainer = Color(0xFFEFD5BC),

    tertiary             = Color(0xFFFFCC70),
    onTertiary           = Color(0xFF3A2200),
    tertiaryContainer    = Color(0xFF543200),
    onTertiaryContainer  = Color(0xFFFFCC70),

    error                = Color(0xFFFFB4AB),
    onError              = Color(0xFF690005),
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = Color(0xFFFFDAD6),

    background           = EspressoDark,
    onBackground         = Color(0xFFEFDDD0),
    surface              = EspressoDark,
    onSurface            = Color(0xFFEFDDD0),
    surfaceVariant       = MaderaOscura,
    onSurfaceVariant     = Color(0xFFD4A98A),

    outline              = Color(0xFF9E7A5A),
    outlineVariant       = MaderaOscura,

    inverseSurface       = Color(0xFFEFDDD0),
    inverseOnSurface     = Espresso,
    inversePrimary       = Pimentón,
)

// ─────────────────────────────────────────────────────────────────────────────
// COLORES SEMÁNTICOS — estados del pedido
// Uso directo en composables de tracking/badge. Fuera del esquema M3.
//
// Ejemplo de uso:
//   StatusBadge(color = QLessStatusColors.enPreparacion)
// ─────────────────────────────────────────────────────────────────────────────
object QLessStatusColors {
    val disponible           = Albahaca
    val disponibleSurface    = AlbahacaClaro
    val enPreparacion        = Azafrán
    val enPreparacionSurface = AzafránClaro
    val enCamino             = Arándano
    val enCaminoSurface      = ArándanoClaro
    val agotadoError         = Borgoña
    val agotadoSurface       = BorgoñaClaro
}