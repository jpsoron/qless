package com.qless.ui.screens.backoffice

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qless.data.Order
import com.qless.ui.components.BackOfficeBottomNav
import com.qless.ui.theme.*
import com.qless.ui.viewmodel.OrderViewModel

@Composable
fun BackOfficeUpdateOrderScreen(
    orderId: String,
    orderViewModel: OrderViewModel,
    onBack: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAjustes: () -> Unit
) {
    val state by orderViewModel.uiState.collectAsStateWithLifecycle()
    val order = state.localOrders.firstOrNull { it.id == orderId }

    Scaffold(
        bottomBar = {
            BackOfficeBottomNav(
                selectedTab = 0,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onNavigateToOrders()
                        1 -> onNavigateToHistory()
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                }
                Text("Actualizar pedido", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }

            if (order == null) {
                Box(Modifier.fillMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                OrderDetail(
                    order = order,
                    onMarkPreparing = {
                        orderViewModel.updateOrderStatus(order.id, "preparing")
                        onNavigateToOrders()
                    },
                    onMarkReady = {
                        orderViewModel.updateOrderStatus(order.id, "ready")
                        onNavigateToOrders()
                    },
                    onMarkPickedUp = {
                        orderViewModel.updateOrderStatus(order.id, "picked_up")
                        onNavigateToOrders()
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun OrderDetail(
    order: Order,
    onMarkPreparing: () -> Unit,
    onMarkReady: () -> Unit,
    onMarkPickedUp: () -> Unit,
) {
    val (statusLabel, statusColor, statusBg) = when (order.status) {
        "pending" -> Triple("Pago confirmado", MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.primaryContainer)
        "preparing" -> Triple("En preparación", QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
        "ready" -> Triple("Listo para retirar", QLessStatusColors.disponible, QLessStatusColors.disponibleSurface)
        "picked_up" -> Triple("Retirado", QLessStatusColors.disponible, QLessStatusColors.disponibleSurface)
        else -> Triple(order.status, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
    }

    Text(
        "Pedido #${order.numero} · ${order.localNombre}",
        modifier = Modifier.padding(start = 48.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(Modifier.height(16.dp))

    Surface(shape = RoundedCornerShape(999.dp), color = statusBg) {
        Text(statusLabel, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.SemiBold)
    }

    Spacer(Modifier.height(16.dp))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    Text("#${order.numero}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Pedido #${order.numero}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "${order.items.sumOf { it.quantity }} ítems · $${"%,d".format(order.totalAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            VerticalDivider(modifier = Modifier.height(40.dp).padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.primaryContainer)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Retiro por", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Surface(shape = RoundedCornerShape(8.dp), color = QLessStatusColors.enPreparacionSurface.copy(alpha = 0.5f)) {
                    Text("Mostrador", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    Text("Items del pedido", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(8.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            order.items.forEach { item ->
                Text("${item.quantity}x ${item.nombre}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    Text("Estado del pedido", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(12.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            StatusStep("Pedido recibido", "Pago confirmado y orden emitida al local", if (order.status != "pending") StatusType.DONE else StatusType.ACTIVE)
            StatusStep("En preparación", "La cocina ya está armando el pedido", when (order.status) {
                "pending" -> StatusType.PENDING
                "preparing" -> StatusType.ACTIVE
                else -> StatusType.DONE
            })
            StatusStep("Listo para retirar", "La app avisa al cliente para que se acerque", when (order.status) {
                "ready" -> StatusType.ACTIVE
                "picked_up" -> StatusType.DONE
                else -> StatusType.PENDING
            })
            StatusStep("Retirado", "Se cierra la orden cuando el cliente lo recibe", if (order.status == "picked_up") StatusType.DONE else StatusType.PENDING, isLast = true)
        }
    }

    Spacer(Modifier.height(32.dp))

    // Botón según el estado actual: un solo paso hacia adelante por vez
    when (order.status) {
        "pending" -> {
            Button(
                onClick = onMarkPreparing,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QLessStatusColors.enPreparacion)
            ) {
                Text("Poner en preparación", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            Spacer(Modifier.height(12.dp))
        }
        "preparing" -> {
            Button(
                onClick = onMarkReady,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QLessStatusColors.disponible)
            ) {
                Text("Marcar listo para retirar", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            Spacer(Modifier.height(12.dp))
        }
        "ready" -> {
            Button(
                onClick = onMarkPickedUp,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Marcar como retirado", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

private enum class StatusType { DONE, ACTIVE, PENDING }

@Composable
private fun StatusStep(title: String, subtitle: String, status: StatusType, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(20.dp).clip(CircleShape).background(
                    when (status) {
                        StatusType.DONE -> QLessStatusColors.disponible
                        StatusType.ACTIVE -> QLessStatusColors.enPreparacion
                        StatusType.PENDING -> MaterialTheme.colorScheme.primaryContainer
                    }
                ),
                contentAlignment = Alignment.Center
            ) {
                if (status == StatusType.DONE) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                else if (status == StatusType.ACTIVE) Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
            }
            if (!isLast) Box(modifier = Modifier.width(2.dp).height(40.dp).background(if (status == StatusType.DONE) QLessStatusColors.disponible else MaterialTheme.colorScheme.primaryContainer))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
private fun BackOfficeUpdateOrderPreview() {
    QLessTheme {
        // Preview requires OrderViewModel — skipped
    }
}
