package com.qless.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
// QLESS THEME EXTRAS
// Valores que no viven en MaterialTheme pero sí forman parte del design system.
// Acceso desde cualquier composable vía:
//   QLessThemeExtras.current.kratfAlpha
// ─────────────────────────────────────────────────────────────────────────────

data class QLessExtras(
    // Opacidad del patrón de textura sobre superficies (papel kraft / lino)
    // 0f = sin textura, 0.04f = sutil (recomendado), 0.08f = visible
    val kraftAlpha: Float = 0.04f,

    // Color base del patrón de textura
    val kraftColor: Color = Color(0xFF7A5C3E),

    // Radio de esquinas para cards de menú (más generoso que M3 default)
    val menuCardRadius: Float = 16f,

    // Radio de esquinas para chips de categoría
    val chipRadius: Float = 99f,
)

val LocalQLessExtras = staticCompositionLocalOf { QLessExtras() }

// Accessor de conveniencia — usar igual que MaterialTheme
object QLessTheme {
    val extras: QLessExtras
        @Composable get() = LocalQLessExtras.current
}

// ─────────────────────────────────────────────────────────────────────────────
// THEME PRINCIPAL
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun QLessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color desactivado intencionalmente.
    // La identidad visual de QLess requiere paleta gastronómica fija.
    // Habilitar solo si se decide soportar wallpaper-based theming en el futuro.
    dynamicColor: Boolean = false,
    // Ajuste fino de la textura de superficie. Pasá 0f para desactivarla.
    kraftAlpha: Float = if (darkTheme) 0.02f else 0.04f,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    val extras = QLessExtras(
        kraftAlpha = kraftAlpha,
        kraftColor = if (darkTheme) Color(0xFF9E7A5A) else Color(0xFF7A5C3E),
    )

    CompositionLocalProvider(LocalQLessExtras provides extras) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = AppTypography,
            content     = content,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TEXTURA DE SUPERFICIE — KraftSurface
//
// Composable de conveniencia que aplica el patrón kraft sobre cualquier
// superficie. Úsalo en lugar de Box/Surface cuando quieras la textura.
//
// Ejemplo:
//   KraftSurface(modifier = Modifier.fillMaxWidth()) {
//       Text("Big Pons")
//   }
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun KraftSurface(
    modifier: Modifier = Modifier,
    // Override puntual del alpha. Por defecto toma el valor del tema.
    kraftAlpha: Float = QLessTheme.extras.kraftAlpha,
    content: @Composable BoxScope.() -> Unit,
) {
    val dotColor = QLessTheme.extras.kraftColor.copy(alpha = kraftAlpha)

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Patrón de puntos en cuadrícula diagonal — evoca trama de papel kraft
            val spacing = 10.dp.toPx()
            val radius  = 0.8.dp.toPx()
            val cols = ceil(size.width  / spacing).toInt() + 2
            val rows = ceil(size.height / spacing).toInt() + 2

            for (row in -1..rows) {
                for (col in -1..cols) {
                    val offset = if (row % 2 == 0) 0f else spacing / 2f
                    drawCircle(
                        color  = dotColor,
                        radius = radius,
                        center = Offset(col * spacing + offset, row * spacing),
                    )
                }
            }
        }
        content()
    }
}