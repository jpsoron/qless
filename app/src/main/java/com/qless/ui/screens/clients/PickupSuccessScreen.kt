package com.qless.ui.screens.clients

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.domain.model.Order
import com.qless.domain.model.OrderItem
import com.qless.ui.theme.*

@Composable
fun PickupSuccessScreen(
    userName: String,
    order: Order?,
    isDarkTheme: Boolean = false,
    onGoHome: () -> Unit,
    onViewSummary: () -> Unit
) {
    val firstName = userName.trim().substringBefore(" ").ifBlank { "crack" }
    val initial = userName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background 
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = padding.calculateBottomPadding()) // Solo padding inferior
        ) {
            // Header con degradado verde que llega hasta arriba
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp) // Más altura para evitar superposición
                    .background(QLessStatusColors.disponible)
                    .statusBarsPadding(), 
                contentAlignment = Alignment.TopCenter // Alineamos arriba
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 24.dp) // Espacio fijo desde la status bar
                ) {
                    // Avatar Circular
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(initial, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "RETIRO EXITOSO",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 2.sp
                    )
                    Text(
                        "¡Buen provecho,\n$firstName! 🥳",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tu pedido fue retirado con éxito",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-30).dp) // Superponer las tarjetas al header verde
            ) {
                // Tarjeta de Detalle del Local
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(order?.localEmoji?.ifBlank { "🍽️" } ?: "🍽️", fontSize = 24.sp)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(order?.localNombre?.ifBlank { "Tu pedido" } ?: "Tu pedido", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    "Pedido #${order?.numero ?: "----"} · Retiro en mostrador",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text("$${"%,d".format(order?.totalAmount ?: 0)}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.primaryContainer)

                        // Lista de Items del pedido
                        order?.items?.forEach { item ->
                            OrderItemRow(item.nombre, "x${item.quantity}")
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.primaryContainer)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total $${"%,d".format(order?.totalAmount ?: 0)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = QLessStatusColors.disponibleSurface
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = QLessStatusColors.disponible, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Retirado", style = MaterialTheme.typography.labelSmall, color = QLessStatusColors.disponible, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                // Tarjeta de calificación oculta: todavía no hay funcionalidad de reseñas.

                Spacer(Modifier.height(32.dp))

                // Botones de acción
                Button(
                    onClick = onGoHome,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Pimentón else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Volver al inicio", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                
                Spacer(Modifier.height(12.dp))
                
                TextButton(
                    onClick = onViewSummary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver resumen del pedido", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                }
                
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun OrderItemRow(name: String, quantity: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
        Text(quantity, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview
@Composable
private fun PickupSuccessPreview() {
    QLessTheme {
        PickupSuccessScreen(
            userName = "María González",
            order = Order(
                id = "1", numero = 4521, userId = "u1", localId = "l1",
                localNombre = "Big Pons", localEmoji = "🍔",
                status = "picked_up", totalAmount = 9000, createdAt = "",
                items = listOf(
                    OrderItem("i1", "Combo Big Classic", 4000, 2),
                    OrderItem("i2", "Papas Fritas Grandes", 1000, 1),
                ),
            ),
            onGoHome = {},
            onViewSummary = {},
        )
    }
}
