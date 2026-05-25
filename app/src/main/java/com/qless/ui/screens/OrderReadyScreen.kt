package com.qless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

@Composable
fun OrderReadyScreen(
    onConfirmPickup: () -> Unit,
) {
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
                color = AlbahacaClaro
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
                            .background(Albahaca)
                    )
                    Text(
                        "¡Listo para retirar!",
                        style = MaterialTheme.typography.labelMedium,
                        color = Albahaca,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Central Visual - Segmented Circle (Compact)
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                SegmentedCircle(
                    segments = 4,
                    progress = 4,
                    activeColor = Albahaca,
                    inactiveColor = MaderaOscura,
                    modifier = Modifier.fillMaxSize()
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "PEDIDO #4521",
                        style = MaterialTheme.typography.labelSmall,
                        color = Madera,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("🔔", fontSize = 32.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Tu pedido\nestá listo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Information Card (Compact)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Albahaca
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Mostrá este código",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            "#4521",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            InfoTag(text = "📍 Caja 1")
                            InfoTag(text = "🍔 Big Pons")
                        }
                        Spacer(Modifier.height(6.dp))
                        InfoTag(text = "⏱ Listo 13:24")
                    }
                    
                    // QR Code Placeholder
                    Surface(
                        modifier = Modifier.size(70.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "▦▦\n▦▦",
                                color = Espresso,
                                fontSize = 36.sp,
                                lineHeight = 30.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
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
                    containerColor = Albahaca,
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
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))
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
                .background(if (isHighlight) Albahaca else Albahaca.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = if (isHighlight) Color.White else Albahaca,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                time,
                style = MaterialTheme.typography.labelSmall,
                color = Madera
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
