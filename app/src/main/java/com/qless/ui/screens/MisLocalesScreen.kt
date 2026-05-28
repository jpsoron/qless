package com.qless.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*
import kotlinx.coroutines.delay

private data class LocalItem(
    val emoji: String,
    val name: String,
    val category: String,
    val location: String,
    val rating: String,
    val time: String?,
    val isOpen: Boolean,
    val hasPromo: Boolean = false,
    val isFeatured: Boolean = false,
)

private val locales = listOf(
    LocalItem("🍔", "Big Pons", "Hamburguesas & Snacks", "San Isidro", "4.8", "15-25 min", true, hasPromo = true, isFeatured = true),
    LocalItem("🍱", "Sushi Nori", "Japonesa · Rolls & Pokés", "Palermo", "4.9", "20-30 min", true),
    LocalItem("🍕", "Pizza Mía", "Italiana · Pastas y Pizzas", "Recoleta", "4.7", "20-35 min", true),
    LocalItem("🥗", "Green Bowl", "Saludable · Bowls", "Núñez", "4.6", null, false),
)

@Composable
fun MisLocalesScreen(
    onLocalSelected: () -> Unit,
    onBack: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToLocationDetected: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
    onNavigateToAjustes: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(1500L); isLoading = false }
    val shimmerBrush = shimmerBrush()

    Scaffold(
        bottomBar = {
            QLessBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onNavigateToInicio()
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
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(16.dp).statusBarsPadding())

                Text(
                    "Mis Locales",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Espresso
                )
                Text(
                    "Elegí en qué local querés pedir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Madera
                )

                Spacer(Modifier.height(14.dp))

                // Geo banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5EE),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFB8DEC8))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Albahaca),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "¿Estás en Big Pons - San Isidro?",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Espresso
                            )
                            Text(
                                "Av. del Libertador 1420, San Isidro",
                                style = MaterialTheme.typography.labelSmall,
                                color = Madera
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = onNavigateToLocationDetected,
                            colors = ButtonDefaults.buttonColors(containerColor = Pimentón),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Sí", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.width(6.dp))
                        OutlinedButton(
                            onClick = {},
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón)
                        ) {
                            Text("No", fontSize = 12.sp, color = Madera)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Buscador
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Mantequilla,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Madera.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Buscar local...", color = Madera.copy(alpha = 0.5f))
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .size(140.dp, 12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush)
                    )
                } else {
                    Text(
                        "${locales.count { it.isOpen }} LOCALES DISPONIBLES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Madera.copy(alpha = 0.6f),
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                if (isLoading) {
                    repeat(4) {
                        SkeletonLocalCard(shimmerBrush)
                        Spacer(Modifier.height(10.dp))
                    }
                } else {
                    locales.forEach { local ->
                        LocalCard(local = local, onClick = onLocalSelected)
                        Spacer(Modifier.height(10.dp))
                    }
                }

                // QR CTA
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToScanQr() },
                    shape = RoundedCornerShape(16.dp),
                    color = Espresso
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Escanear código QR del local", fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text("Apuntá al QR en la mesa o caja", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        Melocotón.copy(alpha = 0.9f),
        Mantequilla,
        Melocotón.copy(alpha = 0.9f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 400f, 0f),
        end = Offset(translateAnim, 0f)
    )
}

@Composable
private fun SkeletonLocalCard(brush: Brush) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Mantequilla,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.fillMaxWidth(0.45f).height(14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                    Box(Modifier.size(55.dp, 18.dp).clip(RoundedCornerShape(999.dp)).background(brush))
                }
                Box(Modifier.fillMaxWidth(0.7f).height(10.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(40.dp, 18.dp).clip(RoundedCornerShape(999.dp)).background(brush))
                    Box(Modifier.size(60.dp, 18.dp).clip(RoundedCornerShape(999.dp)).background(brush))
                    Box(Modifier.size(55.dp, 18.dp).clip(RoundedCornerShape(999.dp)).background(brush))
                }
            }
        }
    }
}

@Composable
private fun LocalCard(local: LocalItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = local.isOpen) { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (local.isFeatured) Color(0xFFFFF5F0) else Mantequilla,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (local.isFeatured) Tomate else Melocotón
        )
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Melocotón),
                contentAlignment = Alignment.Center
            ) {
                Text(local.emoji, fontSize = 28.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(local.name, fontWeight = FontWeight.SemiBold, color = Espresso)
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (local.isOpen) AlbahacaClaro else BorgoñaClaro
                    ) {
                        Text(
                            if (local.isOpen) "Abierto" else "Cerrado",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (local.isOpen) Albahaca else Borgoña
                        )
                    }
                }
                Text(local.category, style = MaterialTheme.typography.bodySmall, color = Madera)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Azafrán, modifier = Modifier.size(12.dp))
                        Text(local.rating, style = MaterialTheme.typography.labelSmall, color = Madera, fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Madera, modifier = Modifier.size(12.dp))
                        Text(local.location, style = MaterialTheme.typography.labelSmall, color = Madera)
                    }
                    if (local.time != null) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = AzafránClaro
                        ) {
                            Text(local.time, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Azafrán, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    if (local.hasPromo) {
                        Surface(shape = RoundedCornerShape(999.dp), color = Melocotón) {
                            Text("10% OFF", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Pimentón, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MisLocalesPreview() {
    QLessTheme { MisLocalesScreen(onLocalSelected = {}, onBack = {}, onNavigateToInicio = {}, onNavigateToLocationDetected = {}, onNavigateToScanQr = {}, onNavigateToMisPedidos = {}, onNavigateToAjustes = {}) }
}
