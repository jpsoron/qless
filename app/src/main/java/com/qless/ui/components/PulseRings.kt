package com.qless.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Anillos concéntricos que emanan desde el centro y se desvanecen al expandirse.
 * Es el "latido" del beeper de QLess: indica que el pedido está vivo y en curso.
 *
 * Pensado para dibujarse DETRÁS del círculo central (el beeper), llenando un Box
 * más grande que ese círculo para que los pulsos salgan por fuera de su borde.
 *
 * @param color color del pulso (ámbar = en preparación, verde = listo).
 * @param ringCount cantidad de anillos en vuelo simultáneo, desfasados en fase.
 * @param durationMillis duración de un anillo desde el centro hasta desvanecerse.
 * @param startRadiusFraction radio inicial como fracción del radio máximo; debe
 *        coincidir aprox. con el borde del círculo central para que "salga" de él.
 * @param maxAlpha opacidad del anillo recién nacido (se apaga hacia 0 al expandirse).
 */
@Composable
fun PulseRings(
    color: Color,
    modifier: Modifier = Modifier,
    ringCount: Int = 3,
    durationMillis: Int = 2600,
    startRadiusFraction: Float = 0.6f,
    strokeWidth: Dp = 2.dp,
    maxAlpha: Float = 0.45f,
) {
    val transition = rememberInfiniteTransition(label = "pulse_rings")
    // Un float por anillo, todos 0→1 pero arrancando desfasados en el tiempo.
    val fractions = (0 until ringCount).map { i ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(i * durationMillis / ringCount),
            ),
            label = "ring_$i",
        )
    }
    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }

    Canvas(modifier = modifier) {
        val maxRadius = size.minDimension / 2f
        val startRadius = maxRadius * startRadiusFraction
        fractions.forEach { fraction ->
            val f = fraction.value
            val radius = startRadius + (maxRadius - startRadius) * f
            drawCircle(
                color = color.copy(alpha = (1f - f) * maxAlpha),
                radius = radius,
                style = Stroke(width = strokePx),
            )
        }
    }
}
