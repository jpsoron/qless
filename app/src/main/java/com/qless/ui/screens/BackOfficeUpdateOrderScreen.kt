package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

@Composable
fun BackOfficeUpdateOrderScreen(
    onBack: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BackOfficeBottomNav(
                selectedTab = 1,
                onTabSelected = { tab ->
                    if (tab == 1) onNavigateToOrders()
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp).statusBarsPadding())

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Pimentón)
                }
                Text(
                    "Actualizar pedido",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Espresso
                )
            }
            
            Text(
                "Pedido #5921 · Big Pons",
                modifier = Modifier.padding(start = 48.dp),
                color = Madera,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = AzafránClaro
                ) {
                    Text(
                        "En preparación",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Azafrán,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    "Retiro estimado: 12:35",
                    style = MaterialTheme.typography.labelSmall,
                    color = Madera
                )
            }

            Spacer(Modifier.height(16.dp))

            // Customer Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
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
                            Text("CR", color = Pimentón, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Camila Ruiz", fontWeight = FontWeight.SemiBold, color = Espresso)
                        Text("3 items · pago aprobado", style = MaterialTheme.typography.bodySmall, color = Madera)
                    }
                    VerticalDivider(modifier = Modifier.height(40.dp).padding(horizontal = 8.dp), color = Melocotón)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Punto de retiro", fontSize = 10.sp, color = Madera)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AzafránClaro.copy(alpha = 0.5f)
                        ) {
                            Text(
                                "Caja 1",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Pimentón,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                        Text("Retiro por mostrador", fontSize = 8.sp, color = Madera.copy(alpha = 0.6f))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Items del pedido", fontWeight = FontWeight.SemiBold, color = Espresso)
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Mantequilla.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("2x Burger clásica", fontWeight = FontWeight.SemiBold, color = Espresso)
                        Text("1x Papas medianas", fontWeight = FontWeight.SemiBold, color = Espresso)
                        Spacer(Modifier.height(8.dp))
                        Text("Sin cebolla · extra cheddar", style = MaterialTheme.typography.bodySmall, color = Madera)
                    }
                    VerticalDivider(modifier = Modifier.height(60.dp).padding(horizontal = 16.dp), color = Melocotón)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Preparación\nestimada\n12 min", fontSize = 10.sp, color = Madera, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Estado del pedido", fontWeight = FontWeight.SemiBold, color = Espresso)
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    StatusStep(
                        title = "Pedido recibido",
                        subtitle = "Pago confirmado y orden emitida al local",
                        status = StatusType.DONE
                    )
                    StatusStep(
                        title = "En preparación",
                        subtitle = "La cocina ya está armando el pedido",
                        status = StatusType.ACTIVE
                    )
                    StatusStep(
                        title = "Listo para retirar",
                        subtitle = "La app avisa al cliente para que se acerque",
                        status = StatusType.PENDING
                    )
                    StatusStep(
                        title = "Retirado",
                        subtitle = "Se cierra la orden cuando el cliente lo recibe",
                        status = StatusType.PENDING,
                        isLast = true
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Pimentón)
            ) {
                Text("Marcar listo para retirar", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón)
            ) {
                Text("Marcar como retirado", color = Pimentón, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

private enum class StatusType { DONE, ACTIVE, PENDING }

@Composable
private fun StatusStep(
    title: String,
    subtitle: String,
    status: StatusType,
    isLast: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        when (status) {
                            StatusType.DONE -> Albahaca
                            StatusType.ACTIVE -> Azafrán
                            StatusType.PENDING -> Melocotón
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (status == StatusType.DONE) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                } else if (status == StatusType.ACTIVE) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(
                            if (status == StatusType.DONE) Albahaca else Melocotón
                        )
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, color = Espresso)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Madera.copy(alpha = 0.6f))
            Spacer(Modifier.height(20.dp))
        }
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
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview
@Composable
private fun BackOfficeUpdateOrderPreview() {
    QLessTheme {
        BackOfficeUpdateOrderScreen(onBack = {}, onNavigateToOrders = {}, onNavigateToHistory = {})
    }
}
