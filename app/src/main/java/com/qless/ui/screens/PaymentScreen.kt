package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.qless.ui.theme.*

private data class PaymentMethod(val emoji: String, val label: String, val sublabel: String)

private val methods = listOf(
    PaymentMethod("💳", "Visa ····4521", "Vence 09/27"),
    PaymentMethod("💙", "MercadoPago", "Saldo disponible · $12.400"),
    PaymentMethod("💵", "Efectivo en local", "Pagás al retirar tu pedido"),
)

@Composable
fun PaymentScreen(
    onPaymentSuccess: () -> Unit,
    onNavigateToAgregarMetodo: () -> Unit,
    onBack: () -> Unit,
) {
    var selectedMethod by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(CremaCálida).statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Melocotón, RoundedCornerShape(999.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Pimentón, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Confirmar pago",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Espresso
                    )
                }

                // Stepper: Carrito → Pago → Listo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepDot(label = "Carrito", isActive = false, isDone = true)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Pimentón)
                    StepDot(label = "Pago", isActive = true, isDone = false)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Melocotón)
                    StepDot(label = "Listo", isActive = false, isDone = false)
                }

                HorizontalDivider(color = Melocotón)
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(CremaCálida)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = onPaymentSuccess,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Pimentón)
                ) {
                    Text("Pagar $5.760", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "🔒 Pago seguro con encriptación SSL",
                    style = MaterialTheme.typography.labelSmall,
                    color = Madera.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        containerColor = CremaCálida
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Resumen colapsado
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Mantequilla,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🍔 Big Pons · 3 ítems", color = Espresso, fontWeight = FontWeight.SemiBold)
                    Text("$5.760", color = Pimentón, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "¿Cómo querés pagar?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Espresso
            )
            Spacer(Modifier.height(12.dp))

            methods.forEachIndexed { index, method ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedMethod = index },
                    shape = RoundedCornerShape(12.dp),
                    color = Mantequilla,
                    border = androidx.compose.foundation.BorderStroke(
                        if (selectedMethod == index) 2.dp else 1.5.dp,
                        if (selectedMethod == index) Pimentón else Melocotón
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(method.emoji, fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(method.label, fontWeight = FontWeight.SemiBold, color = Espresso)
                            Text(method.sublabel, style = MaterialTheme.typography.bodySmall, color = Madera)
                        }
                        RadioButton(
                            selected = selectedMethod == index,
                            onClick = { selectedMethod = index },
                            colors = RadioButtonDefaults.colors(selectedColor = Pimentón)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onNavigateToAgregarMetodo, modifier = Modifier.fillMaxWidth()) {
                Text("💳 Otros métodos de pago", color = Madera)
                Spacer(Modifier.weight(1f))
                Text("Agregar", color = Pimentón, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total a pagar", color = Madera, style = MaterialTheme.typography.titleSmall)
                Text("$5.760", fontWeight = FontWeight.Bold, color = Espresso, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StepDot(label: String, isActive: Boolean, isDone: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    when {
                        isActive -> Pimentón
                        isDone -> Pimentón
                        else -> Melocotón
                    },
                    androidx.compose.foundation.shape.CircleShape
                )
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) Pimentón else Madera.copy(alpha = 0.5f),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentPreview() {
    QLessTheme { PaymentScreen(onPaymentSuccess = {}, onNavigateToAgregarMetodo = {}, onBack = {}) }
}
