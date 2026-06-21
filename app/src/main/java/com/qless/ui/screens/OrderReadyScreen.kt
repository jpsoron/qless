package com.qless.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import com.qless.ui.components.PulseRings
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.Espresso
import com.qless.ui.theme.EspressoDark
import com.qless.ui.theme.MaderaOscura
import com.qless.ui.theme.Pimentón
import com.qless.ui.theme.QLessStatusColors
import com.qless.ui.theme.QLessTheme

@Composable
fun OrderReadyScreen(
    orderCode: String = "------",
    localNombre: String = "",
    onConfirmPickup: () -> Unit,
) {
    // Revelado de entrada: el beeper "se enciende" en verde con un pop elástico
    // al llegar acá, dando la sensación de que la pantalla de seguimiento mutó
    // al estado "listo".
    val reveal = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        reveal.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.55f, stiffness = 220f)
        )
    }

    Scaffold(
        containerColor = EspressoDark
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp).statusBarsPadding())

            // Header Status Badge
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = QLessStatusColors.disponibleSurface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(QLessStatusColors.disponible)
                    )
                    Text(
                        "¡Listo para retirar!",
                        style = MaterialTheme.typography.labelMedium,
                        color = QLessStatusColors.disponible,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Central Visual: beeper "listo" con pulsos verdes que llaman la
            // atención + revelado elástico de entrada.
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                PulseRings(
                    color = QLessStatusColors.disponible,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = reveal.value.coerceIn(0f, 1f) },
                    startRadiusFraction = 0.66f
                )
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer {
                            val s = 0.6f + 0.4f * reveal.value
                            scaleX = s
                            scaleY = s
                            alpha = reveal.value.coerceIn(0f, 1f)
                        },
                    contentAlignment = Alignment.Center
                ) {
                SegmentedCircle(
                    segments = 4,
                    progress = 4,
                    activeColor = QLessStatusColors.disponible,
                    inactiveColor = MaderaOscura,
                    modifier = Modifier.fillMaxSize()
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "PEDIDO #$orderCode",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Tu pedido\nestá listo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Information Card (Compact)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = QLessStatusColors.disponible
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Mostrá este código",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            "#$orderCode",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            InfoTag(text = "📍 Caja 1")
                            InfoTag(text = "🍔 ${localNombre.ifEmpty { "Local" }}")
                        }
                        Spacer(Modifier.height(6.dp))
                        InfoTag(text = "⏱ Listo 13:24")
                    }
                    
                    DecorativeQrCode()
                }
            }

            Spacer(Modifier.height(28.dp))

            // Timeline Summary (Compact)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReadyStepRow(title = "Pedido recibido", time = "13:08")
                HorizontalDivider(color = MaderaOscura.copy(alpha = 0.5f), thickness = 0.5.dp)
                ReadyStepRow(title = "En preparación", time = "13:11 — 13:24")
                HorizontalDivider(color = MaderaOscura.copy(alpha = 0.5f), thickness = 0.5.dp)
                ReadyStepRow(title = "Listo para retirar", time = "Ahora · esperando retiro", isHighlight = true)
            }

            Spacer(Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = onConfirmPickup,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QLessStatusColors.disponible,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.Check, 
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Confirmar retiro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DecorativeQrCode() {
    Surface(
        modifier = Modifier.size(76.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.White
    ) {
        Box(
            modifier = Modifier.padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            QrFinder(modifier = Modifier.align(Alignment.TopStart))
            QrFinder(modifier = Modifier.align(Alignment.TopEnd))
            QrFinder(modifier = Modifier.align(Alignment.BottomStart))

            val moduleColor = Espresso
            Box(Modifier.size(7.dp).offset(x = 27.dp, y = 3.dp).align(Alignment.TopStart).background(moduleColor, RoundedCornerShape(2.dp)))
            Box(Modifier.size(7.dp).offset(x = 3.dp, y = 27.dp).align(Alignment.TopStart).background(moduleColor, RoundedCornerShape(2.dp)))
            Box(Modifier.size(7.dp).offset(x = 27.dp, y = 27.dp).align(Alignment.TopStart).background(Pimentón, RoundedCornerShape(2.dp)))
            Box(Modifier.size(7.dp).offset(x = 39.dp, y = 27.dp).align(Alignment.TopStart).background(moduleColor, RoundedCornerShape(2.dp)))
            Box(Modifier.size(7.dp).offset(x = 27.dp, y = 39.dp).align(Alignment.TopStart).background(moduleColor, RoundedCornerShape(2.dp)))
            Box(Modifier.size(7.dp).offset(x = 39.dp, y = 39.dp).align(Alignment.TopStart).background(Pimentón, RoundedCornerShape(2.dp)))
            Box(Modifier.size(7.dp).offset(x = 51.dp, y = 39.dp).align(Alignment.TopStart).background(moduleColor, RoundedCornerShape(2.dp)))
            Box(Modifier.size(7.dp).offset(x = 39.dp, y = 51.dp).align(Alignment.TopStart).background(moduleColor, RoundedCornerShape(2.dp)))
            Box(Modifier.size(7.dp).offset(x = 51.dp, y = 51.dp).align(Alignment.TopStart).background(moduleColor, RoundedCornerShape(2.dp)))
        }
    }
}

@Composable
private fun QrFinder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(20.dp)
            .background(Espresso, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(13.dp)
                .background(Color.White, RoundedCornerShape(3.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(Pimentón, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
private fun SegmentedCircle(
    segments: Int,
    progress: Int,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 10.dp.toPx()
        val spacing = 10f
        val sweepAngle = (360f / segments) - spacing
        
        for (i in 0 until segments) {
            val startAngle = -90f + (i * (360f / segments)) + (spacing / 2)
            drawArc(
                color = if (i < progress) activeColor else inactiveColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun InfoTag(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ReadyStepRow(
    title: String,
    time: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isHighlight) QLessStatusColors.disponible else QLessStatusColors.disponible.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = if (isHighlight) Color.White else QLessStatusColors.disponible,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun OrderReadyPreview() {
    QLessTheme {
        OrderReadyScreen(onConfirmPickup = {})
    }
}
