package com.qless.ui.screens.backoffice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.qless.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qless.domain.model.Order
import com.qless.ui.components.BackOfficeBottomNav
import com.qless.ui.theme.*
import com.qless.ui.viewmodel.BackOfficeFilter
import com.qless.ui.viewmodel.OrderViewModel

@Composable
fun BackOfficeScreen(
    orderViewModel: OrderViewModel,
    onNavigateToHistory: () -> Unit,
    onUpdateOrder: (orderId: String) -> Unit,
    onNavigateToAjustes: () -> Unit
) {
    val state by orderViewModel.uiState.collectAsStateWithLifecycle()

    // Tiempo real: el canal del local refresca activos e historial ante cada cambio.
    // Vive mientras la pantalla está en composición y se corta al salir.
    LaunchedEffect(Unit) { orderViewModel.observeLocalOrders() }

    // Derivado directamente del state observado para garantizar recomposición correcta
    val filtered = when (state.localFilter) {
        BackOfficeFilter.RECEIVED  -> state.localOrders.filter { it.status == "pending" }
        BackOfficeFilter.PREPARING -> state.localOrders.filter { it.status == "preparing" }
        BackOfficeFilter.READY     -> state.localOrders.filter { it.status == "ready" }
    }

    Scaffold(
        bottomBar = {
            BackOfficeBottomNav(
                selectedTab = 0,
                onTabSelected = { tab ->
                    when (tab) {
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
                .padding(bottom = padding.calculateBottomPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaderaOscura)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_qless_blanco),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(84.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "QLess",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "BackOffice",
                        style = MaterialTheme.typography.bodySmall,
                        color = Azafrán
                    )
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onNavigateToAjustes() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                if (state.localNombre.isNotEmpty()) {
                    Text(
                        buildString {
                            append(state.localNombre)
                            if (state.localEmoji.isNotEmpty()) append("  ${state.localEmoji} ")
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(16.dp))
                }
                Text(
                    "Pedidos en curso",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Gestioná los pedidos activos del local y actualizá su estado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(999.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Text(
                            "Pedidos activos",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            "${state.localOrders.size} pedidos",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BackOfficeFilterChip(
                        text = "Recibidos",
                        count = state.localOrders.count { it.status == "pending" },
                        selected = state.localFilter == BackOfficeFilter.RECEIVED
                    ) { orderViewModel.setLocalFilter(BackOfficeFilter.RECEIVED) }
                    BackOfficeFilterChip(
                        text = "En prep.",
                        count = state.localOrders.count { it.status == "preparing" },
                        selected = state.localFilter == BackOfficeFilter.PREPARING
                    ) { orderViewModel.setLocalFilter(BackOfficeFilter.PREPARING) }
                    BackOfficeFilterChip(
                        text = "Retirar",
                        count = state.localOrders.count { it.status == "ready" },
                        selected = state.localFilter == BackOfficeFilter.READY
                    ) { orderViewModel.setLocalFilter(BackOfficeFilter.READY) }
                }

                Spacer(Modifier.height(12.dp))

                if (state.isLoadingLocal) {
                    Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (state.error != null) {
                    val errorMsg = state.error ?: "Error desconocido"
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay pedidos en este estado",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Text(
                        "Tocá un pedido para ver su detalle y actualizar el estado.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(16.dp))
                    filtered.forEach { order ->
                        BackOfficeOrderCard(order = order, onClick = { onUpdateOrder(order.id) })
                    }
                }

                Spacer(Modifier.height(24.dp))

                val cntRecibidos = state.localOrders.count { it.status == "pending" }
                val cntPrep      = state.localOrders.count { it.status == "preparing" }
                val cntListos    = state.localOrders.count { it.status == "ready" }
                val totalActivos = cntRecibidos + cntPrep + cntListos

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Resumen del turno", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(8.dp))
                            SummaryRow("$cntRecibidos recibidos")
                            SummaryRow("$cntPrep en preparación")
                            SummaryRow("$cntListos listos para retirar")
                        }
                        VerticalDivider(modifier = Modifier.height(96.dp).padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.primaryContainer)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Activos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "$totalActivos",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun BackOfficeFilterChip(text: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
            if (count > 0) {
                Surface(
                    shape = CircleShape,
                    color = if (selected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "$count",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun BackOfficeOrderCard(order: Order, onClick: () -> Unit) {
    val (statusLabel, statusColor, statusBg) = when (order.status) {
        "pending" -> Triple("Pago confirmado", MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.primaryContainer)
        "preparing" -> Triple("En preparación", QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
        "ready" -> Triple("Listo para retirar", QLessStatusColors.disponible, QLessStatusColors.disponibleSurface)
        else -> Triple(order.status, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
    }
    val initials = "#${order.numero}"
    val itemCount = order.items.sumOf { it.quantity }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
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
                        Text("Pedido #${order.numero}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Text("$itemCount ${if (itemCount == 1) "ítem" else "ítems"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(shape = RoundedCornerShape(999.dp), color = statusBg) {
                        Text(statusLabel, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(order.createdAt.take(16).replace("T", " · "), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Actualizar", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 1.dp))
}

@Preview
@Composable
private fun BackOfficePreview() {
    QLessTheme {
        // Preview requires OrderViewModel — skipped
    }
}
