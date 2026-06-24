package com.qless.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.*
import android.annotation.SuppressLint
import com.qless.R
import com.qless.domain.model.Local
import com.qless.domain.model.Order
import com.qless.domain.usecase.NEARBY_THRESHOLD_METERS
import com.qless.ui.components.ActiveCartCard
import com.qless.ui.components.ActiveCartUi
import com.qless.ui.components.OfflineBanner
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*
import com.qless.ui.viewmodel.HomeViewModel

private data class BannerConfig(
    val eyebrow: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val bg: Color,
    val border: Color,
)

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    userName: String,
    activeOrder: Order? = null,
    activeCart: ActiveCartUi? = null,
    onViewCart: () -> Unit = {},
    isDarkTheme: Boolean = false,
    firstOrderDiscount: Boolean = false,
    unreadNotifications: Int = 0,
    onNavigateToNotifications: () -> Unit = {},
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

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Con permiso, obtiene la ubicación y calcula el local más cercano.
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            homeViewModel.refreshNearestLocal()
        }
    }

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
                .padding(bottom = padding.calculateBottomPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar — el fondo cubre la status bar gracias a statusBarsPadding()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Pimentón)
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
                        "Tu comida, sin filas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.70f)
                    )
                }
                Spacer(Modifier.weight(1f))
                Box(contentAlignment = Alignment.TopEnd) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onNavigateToNotifications() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    if (unreadNotifications > 0) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Pimentón),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (unreadNotifications > 9) "9+" else unreadNotifications.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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

                // Badge de descuento de bienvenida: solo si todavía no lo canjeó.
                if (firstOrderDiscount) {
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = AlbahacaClaro,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Albahaca.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🎉", fontSize = 13.sp)
                            Text(
                                "¡Tenés un 10% de descuento en tu primer pedido!",
                                color = Albahaca,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Buscador — abre la lista de locales (con su búsqueda funcional).
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToMisLocales() },
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

                if (homeUiState.isOffline) {
                    Spacer(Modifier.height(16.dp))
                    OfflineBanner()
                }

                Spacer(Modifier.height(16.dp))

                // Location detection prompt
                if (!locationPermissionState.status.isGranted) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
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
                                    .clip(CircleShape)
                                    .background(Albahaca),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Detectar cercanía", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Espresso)
                                Text("Encontrá el local más cercano a vos", style = MaterialTheme.typography.labelSmall, color = Madera)
                            }
                            Button(
                                onClick = { locationPermissionState.launchPermissionRequest() },
                                colors = ButtonDefaults.buttonColors(containerColor = Albahaca),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Activar", fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Estás acá: solo si el más cercano está dentro de 50 m y no hay un
                // pedido activo en curso (con pedido activo el foco es el seguimiento).
                // Aparece con fade + expand para no ser un salto abrupto: la ubicación
                // se resuelve después de cargar el resto del Home.
                // Con carrito activo el foco es retomar ese pedido, no "¿estás acá?":
                // se prioriza la ActiveCartCard (más abajo) y se oculta este bloque.
                val closest = homeUiState.closestLocal
                val showAtLocal = closest != null &&
                    activeOrder == null &&
                    activeCart == null &&
                    locationPermissionState.status.isGranted &&
                    (closest.distanciaMetros?.let { it <= NEARBY_THRESHOLD_METERS } == true)
                // Conserva el último local para que la animación de salida no se quede
                // sin contenido si closestLocal vuelve a null mientras encoge.
                var lastAtLocal by remember { mutableStateOf<Local?>(null) }
                if (showAtLocal) lastAtLocal = closest
                AnimatedVisibility(
                    visible = showAtLocal,
                    enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(200)),
                ) {
                    Column {
                        Text(
                            "¿ESTÁS ACÁ?",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Pimentón,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        lastAtLocal?.let { local ->
                            RestaurantCard(local = local, onClick = { onLocalSelected(local.id) })
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }

                // Banner pedido en curso — solo visible cuando hay un pedido activo
                if (activeOrder != null) {
                    val bannerConfig = when (activeOrder.status) {
                        "pending" -> BannerConfig(
                            eyebrow   = "PEDIDO RECIBIDO",
                            subtitle  = "Pago confirmado, esperando al local",
                            icon      = Icons.Default.CheckCircle,
                            color     = MaterialTheme.colorScheme.primary,
                            bg        = if (isDarkTheme) Color(0xFF1A2340) else Color(0xFFEEF2FF),
                            border    = if (isDarkTheme) Color(0xFF3A4F8A) else Color(0xFFBFCCF8),
                        )
                        "preparing" -> BannerConfig(
                            eyebrow   = "EN PREPARACIÓN",
                            subtitle  = "La cocina está armando tu pedido",
                            icon      = Icons.Default.Schedule,
                            color     = QLessStatusColors.enPreparacion,
                            bg        = if (isDarkTheme) Albahaca else Color(0xFFFFF8E1),
                            border    = if (isDarkTheme) Albahaca else Color(0xFFFFE082),
                        )
                        "ready" -> BannerConfig(
                            eyebrow   = "¡LISTO PARA RETIRAR!",
                            subtitle  = "Acercate al mostrador con tu código",
                            icon      = Icons.Default.Notifications,
                            color     = QLessStatusColors.disponible,
                            bg        = if (isDarkTheme) Albahaca else Color(0xFFE8F5EE),
                            border    = if (isDarkTheme) Albahaca else Color(0xFF9BCFB0),
                        )
                        else -> BannerConfig(
                            eyebrow   = "PEDIDO EN CURSO",
                            subtitle  = "Estado desconocido",
                            icon      = Icons.Default.Schedule,
                            color     = MaterialTheme.colorScheme.primary,
                            bg        = MaterialTheme.colorScheme.surfaceVariant,
                            border    = MaterialTheme.colorScheme.primaryContainer,
                        )
                    }

                    // Solo pulsa el ícono cuando está listo para retirar
                    val iconScale = if (activeOrder.status == "ready") pulseScale else 1f

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToTracking() },
                        shape = RoundedCornerShape(16.dp),
                        color = bannerConfig.bg,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, bannerConfig.border)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .scale(iconScale)
                                    .clip(CircleShape)
                                    .background(bannerConfig.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = bannerConfig.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    bannerConfig.eyebrow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = bannerConfig.color,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    "${activeOrder.localNombre} · #${activeOrder.numero}",
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    bannerConfig.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDarkTheme) Color.White.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "Ver →",
                                color = bannerConfig.color,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }

                // Carrito activo — debajo del pedido en curso si lo hay
                if (activeCart != null) {
                    ActiveCartCard(cart = activeCart, onVer = onViewCart)
                    Spacer(Modifier.height(24.dp))
                }

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

                // Swap skeleton → favoritos con crossfade para evitar el corte seco.
                Crossfade(targetState = homeUiState.isLoading, label = "favoritos") { loading ->
                    Column {
                        when {
                            loading -> {
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
                    local.distanciaMetros?.let { distance ->
                        val distanceText = if (distance < 1000) {
                            "${distance.toInt()}m"
                        } else {
                            "%.1f km".format(distance / 1000)
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = AlbahacaClaro
                        ) {
                            Text(
                                distanceText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Albahaca,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
