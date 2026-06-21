package com.qless.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.domain.model.Order
import com.qless.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrderSummaryScreen(
    order: Order?,
    isDarkTheme: Boolean = false,
    onBack: () -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp).statusBarsPadding())

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Resumen del pedido",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (order == null) {
                Box(Modifier.fillMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val formatted = NumberFormat.getNumberInstance(Locale("es", "AR")).format(order.totalAmount)

                Spacer(Modifier.height(2.dp))

                Text(
                    "Pedido #${order.numero} · ${order.localNombre}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(8.dp))

                StatusBadgeRow(status = order.status)

                Spacer(Modifier.height(16.dp))

                // Encabezado: local y total
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDarkTheme) MaderaOscura else Color.White.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, Color(0xFFE4CDBB))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                order.localNombre,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp
                            )
                            Text(
                                "Retiro en mostrador",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Surface(shape = RoundedCornerShape(999.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Text(
                                "$$formatted",
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp),
                                color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))

                // Items
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, Color(0xFFE4CDBB))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Items", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        Spacer(Modifier.height(8.dp))
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${item.quantity}x ${item.nombre}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "$${NumberFormat.getNumberInstance(Locale("es", "AR")).format(item.unitPrice * item.quantity)}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        if (order.items.size > 1) {
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFE4CDBB))
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                                Text("$$formatted", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))

                // Seguimiento
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDarkTheme) MaderaOscura else Color.White.copy(alpha = 0.42f),
                    border = BorderStroke(1.dp, Color(0xFFE4CDBB))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Seguimiento", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        Spacer(Modifier.height(18.dp))

                        val stepsDone = when (order.status) {
                            "pending"   -> 1
                            "preparing" -> 2
                            "ready"     -> 3
                            "picked_up" -> 4
                            else        -> 1
                        }

                        SummaryStep("Pedido recibido", "Pago confirmado, pedido enviado al local", done = stepsDone >= 1, showLine = true)
                        SummaryStep("En preparación", "La cocina está armando el pedido", done = stepsDone >= 2, showLine = true)
                        SummaryStep("Listo para retirar", "El pedido quedó listo en el mostrador", done = stepsDone >= 3, showLine = true)
                        SummaryStep("Retirado", "El pedido fue retirado correctamente", done = stepsDone >= 4, showLine = false)
                    }
                }

                Spacer(Modifier.height(22.dp))

                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("← Volver al historial", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun StatusBadgeRow(status: String) {
    val (label, color, bg) = when (status) {
        "pending"   -> Triple("Confirmado", QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
        "preparing" -> Triple("En preparación", QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
        "ready"     -> Triple("Listo para retirar", QLessStatusColors.disponible, QLessStatusColors.disponibleSurface)
        "picked_up" -> Triple("Retirado", QLessStatusColors.disponible, QLessStatusColors.disponibleSurface)
        "cancelled" -> Triple("Cancelado", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
        else        -> Triple(status, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
    }
    Surface(shape = RoundedCornerShape(999.dp), color = bg) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SummaryStep(title: String, detail: String, done: Boolean, showLine: Boolean) {
    Row(verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (done) QLessStatusColors.disponible else MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (done) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
            }
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(34.dp)
                        .background(if (done) QLessStatusColors.disponible else MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (showLine) 8.dp else 0.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(detail, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}
