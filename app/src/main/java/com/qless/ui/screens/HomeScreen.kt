package com.qless.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.R
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*

private data class RestaurantItem(
    val emoji: String,
    val name: String,
    val category: String,
    val location: String,
    val rating: String,
    val isOpen: Boolean,
    val hasPromo: Boolean = false,
)

private val featured = listOf(
    RestaurantItem("🍔", "Big Pons", "Hamburguesas & Snacks", "San Isidro", "4.8", true, hasPromo = true),
    RestaurantItem("🍱", "Sushi Nori", "Japonesa · Rolls & Pokés", "Palermo", "4.9", true),
    RestaurantItem("🥗", "Green Bowl", "Saludable · Bowls", "Núñez", "4.6", false),
)

@Composable
fun HomeScreen(
    userName: String,
    onNavigateToMisLocales: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToAjustes: () -> Unit,
) {
    val initial = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    var selectedTab by remember { mutableIntStateOf(0) }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Scaffold(
        bottomBar = {
            QLessBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    when (tab) {
                        1 -> onNavigateToMisLocales()
                        2 -> onNavigateToMisPedidos()
                        3 -> onNavigateToAjustes()
                        4 -> onNavigateToScanQr()
                        else -> selectedTab = tab
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
        ) {
            // Top bar con logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Pimentón)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_qless_blanco),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(76.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "QLess",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Text(
                        "Tu comida, sin filas.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(20.dp))

                // Saludo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Pimentón),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initial, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Buenos días 👋", style = MaterialTheme.typography.bodySmall, color = Madera)
                        Text(
                            userName.ifBlank { "Usuario" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Espresso
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Buscador
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Mantequilla,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Madera.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Buscar locales o productos...", color = Madera.copy(alpha = 0.6f))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Banner pedido en curso
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToTracking() },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE8F5EE),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFB8DEC8))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(Albahaca),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "PEDIDO EN CURSO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Albahaca,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                "Big Pons · #4521",
                                fontWeight = FontWeight.SemiBold,
                                color = Espresso
                            )
                            Text(
                                "En preparación · ~12 min",
                                style = MaterialTheme.typography.bodySmall,
                                color = Madera
                            )
                        }
                        Text("Ver →", color = Pimentón, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Sección favoritos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tus favoritos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Espresso
                    )
                    TextButton(onClick = onNavigateToMisLocales, contentPadding = PaddingValues(0.dp)) {
                        Text("Ver todos", color = Pimentón, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))

                featured.forEach { resto ->
                    RestaurantCard(resto = resto, onClick = onNavigateToMisLocales)
                    Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RestaurantCard(resto: RestaurantItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Mantequilla,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Melocotón),
                contentAlignment = Alignment.Center
            ) {
                Text(resto.emoji, fontSize = 28.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        resto.name,
                        fontWeight = FontWeight.SemiBold,
                        color = Espresso,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (resto.isOpen) AlbahacaClaro else BorgoñaClaro
                    ) {
                        Text(
                            if (resto.isOpen) "Abierto" else "Cerrado",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (resto.isOpen) Albahaca else Borgoña
                        )
                    }
                }
                Text(resto.category, style = MaterialTheme.typography.bodySmall, color = Madera)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Azafrán, modifier = Modifier.size(12.dp))
                        Text(resto.rating, style = MaterialTheme.typography.bodySmall, color = Madera, fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Madera, modifier = Modifier.size(12.dp))
                        Text(resto.location, style = MaterialTheme.typography.bodySmall, color = Madera)
                    }
                    if (resto.hasPromo) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Melocotón
                        ) {
                            Text(
                                "10% OFF",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Pimentón
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    QLessTheme { HomeScreen(userName = "María González", onNavigateToMisLocales = {}, onNavigateToTracking = {}, onNavigateToMisPedidos = {}, onNavigateToScanQr = {}, onNavigateToAjustes = {}) }
}
