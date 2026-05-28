package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

@Composable
fun PickupSuccessScreen(
    onGoHome: () -> Unit,
    onViewSummary: () -> Unit
) {
    Scaffold(
        containerColor = CremaCálida 
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
                    .background(Albahaca)
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
                        color = Pimentón
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("M", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
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
                        "¡Buen provecho,\nMaría! 🥳",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tu pedido fue retirado a las 13:25",
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
                    color = Mantequilla,
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
                                    Text("🍔", fontSize = 24.sp)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Big Pons · San Isidro", fontWeight = FontWeight.SemiBold, color = Espresso)
                                Text("Pedido #4521 · Retiro en mostrador", style = MaterialTheme.typography.bodySmall, color = Madera)
                            }
                            Text("$9.000", fontWeight = FontWeight.SemiBold, color = Espresso, fontSize = 18.sp)
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Melocotón)
                        
                        // Lista de Items
                        OrderItemRow("Combo Big Classic", "x2")
                        OrderItemRow("Papas Fritas Grandes", "x1")
                        OrderItemRow("Gaseosa 500 ml", "x1")
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Melocotón)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Visa •••• 4242 · $9.000", style = MaterialTheme.typography.bodySmall, color = Madera)
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = AlbahacaClaro
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Albahaca, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Retirado", style = MaterialTheme.typography.labelSmall, color = Albahaca, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Tarjeta de Calificación
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Mantequilla,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("¿Cómo estuvo Big Pons?", fontWeight = FontWeight.SemiBold, color = Espresso)
                        Text("Tu opinión ayuda a otros usuarios", style = MaterialTheme.typography.bodySmall, color = Madera)
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(4) { Icon(Icons.Filled.Star, contentDescription = null, tint = Azafrán, modifier = Modifier.size(32.dp)) }
                            Icon(Icons.Outlined.Star, contentDescription = null, tint = Madera.copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
                        }
                        
                        Spacer(Modifier.height(12.dp))
                        Text("Tocá para calificar", style = MaterialTheme.typography.labelSmall, color = Madera.copy(alpha = 0.5f))
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Botones de acción
                Button(
                    onClick = onGoHome,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Pimentón)
                ) {
                    Text("Volver al inicio", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                
                Spacer(Modifier.height(12.dp))
                
                TextButton(
                    onClick = onViewSummary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver resumen del pedido", color = Espresso, fontWeight = FontWeight.SemiBold)
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
        Text(name, color = Espresso, style = MaterialTheme.typography.bodyMedium)
        Text(quantity, color = Madera, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview
@Composable
private fun PickupSuccessPreview() {
    QLessTheme {
        PickupSuccessScreen(onGoHome = {}, onViewSummary = {})
    }
}
