package com.qless.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

@Composable
fun OrderSummaryScreen(
    isDarkTheme: Boolean = false,
    onBack: () -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp).statusBarsPadding())

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Resumen del pedido",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(2.dp))

            Text("Pedido #5801 · Agustina López", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(999.dp), color = QLessStatusColors.disponibleSurface) {
                    Text(
                        "Retirado",
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                        color = QLessStatusColors.disponible,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.weight(1f))
                Text("Retirado hoy a las\n12:06", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(16.dp))

            SummaryHeaderCard(isDarkTheme)

            Spacer(Modifier.height(22.dp))

            ItemsCard()

            Spacer(Modifier.height(22.dp))

            SummaryTrackingCard(isDarkTheme)

            Spacer(Modifier.height(22.dp))

            TextButton(
                onClick = onBack,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("← Volver al historial", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun SummaryHeaderCard(isDarkTheme: Boolean = false) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (isDarkTheme) MaderaOscura else Color.White.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, Color(0xFFE4CDBB))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Big Pons · San Isidro", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                Text("Retiro en mostrador", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                Text("Tarjeta terminada en 4242", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f), style = MaterialTheme.typography.bodySmall)
            }
            Surface(shape = RoundedCornerShape(999.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    "$18.900",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ItemsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, Color(0xFFE4CDBB))
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Items", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                Spacer(Modifier.height(8.dp))
                Text("2x Burger clásica", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Text("1x Papas medianas", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Text("1x Gaseosa lata", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("Sin cebolla · cheddar extra", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(92.dp)
                    .background(Color(0xFFE0CDBD))
            )
            Spacer(Modifier.width(18.dp))
            Column(modifier = Modifier.width(92.dp)) {
                Text("Totales", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                Text("Subtotal\n$17.000", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text("Tasa\n$1.900", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun SummaryTrackingCard(isDarkTheme: Boolean = false) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (isDarkTheme) MaderaOscura else Color.White.copy(alpha = 0.42f),
        border = BorderStroke(1.dp, Color(0xFFE4CDBB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Seguimiento", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            Spacer(Modifier.height(18.dp))
            SummaryStep("Pedido recibido", "Confirmado a las 11:41", showLine = true)
            SummaryStep("En preparación", "Cocina inició a las 11:46", showLine = true)
            SummaryStep("Retirado", "Cliente retiró el pedido a las 12:06", showLine = false)
        }
    }
}

@Composable
private fun SummaryStep(title: String, detail: String, showLine: Boolean) {
    Row(verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(QLessStatusColors.disponible),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
            }
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(34.dp)
                        .background(QLessStatusColors.disponible)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (showLine) 8.dp else 0.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(detail, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderSummaryPreview() {
    QLessTheme {
        OrderSummaryScreen(onBack = {})
    }
}
