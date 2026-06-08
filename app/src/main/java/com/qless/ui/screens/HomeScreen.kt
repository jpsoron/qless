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
import com.qless.data.Local
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*
import com.qless.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userName: String,
    isDarkTheme: Boolean = false,
    onNavigateToMisLocales: () -> Unit,
    onLocalSelected: (localId: String) -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToAjustes: () -> Unit,
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
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
        containerColor = MaterialTheme.colorScheme.background
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
                    .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_qless_blanco),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(66.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "QLess",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        "Tu comida, sin filas.",
                        style = MaterialTheme.typography.labelMedium,
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
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initial, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Buenos días 👋", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            userName.ifBlank { "Usuario" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Buscador
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Buscar locales o productos...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Banner pedido en curso
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToTracking() },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDarkTheme) Albahaca else Color(0xFFE8F5EE),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isDarkTheme) Albahaca else Color(0xFFB8DEC8)
                    )
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
                                .background(if (isDarkTheme) Color.Black else QLessStatusColors.disponible),
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
                                color = if (isDarkTheme) Color.White else QLessStatusColors.disponible,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                "Big Pons · #4521",
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "En preparación · ~12 min",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDarkTheme) Color.White.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "Ver →",
                            color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onNavigateToMisLocales, contentPadding = PaddingValues(0.dp)) {
                        Text("Ver todos", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))

                when {
                    homeUiState.isLoading -> {
                        repeat(2) {
                            FavoritoSkeletonCard()
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                    homeUiState.favoritos.isEmpty() -> {
                        Text(
                            "Aún no tenés favoritos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    else -> {
                        homeUiState.favoritos.forEach { local ->
                            RestaurantCard(local = local, onClick = { onLocalSelected(local.id) })
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RestaurantCard(local: Local, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(local.emoji, fontSize = 28.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        local.nombre,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (local.abierto) QLessStatusColors.disponibleSurface else MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            if (local.abierto) "Abierto" else "Cerrado",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (local.abierto) QLessStatusColors.disponible else MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(local.categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = QLessStatusColors.enPreparacion, modifier = Modifier.size(12.dp))
                        Text(local.rating, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                        Text(local.barrio, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (local.tienePromo) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                "10% OFF",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritoSkeletonCard() {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(animation = tween(1200, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "shimmerTranslate"
    )
    val brush = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset(translateAnim - 400f, 0f),
        end = androidx.compose.ui.geometry.Offset(translateAnim, 0f)
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(brush))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(Modifier.fillMaxWidth(0.45f).height(14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                    Box(Modifier.size(55.dp, 18.dp).clip(RoundedCornerShape(999.dp)).background(brush))
                }
                Box(Modifier.fillMaxWidth(0.65f).height(10.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(40.dp, 18.dp).clip(RoundedCornerShape(999.dp)).background(brush))
                    Box(Modifier.size(60.dp, 18.dp).clip(RoundedCornerShape(999.dp)).background(brush))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("ViewModelConstructorInComposable") // Solo preview; VM construido a mano a propósito.
private fun HomePreview() {
    QLessTheme { HomeScreen(homeViewModel = HomeViewModel(), userName = "María González", onNavigateToMisLocales = {}, onLocalSelected = { _ -> }, onNavigateToTracking = {}, onNavigateToMisPedidos = {}, onNavigateToScanQr = {}, onNavigateToAjustes = {}) }
}
