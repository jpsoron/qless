package com.qless.ui.screens.clients

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.components.PulseRings
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*

private data class TrackingStep(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val status: StepStatus,
    val time: String? = null,
)

private enum class StepStatus { DONE, ACTIVE, PENDING }

@Composable
fun TrackingScreen(
    orderCode: String = "----",
    localNombre: String = "",
    status: String = "preparing",
    onGoHome: () -> Unit,
    onNavigateToMisLocales: () -> Unit = {},
    onNavigateToMisPedidos: () -> Unit = {},
    onNavigateToAjustes: () -> Unit = {},
    onNavigateToScanQr: () -> Unit = {},
    onReadyComplete: () -> Unit = {},
) {
    val steps = listOf(
        TrackingStep(
            Icons.Default.CheckCircle, "Pedido recibido", "Tu compra fue confirmada",
            StepStatus.DONE
        ),
        TrackingStep(
            Icons.Default.Schedule, "En preparación", "La cocina está armando tu pedido",
            when (status) {
                "pending" -> StepStatus.PENDING
                "preparing" -> StepStatus.ACTIVE
                else -> StepStatus.DONE
            }
        ),
        TrackingStep(
            Icons.Default.Notifications, "Listo para retirar", "Te avisamos cuando esté listo",
            when (status) {
                "ready" -> StepStatus.ACTIVE
                else -> StepStatus.PENDING
            }
        ),
    )

    // Indicador circular: cada estado define un objetivo discreto del borde. El
    // anillo arranca en 0 al abrir y se desliza suavemente hasta su objetivo (p.
    // ej. 0→33% al entrar), y cuando el pedido avanza tweenea de un tramo al otro
    // sin saltar. Cuando llega a "ready", recién al terminar la animación
    // (66→100%) avisamos para que la navegación a la próxima pantalla espere.
    val infiniteTransition = rememberInfiniteTransition(label = "timer_pulse")
    val targetProgress = when (status) {
        "pending" -> 0.33f
        "preparing" -> 0.66f
        else -> 1f
    }
    val progressAnim = remember { Animatable(0f) }
    LaunchedEffect(targetProgress) {
        progressAnim.animateTo(
            targetValue = targetProgress,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
        if (status == "ready") onReadyComplete()
    }
    val progress = progressAnim.value
    // "Respiro" sutil del beeper para que se sienta vivo
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    Scaffold(
        bottomBar = {
            QLessBottomNav(
                selectedTab = 0,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onGoHome()
                        1 -> onNavigateToMisLocales()
                        2 -> onNavigateToMisPedidos()
                        3 -> onNavigateToAjustes()
                        4 -> onNavigateToScanQr()
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp).statusBarsPadding())

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "Seguimiento",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("Pedido #$orderCode${if (localNombre.isNotEmpty()) " · $localNombre" else ""}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
                val (badgeLabel, badgeColor, badgeBg) = when (status) {
                    "pending"   -> Triple("Pago confirmado",    Arándano,                      ArándanoClaro)
                    "preparing" -> Triple("En preparación",     QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
                    "ready"     -> Triple("Listo para retirar", QLessStatusColors.disponible,  QLessStatusColors.disponibleSurface)
                    else        -> Triple("Pedido activo",      QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = badgeBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Text(badgeLabel, style = MaterialTheme.typography.labelSmall, color = badgeColor, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Spacer(Modifier.height(24.dp))

            // Color unificado del beeper: azul para recibido, ámbar para cocina, verde para listo.
            val beeperColor = when (status) {
                "pending" -> Arándano
                "ready"   -> QLessStatusColors.disponible
                else      -> QLessStatusColors.enPreparacion
            }

            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                PulseRings(
                    color = beeperColor,
                    modifier = Modifier.fillMaxSize(),
                    startRadiusFraction = 0.66f
                )
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(breathe),
                    contentAlignment = Alignment.Center
                ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = beeperColor,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.RoomService,
                        contentDescription = null,
                        tint = beeperColor,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        when (status) {
                            "pending" -> "CONFIRMADO"
                            "ready" -> "¡LISTO!"
                            else -> "EN COCINA"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = beeperColor,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp
                    )
                }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Timeline
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                steps.forEachIndexed { index, step ->
                    TrackingStepRow(step = step)
                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .padding(start = 19.dp)
                                .width(2.dp)
                                .height(24.dp)
                                .background(
                                    if (step.status == StepStatus.DONE) QLessStatusColors.disponible
                                    else if (step.status == StepStatus.ACTIVE) QLessStatusColors.enPreparacion
                                    else MaterialTheme.colorScheme.primaryContainer
                                )
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Código de retiro
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Mostrá este código al retirar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
                        Text(
                            "#$orderCode",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f), modifier = Modifier.size(14.dp))
                            Text("Retiro en mostrador", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
                        }
                    }
                    DecorativeQrCode()
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TrackingStepRow(step: TrackingStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when (step.status) {
                        StepStatus.DONE -> QLessStatusColors.disponible
                        StepStatus.ACTIVE -> QLessStatusColors.enPreparacion
                        StepStatus.PENDING -> MaterialTheme.colorScheme.primaryContainer
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = step.icon,
                contentDescription = null,
                tint = if (step.status == StepStatus.PENDING) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f).padding(top = 4.dp)) {
            Text(
                step.title,
                fontWeight = FontWeight.SemiBold,
                color = if (step.status == StepStatus.PENDING) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(step.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (step.time != null) {
                Text(
                    step.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (step.status == StepStatus.DONE) QLessStatusColors.disponible else QLessStatusColors.enPreparacion,
                    fontWeight = FontWeight.SemiBold
                )
            }
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

@Preview(showBackground = true)
@Composable
private fun TrackingPreview() {
    QLessTheme { TrackingScreen(onGoHome = {}) }
}