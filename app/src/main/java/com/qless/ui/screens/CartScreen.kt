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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.data.CartItem
import com.qless.ui.viewmodel.CartViewModel
import com.qless.ui.theme.*

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    isDarkTheme: Boolean = false,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState by cartViewModel.uiState.collectAsState()
    val items = uiState.items
    var notes by remember { mutableStateOf("") }

    val subtotal = items.sumOf { it.unitPrice * it.quantity }
    val discount = (subtotal * 0.10).toInt()
    val total = subtotal - discount

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
                        "Mi carrito",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer)
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Pimentón else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Confirmar pedido — $${"%,d".format(total)}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
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

            // Header del local
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🍔", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Big Pons – San Isidro", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                        Text("Cambiar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Tu pedido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))

            items.forEach { item ->
                CartItemRow(
                    item = item,
                    onAdd = { cartViewModel.addItem(item.emoji, item.name, item.detail, item.unitPrice) },
                    onRemove = { cartViewModel.removeItem(item.name) }
                )
                Spacer(Modifier.height(8.dp))
            }

            TextButton(onClick = onBack, contentPadding = PaddingValues(vertical = 4.dp)) {
                Text("+ Agregar más productos", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.padding(vertical = 16.dp))

            // Resumen
            Text("Resumen del pedido", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${"%,d".format(subtotal)}", color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Descuento (10% primera vez)", color = QLessStatusColors.disponible)
                Text("−$${"%,d".format(discount)}", color = QLessStatusColors.disponible)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.padding(vertical = 10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium)
                Text("$${"%,d".format(total)}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(20.dp))

            // Notas
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("Notas para el local... (sin picante, bien cocido)", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                minLines = 2
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CartItemRow(item: CartItem, onAdd: () -> Unit, onRemove: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    "x${item.quantity}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(item.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "$${"%,d".format(item.unitPrice * item.quantity)}",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.width(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("−", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, modifier = Modifier.clickable { onRemove() })
                Text("+", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, modifier = Modifier.clickable { onAdd() })
            }
        }
    }
}

