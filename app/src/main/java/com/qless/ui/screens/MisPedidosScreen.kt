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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*

private data class RecentOrder(
    val emoji: String,
    val number: String,
    val local: String,
    val date: String,
    val amount: String,
)

private val recentOrders = listOf(
    RecentOrder("🍕", "#4462", "Pizza Mía · Recoleta", "Ayer · 21:10", "$6.200"),
    RecentOrder("🍔", "#4415", "Big Pons · San Isidro", "Lun 12/05 · 13:40", "$9.000"),
)

@Composable
fun MisPedidosScreen(
    onNavigateToInicio: () -> Unit,
    onNavigateToMisLocales: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToAjustes: () -> Unit,
    onViewActiveOrder: () -> Unit,
    onViewOrderSummary: () -> Unit,
) {
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
        containerColor = CremaCálida
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
                color = Espresso,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Seguí tus pedidos y revisá el historial",
                style = MaterialTheme.typography.bodyMedium,
                color = Madera
            )

            Spacer(Modifier.height(18.dp))

            OrderFilterTabs()

            Spacer(Modifier.height(24.dp))

            ActiveOrderCard(onClick = onViewActiveOrder)

            Spacer(Modifier.height(28.dp))

            Text(
                "RECIENTES",
                style = MaterialTheme.typography.labelMedium,
                color = Madera.copy(alpha = 0.62f),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            )

            Spacer(Modifier.height(12.dp))

            recentOrders.forEach { order ->
                RecentOrderCard(order = order, onClick = onViewOrderSummary)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun OrderFilterTabs() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FilterChip(text = "Activos", selected = true)
        FilterChip(text = "Finalizados", selected = false)
        FilterChip(text = "Cancelados", selected = false)
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) Espresso else Mantequilla,
        border = if (selected) null else BorderStroke(1.dp, Melocotón)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
            color = if (selected) Color.White else Madera,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ActiveOrderCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = Albahaca
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF0D5A31), Color(0xFF218B52))
                    )
                )
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
                    Text(
                        "¡TU PEDIDO ESTÁ LISTO!",
                        color = Color.White.copy(alpha = 0.58f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.1.sp
                    )
                    Text("Sushi Nori ·", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
                    Text("#4498", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("Acercate al mostrador a\nretirar", color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.bodyMedium)
                }
                Surface(shape = RoundedCornerShape(999.dp), color = Color.White) {
                    Text(
                        "Ver →",
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                        color = Albahaca,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentOrderCard(order: RecentOrder, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = Mantequilla,
        border = BorderStroke(1.4.dp, Color(0xFFEAD5C5))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Melocotón),
                contentAlignment = Alignment.Center
            ) {
                Text(order.emoji, fontSize = 28.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pedido ${order.number}", color = Espresso, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.weight(1f))
                    DeliveredBadge()
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(order.local, color = Madera, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(order.date, color = Madera.copy(alpha = 0.65f), style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Text("Ver resumen →", color = Pimentón, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Text(order.amount, color = Espresso, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun DeliveredBadge() {
    Surface(shape = RoundedCornerShape(999.dp), color = AlbahacaClaro) {
        Text(
            "Entregado",
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            color = Albahaca,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MisPedidosPreview() {
    QLessTheme {
        MisPedidosScreen({}, {}, {}, {}, {}, {})
    }
}
