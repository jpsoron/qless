package com.qless.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qless.domain.model.Order
import com.qless.ui.components.ActiveCartCard
import com.qless.ui.components.ActiveCartUi
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*
import com.qless.ui.viewmodel.OrderFilter
import com.qless.ui.viewmodel.OrderViewModel
import com.qless.ui.viewmodel.activeOrder
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MisPedidosScreen(
    orderViewModel: OrderViewModel,
    activeCart: ActiveCartUi? = null,
    onViewCart: () -> Unit = {},
    onNavigateToInicio: () -> Unit,
    onNavigateToMisLocales: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToAjustes: () -> Unit,
    onViewActiveOrder: () -> Unit,
    onViewOrderSummary: () -> Unit,
) {
    val state by orderViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { orderViewModel.loadUserOrders() }

    val activeOrder = state.activeOrder()
    val allFiltered = orderViewModel.filteredUserOrders()
    val filtered = if (state.userFilter == OrderFilter.ACTIVE && activeOrder != null) {
        allFiltered.filter { it.id != activeOrder.id }
    } else allFiltered

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

                if (activeCart != null) {
                    ActiveCartCard(cart = activeCart, onVer = onViewCart)
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
                        RecentOrderCard(order = order, onClick = {
                            orderViewModel.selectOrder(order)
                            onViewOrderSummary()
                        })
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
    val isReady = order.status == "ready"

    val gradientColors = if (isReady)
        listOf(Color(0xFF0D5A31), Color(0xFF218B52))
    else
        listOf(Color(0xFF5C3800), Color(0xFF9E6304))

    val accentColor = if (isReady) QLessStatusColors.disponible else QLessStatusColors.enPreparacion

    val statusLabel = when (order.status) {
        "pending"   -> "PAGO CONFIRMADO"
        "preparing" -> "EN PREPARACIÓN"
        "ready"     -> "¡TU PEDIDO ESTÁ LISTO!"
        else        -> "PEDIDO ACTIVO"
    }

    val subtitleText = when (order.status) {
        "pending"   -> "El local está por empezar tu pedido"
        "preparing" -> "La cocina está armando tu pedido"
        else        -> "Acercate al mostrador a retirar"
    }

    val icon = if (isReady) Icons.Default.NotificationsActive else Icons.Default.Schedule

    val pulse = rememberInfiniteTransition(label = "pulse")

    // Ícono: scale pronunciado cuando listo, suave cuando en preparación
    val iconScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (isReady) 1.45f else 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isReady) 700 else 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    // Dot: parpadea en alpha para señalizar estado "en vivo"
    val dotAlpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradientColors), shape = RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            // Círculo decorativo de fondo
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.07f))
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ícono del estado
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .scale(iconScale)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    // Eyebrow con dot pulsante para en preparación
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (!isReady) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = dotAlpha))
                            )
                        }
                        Text(
                            statusLabel,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(order.localNombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp)
                    Text("Pedido #${order.numero}", color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White.copy(alpha = 0.12f)
                    ) {
                        Text(
                            subtitleText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                // Botón "Ver"
                Surface(shape = RoundedCornerShape(999.dp), color = Color.White.copy(alpha = 0.18f)) {
                    Text(
                        "Ver →",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
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
