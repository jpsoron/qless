package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*

@Composable
fun NotificacionesScreen(
    onBack: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToMisLocales: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
    onNavigateToAjustes: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            QLessBottomNav(
                selectedTab = 3,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onNavigateToInicio()
                        1 -> onNavigateToMisLocales()
                        2 -> onNavigateToMisPedidos()
                        3 -> onNavigateToAjustes()
                        4 -> onNavigateToScanQr()
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(12.dp).statusBarsPadding())

            // Back button and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    "Notificaciones",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Elegí qué avisos querés recibir",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Notification Items
            NotificationToggleItem(
                title = "Estado del pedido",
                description = "Cambios de estado y tiempo estimado",
                initialChecked = true
            )
            Spacer(Modifier.height(12.dp))
            NotificationToggleItem(
                title = "Pedido listo",
                description = "Aviso cuando ya podés retirarlo",
                initialChecked = true
            )
            Spacer(Modifier.height(12.dp))
            NotificationToggleItem(
                title = "Sonido y vibración",
                description = "Refuerzo para avisos importantes",
                initialChecked = true
            )
            Spacer(Modifier.height(12.dp))
            NotificationToggleItem(
                title = "Promociones",
                description = "Descuentos y cupones de tus locales",
                initialChecked = false
            )
            Spacer(Modifier.height(12.dp))
            NotificationToggleItem(
                title = "Novedades",
                description = "Cambios de menú y nuevos locales",
                initialChecked = false
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Los avisos de pedido listo conviene dejarlos activos para no perder el retiro.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun NotificationToggleItem(
    title: String,
    description: String,
    initialChecked: Boolean
) {
    var isChecked by remember { mutableStateOf(initialChecked) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray.copy(alpha = 0.4f),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificacionesPreview() {
    QLessTheme {
        NotificacionesScreen({}, {}, {}, {}, {}, {})
    }
}