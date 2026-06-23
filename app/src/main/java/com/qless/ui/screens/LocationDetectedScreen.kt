package com.qless.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.qless.domain.model.Local
import com.qless.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetectedScreen(
    local: Local,
    distanceMeters: Double? = null,
    onConfirmLocation: () -> Unit,
    onRejectLocation: () -> Unit,
    onSearchAnother: () -> Unit,
) {
    // El sheet arranca EXPANDIDO (completo) y no puede ocultarse.
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded,
        skipHiddenState = true,
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    // Altura mínima del sheet: deja ver el CTA cuando se minimiza.
    val peekHeight = 460.dp

    // Cámara hoisteada para detectar gestos del usuario sobre el mapa.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(local.latitud, local.longitud), 16.5f)
    }

    // Al mover/scrollear el mapa con un gesto, minimizar el sheet.
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving &&
            cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE &&
            sheetState.currentValue == SheetValue.Expanded
        ) {
            sheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetShadowElevation = 16.dp,
        sheetContent = {
            LocationSheetContent(
                local = local,
                distanceMeters = distanceMeters,
                onConfirmLocation = onConfirmLocation,
                onRejectLocation = onRejectLocation,
                onSearchAnother = onSearchAnother,
            )
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LocationMap(
                local = local,
                cameraPositionState = cameraPositionState,
                bottomPadding = peekHeight,
                modifier = Modifier.fillMaxSize(),
            )
            GpsStatusPill(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun LocationSheetContent(
    local: Local,
    distanceMeters: Double?,
    onConfirmLocation: () -> Unit,
    onRejectLocation: () -> Unit,
    onSearchAnother: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(bottom = 24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "UBICACIÓN DETECTADA",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.6.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "¿Estás en ${local.nombre}?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(22.dp))

        DetectedRestaurantCard(local = local, distanceMeters = distanceMeters)

        Spacer(Modifier.height(22.dp))

        Text(
            "Detectamos que estás cerca de este local.\n¿Querés ver el menú y hacer un pedido?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onRejectLocation,
                modifier = Modifier
                    .weight(0.9f)
                    .height(72.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pimentón,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "No,\ngracias",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp
                )
            }

            Button(
                onClick = onConfirmLocation,
                modifier = Modifier
                    .weight(1.55f)
                    .height(72.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Pimentón),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("¡Sí, pedir!", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            }
        }

        Spacer(Modifier.height(18.dp))

        TextButton(onClick = onSearchAnother) {
            Text(
                "¿No es este local? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Buscar otro",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LocationMap(
    local: Local,
    cameraPositionState: CameraPositionState,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    // Sin coordenadas cargadas: cae al mapa decorativo.
    if (local.latitud == 0.0 && local.longitud == 0.0) {
        MapBackground()
        return
    }
    val position = LatLng(local.latitud, local.longitud)
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        // Empuja el centro del mapa por encima del sheet: el pin queda centrado en lo visible.
        contentPadding = PaddingValues(bottom = bottomPadding),
        // Oculta los POIs (otros comercios) para reducir ruido.
        properties = MapProperties(mapStyleOptions = MapStyleOptions(MAP_STYLE_NO_POI)),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = false,
            mapToolbarEnabled = false,
            rotationGesturesEnabled = false,
            tiltGesturesEnabled = false,
            // scroll y zoom quedan habilitados (default) para mover el mapa.
        ),
    ) {
        Marker(
            state = rememberMarkerState(position = position),
            title = local.nombre,
        )
    }
}

// Estilo de mapa que apaga los puntos de interés y etiquetas de comercios.
private const val MAP_STYLE_NO_POI = """
[
  { "featureType": "poi", "stylers": [ { "visibility": "off" } ] },
  { "featureType": "poi.business", "stylers": [ { "visibility": "off" } ] },
  { "featureType": "transit", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }
]
"""

@Composable
private fun MapBackground() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.62f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFD6E8F5),
                        Color(0xFFD6E8F5),
                        Color(0xFFD6E8F5).copy(alpha = 0.86f)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 132.dp)
                .fillMaxWidth(0.9f)
                .height(190.dp)
                .background(Color(0xFFC5D2DD).copy(alpha = 0.38f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(74.dp)
                .fillMaxHeight()
                .background(Color.White.copy(alpha = 0.16f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp)
                .width(22.dp)
                .height(150.dp)
                .background(Color.White.copy(alpha = 0.16f))
        )
    }
}

@Composable
private fun GpsStatusPill(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(QLessStatusColors.disponible.copy(alpha = 0.65f))
            )
            Spacer(Modifier.width(10.dp))
            Text("GPS activo · precisión alta", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun DetectedRestaurantCard(local: Local, distanceMeters: Double?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.5.dp, Color(0xFFEAD5C5))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(96.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(local.emoji, fontSize = 34.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(local.nombre, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                    Spacer(Modifier.weight(1f))
                    DistancePill(distanceMeters)
                }
                Spacer(Modifier.height(4.dp))
                Text("${local.barrio} · ${local.categoria}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (local.abierto) {
                        SmallStatusPill("Abierto", QLessStatusColors.disponible, QLessStatusColors.disponibleSurface)
                    } else {
                        SmallStatusPill("Cerrado", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = QLessStatusColors.enPreparacion, modifier = Modifier.size(12.dp))
                        Text(local.rating, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                    local.tiempoEntrega?.let {
                        SmallStatusPill(it, QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
                    }
                }
                if (local.tienePromo) {
                    Spacer(Modifier.height(7.dp))
                    SmallStatusPill("10% OFF 1.er pedido", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                }
            }
        }
    }
}

@Composable
private fun DistancePill(distanceMeters: Double?) {
    val label = distanceMeters?.let { "~${it.roundToInt()} m" } ?: "cerca"
    Surface(shape = RoundedCornerShape(999.dp), color = MaterialTheme.colorScheme.onSurface) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SmallStatusPill(text: String, color: Color, container: Color) {
    Surface(shape = RoundedCornerShape(999.dp), color = container) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationDetectedPreview() {
    QLessTheme {
        LocationDetectedScreen(
            local = Local(
                id = "1", emoji = "🍔", nombre = "Big Pons", categoria = "Hamburguesas",
                barrio = "San Isidro", rating = "4.8", tiempoEntrega = "15–25 min",
                abierto = true, tienePromo = true, destacado = false,
            ),
            distanceMeters = 28.0,
            onConfirmLocation = {}, onRejectLocation = {}, onSearchAnother = {},
        )
    }
}
