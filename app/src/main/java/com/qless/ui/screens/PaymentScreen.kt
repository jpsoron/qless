package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.domain.usecase.FIRST_ORDER_DISCOUNT_RATE
import com.qless.ui.theme.*
import com.qless.ui.viewmodel.CartViewModel

private data class PaymentMethodOption(
    val icon: ImageVector,
    val label: String,
    val sublabel: String,
    val enabled: Boolean,
)

// MVP: solo pago en efectivo en el local. Los métodos digitales se muestran
// grisados ("Próximamente") para comunicar el roadmap sin habilitarlos.
private val methods = listOf(
    PaymentMethodOption(Icons.Default.Payments, "Efectivo en local", "Pagás al retirar tu pedido", enabled = true),
    PaymentMethodOption(Icons.Default.CreditCard, "Tarjeta de crédito o débito", "Próximamente", enabled = false),
    PaymentMethodOption(Icons.Default.AccountBalanceWallet, "Billeteras digitales", "Próximamente", enabled = false),
)

@Composable
fun PaymentScreen(
    cartViewModel: CartViewModel,
    isDarkTheme: Boolean = false,
    firstOrderDiscount: Boolean = false,
    onPaymentSuccess: () -> Unit,
    onBack: () -> Unit,
) {
    val cartUiState by cartViewModel.uiState.collectAsState()
    val cartItems = cartUiState.items
    val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
    // Mismo descuento de bienvenida que el carrito: solo si el perfil lo tiene disponible.
    val discount = if (firstOrderDiscount) (subtotal * FIRST_ORDER_DISCOUNT_RATE).toInt() else 0
    val cartTotal = subtotal - discount
    val cartCount = cartItems.sumOf { it.quantity }
    val totalFormatted = "%,d".format(cartTotal)

    var selectedMethod by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
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
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(999.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Confirmar pago",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
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
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
                    StepDot(label = "Pago", isActive = true, isDone = false)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primaryContainer)
                    StepDot(label = "Listo", isActive = false, isDone = false)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer)
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = onPaymentSuccess,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Pimentón else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Confirmar pedido · $$totalFormatted", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "💵 Pagás en efectivo al retirar tu pedido",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
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
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        Text("Big Pons · $cartCount ${if (cartCount == 1) "ítem" else "ítems"}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    }
                    Text("$$totalFormatted", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "¿Cómo querés pagar?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))

            methods.forEachIndexed { index, method ->
                val isSelected = method.enabled && selectedMethod == index
                // Los métodos no disponibles van grisados y no son seleccionables.
                val contentAlpha = if (method.enabled) 1f else 0.4f
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .then(if (method.enabled) Modifier.clickable { selectedMethod = index } else Modifier),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.5.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(method.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(method.label, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha))
                            Text(method.sublabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha))
                        }
                        if (method.enabled) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedMethod = index },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(99.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    "Próximamente",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (discount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Descuento (10% primera vez)", color = QLessStatusColors.disponible, style = MaterialTheme.typography.titleSmall)
                    Text("−$${"%,d".format(discount)}", color = QLessStatusColors.disponible, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total a pagar", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.titleSmall)
                Text("$$totalFormatted", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleLarge)
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
                        isActive -> MaterialTheme.colorScheme.primary
                        isDone -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.primaryContainer
                    },
                    androidx.compose.foundation.shape.CircleShape
                )
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentPreview() {
    // Preview omitida: la pantalla depende de ViewModels cableados por AppModule
}
