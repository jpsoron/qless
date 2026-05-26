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
    onBack: () -> Unit,
) {
    Scaffold(containerColor = CremaCálida) { padding ->
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
                        .background(Melocotón)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Pimentón)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Resumen del pedido",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Espresso,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(2.dp))

            Text("Pedido #5801 · Agustina López", color = Madera, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(999.dp), color = AlbahacaClaro) {
                    Text(
                        "Retirado",
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                        color = Albahaca,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(Modifier.weight(1f))
                Text("Retirado hoy a las\n12:06", color = Madera, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(16.dp))

            SummaryHeaderCard()

            Spacer(Modifier.height(22.dp))

            ItemsCard()

            Spacer(Modifier.height(22.dp))

            SummaryTrackingCard()

            Spacer(Modifier.height(22.dp))

            TextButton(
                onClick = onBack,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("← Volver al historial", color = Pimentón, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun SummaryHeaderCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, Color(0xFFE4CDBB))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Big Pons · San Isidro", color = Espresso, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                Text("Retiro en mostrador", color = Madera, style = MaterialTheme.typography.bodySmall)
                Text("Tarjeta terminada en 4242", color = Madera.copy(alpha = 0.62f), style = MaterialTheme.typography.bodySmall)
            }
            Surface(shape = RoundedCornerShape(999.dp), color = Melocotón) {
                Text(
                    "$18.900",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp),
                    color = Pimentón,
                    fontWeight = FontWeight.ExtraBold
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
        color = Mantequilla,
        border = BorderStroke(1.dp, Color(0xFFE4CDBB))
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Items", color = Espresso, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Spacer(Modifier.height(8.dp))
                Text("2x Burger clásica", color = Espresso, fontWeight = FontWeight.Bold)
                Text("1x Papas medianas", color = Espresso, fontWeight = FontWeight.Bold)
                Text("1x Gaseosa lata", color = Espresso, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Sin cebolla · cheddar extra", color = Madera, style = MaterialTheme.typography.bodySmall)
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(92.dp)
                    .background(Color(0xFFE0CDBD))
            )
            Spacer(Modifier.width(18.dp))
            Column(modifier = Modifier.width(92.dp)) {
                Text("Totales", color = Madera, fontWeight = FontWeight.ExtraBold)
                Text("Subtotal\n$17.000", color = Madera.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text("Tasa\n$1.900", color = Madera.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun SummaryTrackingCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.42f),
        border = BorderStroke(1.dp, Color(0xFFE4CDBB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Seguimiento", color = Espresso, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
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
                    .background(Albahaca),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
            }
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(34.dp)
                        .background(Albahaca)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (showLine) 8.dp else 0.dp)) {
            Text(title, color = Espresso, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text(detail, color = Madera, style = MaterialTheme.typography.bodySmall)
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
