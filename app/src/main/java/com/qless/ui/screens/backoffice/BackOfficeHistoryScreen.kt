package com.qless.ui.screens.backoffice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qless.domain.model.Order
import com.qless.ui.components.BackOfficeBottomNav
import com.qless.ui.theme.*
import com.qless.ui.viewmodel.ACTIVE_ORDER_STATUSES
import com.qless.ui.viewmodel.OrderViewModel

@Composable
fun BackOfficeHistoryScreen(
    orderViewModel: OrderViewModel,
    onBack: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAjustes: () -> Unit,
) {
    val state by orderViewModel.uiState.collectAsStateWithLifecycle()

    // Tiempo real: el mismo canal del local mantiene al día activos e historial.
    LaunchedEffect(Unit) { orderViewModel.observeLocalOrders() }

    val completedOrders = state.historyOrders
    val activeCount     = state.localOrders.count { it.status in ACTIVE_ORDER_STATUSES }
    val totalCount      = state.localOrders.size + completedOrders.size

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
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    "Historial de pedidos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(16.dp))

            // Stats
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HistoryStatItem(
                        value = "$totalCount",
                        label = "total",
                        color = MaterialTheme.colorScheme.primary
                    )
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    HistoryStatItem(
                        value = "${completedOrders.size}",
                        label = "entregados",
                        color = QLessStatusColors.disponible
                    )
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    HistoryStatItem(
                        value = "$activeCount",
                        label = "activos",
                        color = QLessStatusColors.enPreparacion
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (state.isLoadingHistory) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (completedOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Todavía no hay pedidos entregados",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    "${completedOrders.size} pedidos entregados",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(12.dp))
                completedOrders.forEach { order ->
                    HistoryOrderCard(order = order)
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HistoryStatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 28.sp, fontWeight = FontWeight.SemiBold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HistoryOrderCard(order: Order) {
    val itemCount = order.items.sumOf { it.quantity }
    val formattedTotal = "$${"%,d".format(order.totalAmount)}"
    val formattedDate = order.createdAt.take(16).replace("T", " · ")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = QLessStatusColors.disponibleSurface
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "#${order.numero}",
                        color = QLessStatusColors.disponible,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        "Pedido #${order.numero}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = QLessStatusColors.disponibleSurface
                    ) {
                        Text(
                            "Entregado",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = QLessStatusColors.disponible,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$itemCount ${if (itemCount == 1) "ítem" else "ítems"} · $formattedDate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        formattedTotal,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun BackOfficeHistoryPreview() {
    QLessTheme {
        // Preview requires OrderViewModel — skipped
    }
}
