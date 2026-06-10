package com.qless.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qless.data.Order
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*
import com.qless.ui.viewmodel.OrderFilter
import com.qless.ui.viewmodel.OrderViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MisPedidosScreen(
    orderViewModel: OrderViewModel,
    onNavigateToInicio: () -> Unit,
    onNavigateToMisLocales: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToAjustes: () -> Unit,
    onViewActiveOrder: () -> Unit,
    onViewOrderSummary: () -> Unit,
) {
    val state by orderViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { orderViewModel.loadUserOrders() }

    val filtered = orderViewModel.filteredUserOrders()
    val activeOrder = state.userOrders.firstOrNull { it.status in setOf("pending", "preparing", "ready") }

    Scaffold(
        bottomBar = {
            QLessBottomNav(
                selectedTab = 2,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onNavigateToInicio()
                        1 -> onNavigateToMisLocales()
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp).statusBarsPadding())

            Text(
                "Mis Pedidos",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Seguí tus pedidos y revisá el historial",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(18.dp))

            OrderFilterTabs(
                selected = state.userFilter,
                onSelect = { orderViewModel.setUserFilter(it) }
            )

            Spacer(Modifier.height(24.dp))

            if (state.isLoadingUser) {
                Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (activeOrder != null && state.userFilter == OrderFilter.ACTIVE) {
                    ActiveOrderCard(order = activeOrder, onClick = onViewActiveOrder)
                    Spacer(Modifier.height(28.dp))
                }

                if (filtered.isNotEmpty()) {
                    Text(
                        "RECIENTES",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    filtered.forEach { order ->
                        RecentOrderCard(order = order, onClick = onViewOrderSummary)
                        Spacer(Modifier.height(12.dp))
                    }
                } else if (!state.isLoadingUser) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            when (state.userFilter) {
                                OrderFilter.ACTIVE -> "No tenés pedidos activos"
                                OrderFilter.COMPLETED -> "No tenés pedidos finalizados"
                                OrderFilter.CANCELLED -> "No tenés pedidos cancelados"
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun OrderFilterTabs(selected: OrderFilter, onSelect: (OrderFilter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FilterChip("Activos", selected == OrderFilter.ACTIVE) { onSelect(OrderFilter.ACTIVE) }
        FilterChip("Finalizados", selected == OrderFilter.COMPLETED) { onSelect(OrderFilter.COMPLETED) }
        FilterChip("Cancelados", selected == OrderFilter.CANCELLED) { onSelect(OrderFilter.CANCELLED) }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ActiveOrderCard(order: Order, onClick: () -> Unit) {
    val statusLabel = when (order.status) {
        "pending" -> "PAGO CONFIRMADO"
        "preparing" -> "EN PREPARACIÓN"
        "ready" -> "¡TU PEDIDO ESTÁ LISTO!"
        else -> "PEDIDO ACTIVO"
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = QLessStatusColors.disponible
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF0D5A31), Color(0xFF218B52))))
                .padding(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(statusLabel, color = Color.White.copy(alpha = 0.58f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, letterSpacing = 1.1.sp)
                    Text(order.localNombre, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
                    Text("Pedido #${order.numero}", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        when (order.status) {
                            "pending"   -> "El local está por empezar tu pedido"
                            "preparing" -> "La cocina está armando tu pedido"
                            else        -> "Acercate al mostrador a retirar"
                        },
                        color = Color.White.copy(alpha = 0.72f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Surface(shape = RoundedCornerShape(999.dp), color = Color.White) {
                    Text("Ver →", modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp), color = QLessStatusColors.disponible, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun RecentOrderCard(order: Order, onClick: () -> Unit) {
    val formatted = NumberFormat.getNumberInstance(Locale("es", "AR")).format(order.totalAmount)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.4.dp, Color(0xFFEAD5C5))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(order.localEmoji.ifEmpty { "🛍️" }, fontSize = 28.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pedido #${order.numero}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.weight(1f))
                    StatusBadge(order.status)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(order.localNombre, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(order.createdAt.take(10), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f), style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Text("Ver resumen →", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Text("$$formatted", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (label, color, bg) = when (status) {
        "pending" -> Triple("Confirmado", QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
        "preparing" -> Triple("En preparación", QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
        "ready" -> Triple("Listo", QLessStatusColors.disponible, QLessStatusColors.disponibleSurface)
        "picked_up" -> Triple("Entregado", QLessStatusColors.disponible, QLessStatusColors.disponibleSurface)
        "cancelled" -> Triple("Cancelado", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
        else -> Triple(status, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
    }
    Surface(shape = RoundedCornerShape(999.dp), color = bg) {
        Text(label, modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), color = color, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
    }
}

@Preview(showBackground = true)
@Composable
private fun MisPedidosPreview() {
    QLessTheme {
        // Preview requires OrderViewModel — skipped
    }
}
