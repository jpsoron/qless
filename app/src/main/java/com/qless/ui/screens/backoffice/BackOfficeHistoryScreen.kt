package com.qless.ui.screens.backoffice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

@Composable
fun BackOfficeHistoryScreen(
    onBack: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAjustes: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BackOfficeBottomNav(
                selectedTab = 1,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onNavigateToOrders()
                        2 -> onNavigateToAjustes()
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp).statusBarsPadding())

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    "Historial de pedidos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Row(
                modifier = Modifier.padding(start = 48.dp, end = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Big Pons · San Isidro", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        "Últimos 7 días",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Filters
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = true, text = "Todos")
                FilterChip(selected = false, text = "Completados")
                FilterChip(selected = false, text = "Retirados")
            }

            Spacer(Modifier.height(24.dp))

            // Summary Stats Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem("28", "pedidos totales", MaterialTheme.colorScheme.primary)
                    VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.primaryContainer)
                    StatItem("22", "entregados", QLessStatusColors.disponible)
                    VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.primaryContainer)
                    StatItem("6", "aún activos", QLessStatusColors.enPreparacion)
                }
            }

            Spacer(Modifier.height(24.dp))

            // List (Sincronizada con BackOffice + Historial)
            HistoryOrderCard(
                initials = "MG",
                orderNum = "#5930",
                customer = "Mateo Gómez",
                details = "En curso · hoy 12:45 · $14.200",
                status = "En curso",
                statusColor = QLessStatusColors.enPreparacion,
                statusBg = QLessStatusColors.enPreparacionSurface
            )

            HistoryOrderCard(
                initials = "LM",
                orderNum = "#5928",
                customer = "Lucía Méndez",
                details = "En curso · hoy 12:40 · $21.500",
                status = "En curso",
                statusColor = QLessStatusColors.enPreparacion,
                statusBg = QLessStatusColors.enPreparacionSurface
            )

            HistoryOrderCard(
                initials = "JP",
                orderNum = "#5924",
                customer = "Juan Pérez",
                details = "En curso · hoy 12:38 · $8.900",
                status = "En curso",
                statusColor = QLessStatusColors.enPreparacion,
                statusBg = QLessStatusColors.enPreparacionSurface
            )

            HistoryOrderCard(
                initials = "CR",
                orderNum = "#5921",
                customer = "Camila Ruiz",
                details = "En curso · hoy 12:35 · $12.400",
                status = "En curso",
                statusColor = QLessStatusColors.enPreparacion,
                statusBg = QLessStatusColors.enPreparacionSurface
            )

            HistoryOrderCard(
                initials = "AG",
                orderNum = "#5801",
                customer = "Agustina López",
                details = "Retirado · hoy 12:06 · $18.900",
                status = "Retirado",
                statusColor = QLessStatusColors.disponible,
                statusBg = QLessStatusColors.disponibleSurface
            )

            HistoryOrderCard(
                initials = "SM",
                orderNum = "#5798",
                customer = "Sofía Martínez",
                details = "Completado · hoy 11:42 · $12.500",
                status = "Completado",
                statusColor = MaterialTheme.colorScheme.primary,
                statusBg = MaterialTheme.colorScheme.primaryContainer
            )

            Spacer(Modifier.height(16.dp))
            Text(
                "Los pedidos activos siguen disponibles también en la pantalla operativa.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HistoryOrderCard(
    initials: String,
    orderNum: String,
    customer: String,
    details: String,
    status: String,
    statusColor: Color,
    statusBg: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(initials, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
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
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(customer, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(details, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Ver resumen",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(selected: Boolean, text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun BackOfficeBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(100f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Icons.AutoMirrored.Filled.List, "Pedidos en curso", selectedTab == 0) { onTabSelected(0) }
            NavItem(Icons.Outlined.DateRange, "Historial", selectedTab == 1) { onTabSelected(1) }
            NavItem(Icons.Default.Settings, "Ajustes", selectedTab == 2) { onTabSelected(2) }
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
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
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            label,
            fontSize = 10.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview
@Composable
private fun BackOfficeHistoryPreview() {
    QLessTheme {
        BackOfficeHistoryScreen(onBack = {}, onNavigateToOrders = {}, onNavigateToAjustes = {})
    }
}
