package com.qless.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.google.accompanist.permissions.*
import android.annotation.SuppressLint
import com.qless.domain.model.Local
import com.qless.ui.components.ActiveCartCard
import com.qless.ui.components.ActiveCartUi
import com.qless.ui.components.OfflineBanner
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.viewmodel.MisLocalesViewModel
import com.qless.ui.theme.Albahaca
import com.qless.ui.theme.Pimentón
import com.qless.ui.theme.QLessStatusColors
import com.qless.ui.theme.QLessTheme

private enum class LocalSortOption(val label: String) {
    Distancia("Más cercano"),
    RatingDesc("Rating ↓"),
    NameAsc("Nombre A-Z"),
    OpenFirst("Abiertos primero"),
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MisLocalesScreen(
    misLocalesViewModel: MisLocalesViewModel,
    isDarkTheme: Boolean = false,
    activeCart: ActiveCartUi? = null,
    onViewCart: () -> Unit = {},
    onLocalSelected: (localId: String) -> Unit,
    onBack: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToLocationDetected: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
    onNavigateToAjustes: () -> Unit,
) {
    val uiState by misLocalesViewModel.uiState.collectAsState()
    val isLoading = uiState.isLoading

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Con permiso y locales ya cargados, calcula la distancia y reordena por cercanía.
    LaunchedEffect(locationPermissionState.status.isGranted, uiState.locales.size) {
        if (locationPermissionState.status.isGranted && uiState.locales.isNotEmpty()) {
            misLocalesViewModel.refreshNearestLocal()
        }
    }

    var selectedTab by remember { mutableIntStateOf(1) }
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedSort by remember { mutableStateOf(LocalSortOption.Distancia) }
    var sortExpanded by remember { mutableStateOf(false) }
    var showGeoBanner by remember { mutableStateOf(true) }
    val shimmerBrush = shimmerBrush()
    val categories = remember(uiState.locales) {
        uiState.locales
            .map { it.categoria }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }
    val visibleLocales = remember(uiState.locales, query, selectedCategory, selectedSort) {
        uiState.locales
            .asSequence()
            .filter { local ->
                query.isBlank() || local.nombre.contains(query.trim(), ignoreCase = true)
            }
            .filter { local ->
                selectedCategory == null || local.categoria == selectedCategory
            }
            .let { locales ->
                when (selectedSort) {
                    LocalSortOption.Distancia -> locales.sortedBy { it.distanciaMetros ?: Double.MAX_VALUE }
                    LocalSortOption.RatingDesc -> locales.sortedByDescending { it.ratingValue() }
                    LocalSortOption.NameAsc -> locales.sortedBy { it.nombre.lowercase() }
                    LocalSortOption.OpenFirst -> locales.sortedWith(
                        compareByDescending<Local> { it.abierto }
                            .thenBy { it.nombre.lowercase() }
                    )
                }
            }
            .toList()
    }

    LaunchedEffect(categories) {
        if (selectedCategory != null && selectedCategory !in categories) {
            selectedCategory = null
        }
    }

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
        containerColor = MaterialTheme.colorScheme.background
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Elegí en qué local querés pedir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (uiState.isOffline) {
                    Spacer(Modifier.height(14.dp))
                    OfflineBanner()
                }

                if (activeCart != null) {
                    Spacer(Modifier.height(14.dp))
                    ActiveCartCard(cart = activeCart, onVer = onViewCart)
                }

                Spacer(Modifier.height(14.dp))

                // Location detection prompt
                if (!locationPermissionState.status.isGranted && showGeoBanner) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Detectar locales cercanos", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                Text("Permití el acceso a tu ubicación para ver distancias.", style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = { locationPermissionState.launchPermissionRequest() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Activar", fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Buscador
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (query.isBlank()) {
                                        Text(
                                            "Buscar local...",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // El más cercano a vos — debajo del buscador, arriba de los filtros.
                uiState.closestLocal?.let { closest ->
                    if (locationPermissionState.status.isGranted && closest.distanciaMetros != null) {
                        Text(
                            "EL MÁS CERCANO A VOS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Pimentón,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        LocalCard(local = closest, onClick = { onLocalSelected(closest.id) })
                        Spacer(Modifier.height(16.dp))
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip(
                        text = "Todos",
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                    categories.forEach { category ->
                        CategoryChip(
                            text = category,
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Box {
                    Surface(
                        modifier = Modifier.clickable { sortExpanded = true },
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Orden: ${selectedSort.label}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Ordenar locales",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false }
                    ) {
                        LocalSortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    selectedSort = option
                                    sortExpanded = false
                                }
                            )
                        }
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
                        "${visibleLocales.count { it.abierto }} LOCALES DISPONIBLES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
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
                    visibleLocales.forEach { local ->
                        LocalCard(local = local, onClick = { onLocalSelected(local.id) })
                        Spacer(Modifier.height(10.dp))
                    }
                    if (visibleLocales.isEmpty() && uiState.error == null) {
                        Text(
                            "No encontramos locales con esos filtros.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    if (uiState.error != null) {
                        LoadLocalesError(
                            message = uiState.error,
                            onRetry = { misLocalesViewModel.loadLocales() }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}


@Composable
private fun LoadLocalesError(
    message: String?,
    onRetry: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "No pudimos cargar los locales.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                message ?: "Revisá tu conexión e intentá de nuevo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
            )
            TextButton(
                onClick = onRetry,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(999.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun Local.ratingValue(): Float =
    rating.replace(",", ".").toFloatOrNull() ?: 0f

@Composable
private fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
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
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
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
private fun LocalCard(local: Local, onClick: () -> Unit) {
    val tiempoEntrega = local.tiempoEntrega

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = local.abierto) { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
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
                    Text(local.nombre, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = QLessStatusColors.enPreparacion, modifier = Modifier.size(14.dp))
                        Text(local.rating, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    }
                    
                    // Barrio
                    Text(local.barrio, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    // Distancia (Badge)
                    local.distanciaMetros?.let { distance ->
                        val distanceText = if (distance < 1000) "${distance.toInt()}m" else "%.1f km".format(distance / 1000)
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(10.dp), tint = MaterialTheme.colorScheme.secondary)
                                Text(distanceText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(Modifier.weight(1f))
                    
                    // Tiempo
                    if (tiempoEntrega != null) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = QLessStatusColors.enPreparacionSurface
                        ) {
                            Text(tiempoEntrega, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = QLessStatusColors.enPreparacion, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (local.tienePromo) {
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(999.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Text("10% OFF", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("ViewModelConstructorInComposable") // Solo preview; VM construido a mano a propósito.
private fun MisLocalesPreview() {
    QLessTheme { MisLocalesScreen(misLocalesViewModel = MisLocalesViewModel(), onLocalSelected = { _ -> }, onBack = {}, onNavigateToInicio = {}, onNavigateToLocationDetected = {}, onNavigateToScanQr = {}, onNavigateToMisPedidos = {}, onNavigateToAjustes = {}) }
}
