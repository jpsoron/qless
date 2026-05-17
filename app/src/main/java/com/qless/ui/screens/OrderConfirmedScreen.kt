package com.qless.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

@Composable
fun OrderConfirmedScreen(
    onViewTracking: () -> Unit,
    onGoHome: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "check_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CremaCálida)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))

        // Checkmark animado
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Melocotón)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Albahaca),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            "¡Pedido confirmado!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Espresso,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Tu pago se procesó correctamente y el local ya está preparando tu pedido.",
            style = MaterialTheme.typography.bodyMedium,
            color = Madera,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(Modifier.height(32.dp))

        // Resumen
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            color = Mantequilla,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pedido #4521", fontWeight = FontWeight.Bold, color = Espresso, style = MaterialTheme.typography.titleMedium)
                Text("Big Pons – San Isidro", color = Madera, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Text("Retiro estimado: 15 min", color = Pimentón, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(32.dp))

        // CTAs
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onViewTracking,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Pimentón)
            ) {
                Text("Ver seguimiento", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = onGoHome,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Melocotón),
                border = androidx.compose.foundation.BorderStroke(0.dp, Color.Transparent)
            ) {
                Text("Volver al inicio", fontWeight = FontWeight.Bold, color = Pimentón, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "También podés ver este pedido desde Mis Pedidos",
            style = MaterialTheme.typography.bodySmall,
            color = Madera.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderConfirmedPreview() {
    QLessTheme { OrderConfirmedScreen(onViewTracking = {}, onGoHome = {}) }
}