package com.qless.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onGoHome: () -> Unit,
    onNavigateToOrderReady: () -> Unit,
) {
    val steps = listOf(
        TrackingStep(Icons.Default.CheckCircle, "Pedido recibido", "Tu compra fue confirmada", StepStatus.DONE, "13:08"),
        TrackingStep(Icons.Default.Schedule, "En preparación", "La cocina está armando tu pedido", StepStatus.ACTIVE, "13:11"),
        TrackingStep(Icons.Default.Notifications, "Listo para retirar", "Te avisamos cuando esté listo", StepStatus.PENDING),
    )

    // Animación del indicador circular
    val infiniteTransition = rememberInfiniteTransition(label = "timer_pulse")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )

    Scaffold(
        bottomBar = {
            QLessBottomNav(selectedTab = 0, onTabSelected = { if (it == 0) onGoHome() })
        },
        containerColor = CremaCálida
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
                        color = Espresso
                    )
                    Text("Pedido #4521 · Big Pons", color = Madera, style = MaterialTheme.typography.bodySmall)
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = AzafránClaro
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
                                .background(Azafrán)
                        )
                        Text("En preparación", style = MaterialTheme.typography.labelSmall, color = Azafrán, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Madera, modifier = Modifier.size(14.dp))
                    Text("~15 min estimados", style = MaterialTheme.typography.bodySmall, color = Madera)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Timer circular
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = Azafrán,
                    trackColor = Melocotón,
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Azafrán,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        "12",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Espresso,
                        lineHeight = 52.sp
                    )
                    Text("min", color = Madera, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "ESTIMADO",
                        style = MaterialTheme.typography.labelSmall,
                        color = Madera.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )
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
                                    if (step.status == StepStatus.DONE) Albahaca
                                    else if (step.status == StepStatus.ACTIVE) Azafrán
                                    else Melocotón
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
                color = Melocotón,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Mostrá este código al retirar", style = MaterialTheme.typography.bodySmall, color = Madera)
                        Text(
                            "#4521",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Pimentón
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Madera, modifier = Modifier.size(14.dp))
                            Text("Retiro en Caja 1", style = MaterialTheme.typography.bodySmall, color = Madera)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White
                    ) {
                        Text(
                            "▦▦\n▦▦",
                            modifier = Modifier.padding(12.dp),
                            color = Espresso,
                            fontSize = 24.sp,
                            lineHeight = 26.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onNavigateToOrderReady,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Espresso, modifier = Modifier.size(18.dp))
                    Text("Simular: Pedido Listo", color = Espresso)
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
                        StepStatus.DONE -> Albahaca
                        StepStatus.ACTIVE -> Azafrán
                        StepStatus.PENDING -> Melocotón
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = step.icon,
                contentDescription = null,
                tint = if (step.status == StepStatus.PENDING) Madera else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f).padding(top = 4.dp)) {
            Text(
                step.title,
                fontWeight = FontWeight.SemiBold,
                color = if (step.status == StepStatus.PENDING) Madera else Espresso,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(step.description, style = MaterialTheme.typography.bodySmall, color = Madera)
            if (step.time != null) {
                Text(
                    step.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (step.status == StepStatus.DONE) Albahaca else Azafrán,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackingPreview() {
    QLessTheme { TrackingScreen(onGoHome = {}, onNavigateToOrderReady = {}) }
}