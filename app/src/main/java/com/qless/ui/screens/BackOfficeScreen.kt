package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
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
fun BackOfficeScreen(
    onNavigateToHistory: () -> Unit,
    onUpdateOrder: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BackOfficeBottomNav(
                selectedTab = 1,
                onTabSelected = { tab ->
                    if (tab == 2) onNavigateToHistory()
                }
            )
        },
        containerColor = CremaCálida
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(MaderaOscura)
                    .statusBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🔔", fontSize = 32.sp)
                    Text(
                        "BackOffice",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Pedidos en curso",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Espresso
                )
                Text(
                    "Gestioná los pedidos activos del local y actualizá su estado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Madera
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Melocotón
                    ) {
                        Text(
                            "Big Pons · San Isidro",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Pimentón,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón)
                    ) {
                        Text(
                            "12 pedidos activos",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Madera
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Filters
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = true, text = "Todos")
                    FilterChip(selected = false, text = "En preparación")
                    FilterChip(selected = false, text = "Listos")
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    "Tocá un pedido para ver su detalle y actualizar el estado.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Madera.copy(alpha = 0.5f)
                )

                Spacer(Modifier.height(16.dp))

                // Orders List
                BackOfficeOrderCard(
                    initials = "MG",
                    orderNum = "#5930",
                    customer = "Mateo Gómez",
                    details = "4 items · listo para empaquetar",
                    status = "En preparación",
                    statusColor = Azafrán,
                    statusBg = AzafránClaro,
                    onClick = onUpdateOrder
                )

                BackOfficeOrderCard(
                    initials = "LM",
                    orderNum = "#5928",
                    customer = "Lucía Méndez",
                    details = "2 combos · recibido hace 11 min",
                    status = "Pago confirmado",
                    statusColor = Madera,
                    statusBg = Melocotón,
                    onClick = onUpdateOrder
                )

                BackOfficeOrderCard(
                    initials = "JP",
                    orderNum = "#5924",
                    customer = "Juan Pérez",
                    details = "1 ítem · retiro inmediato",
                    status = "Listo para retirar",
                    statusColor = Albahaca,
                    statusBg = AlbahacaClaro,
                    onClick = onUpdateOrder
                )

                BackOfficeOrderCard(
                    initials = "CR",
                    orderNum = "#5921",
                    customer = "Camila Ruiz",
                    details = "3 items · recibido hace 4 min",
                    status = "En preparación",
                    statusColor = Azafrán,
                    statusBg = AzafránClaro,
                    onClick = onUpdateOrder
                )

                Spacer(Modifier.height(24.dp))

                // Shift Summary Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Resumen del turno",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Espresso
                            )
                            Spacer(Modifier.height(8.dp))
                            SummaryRow("4 en preparación")
                            SummaryRow("3 listos para retirar")
                            SummaryRow("5 pendientes de cocina")
                        }
                        
                        VerticalDivider(
                            modifier = Modifier.height(80.dp).padding(horizontal = 16.dp),
                            color = Melocotón
                        )
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Próximo retiro", style = MaterialTheme.typography.labelSmall, color = Madera)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("7", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Pimentón)
                                Text("min", modifier = Modifier.padding(bottom = 8.dp, start = 4.dp), color = Madera, fontSize = 14.sp)
                            }
                            Text("12:35", style = MaterialTheme.typography.labelSmall, color = Madera.copy(alpha = 0.6f))
                        }
                    }
                }
                
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun FilterChip(selected: Boolean, text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) Pimentón else Color.White,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Melocotón)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) Color.White else Espresso,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun BackOfficeOrderCard(
    initials: String,
    orderNum: String,
    customer: String,
    details: String,
    status: String,
    statusColor: Color,
    statusBg: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = Melocotón
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(initials, color = Pimentón, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            "Pedido $orderNum",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Espresso
                        )
                        Text(customer, style = MaterialTheme.typography.bodyMedium, color = Madera)
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = statusBg
                    ) {
                        Text(
                            status,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(details, style = MaterialTheme.typography.labelSmall, color = Madera.copy(alpha = 0.6f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Actualizar",
                            style = MaterialTheme.typography.labelSmall,
                            color = Pimentón,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Pimentón,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = Madera,
        modifier = Modifier.padding(vertical = 1.dp)
    )
}

@Composable
private fun BackOfficeBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Icons.Default.Home, "Inicio", selectedTab == 0) { onTabSelected(0) }
            NavItem(Icons.AutoMirrored.Filled.List, "Pedidos en curso", selectedTab == 1) { onTabSelected(1) }
            NavItem(Icons.Outlined.DateRange, "Historial", selectedTab == 2) { onTabSelected(2) }
            NavItem(Icons.Default.Settings, "Ajustes", selectedTab == 3) { onTabSelected(3) }
        }
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) Pimentón else Madera.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            label,
            fontSize = 10.sp,
            color = if (isSelected) Pimentón else Madera.copy(alpha = 0.5f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview
@Composable
private fun BackOfficePreview() {
    QLessTheme {
        BackOfficeScreen(onNavigateToHistory = {}, onUpdateOrder = {})
    }
}
