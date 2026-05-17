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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

private data class CartItem(val emoji: String, val name: String, val detail: String, val unitPrice: Int, var quantity: Int)

@Composable
fun CartScreen(
    onConfirm: () -> Unit,
    onBack: () -> Unit,
) {
    val items = remember {
        mutableStateListOf(
            CartItem("🍔", "Combo Big Classic", "Mediano · Sin cebolla", 4500, 1),
            CartItem("🍟", "Papas Fritas Grandes", "Con mayonesa", 1200, 2),
            CartItem("🥤", "Gaseosa 500ml", "Coca-Cola", 700, 1),
        )
    }
    var notes by remember { mutableStateOf("") }

    val subtotal = items.sumOf { it.unitPrice * it.quantity }
    val discount = (subtotal * 0.10).toInt()
    val total = subtotal - discount

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
                        "Mi carrito",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Espresso
                    )
                }
                HorizontalDivider(color = Melocotón)
            }
        },
        bottomBar = {
            Surface(
                color = CremaCálida,
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
                    colors = ButtonDefaults.buttonColors(containerColor = Pimentón)
                ) {
                    Text(
                        "Confirmar pedido — $${"%,d".format(total)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
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

            // Header del local
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Mantequilla,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🍔", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Big Pons – San Isidro", fontWeight = FontWeight.SemiBold, color = Espresso)
                    }
                    TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                        Text("Cambiar", color = Pimentón, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Tu pedido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Espresso)
            Spacer(Modifier.height(8.dp))

            items.forEachIndexed { index, item ->
                CartItemRow(
                    item = item,
                    onAdd = { items[index] = item.copy(quantity = item.quantity + 1) },
                    onRemove = {
                        if (item.quantity > 1) items[index] = item.copy(quantity = item.quantity - 1)
                        else items.removeAt(index)
                    }
                )
                Spacer(Modifier.height(8.dp))
            }

            TextButton(onClick = onBack, contentPadding = PaddingValues(vertical = 4.dp)) {
                Text("+ Agregar más productos", color = Pimentón, fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(color = Melocotón, modifier = Modifier.padding(vertical = 16.dp))

            // Resumen
            Text("Resumen del pedido", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Espresso)
            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", color = Madera)
                Text("$${"%,d".format(subtotal)}", color = Espresso)
            }
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Descuento (10% primera vez)", color = Albahaca)
                Text("−$${"%,d".format(discount)}", color = Albahaca)
            }
            HorizontalDivider(color = Melocotón, modifier = Modifier.padding(vertical = 10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold, color = Espresso, style = MaterialTheme.typography.titleMedium)
                Text("$${"%,d".format(total)}", fontWeight = FontWeight.Bold, color = Pimentón, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(20.dp))

            // Notas
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("Notas para el local... (sin picante, bien cocido)", color = Madera.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Melocotón,
                    focusedBorderColor = Pimentón,
                    unfocusedContainerColor = Mantequilla,
                    focusedContainerColor = Mantequilla,
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
        color = Mantequilla,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Pimentón
            ) {
                Text(
                    "x${item.quantity}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold, color = Espresso)
                Text(item.detail, style = MaterialTheme.typography.bodySmall, color = Madera)
            }
            Text(
                "$${"%,d".format(item.unitPrice * item.quantity)}",
                fontWeight = FontWeight.Bold,
                color = Espresso
            )
            Spacer(Modifier.width(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("−", color = Pimentón, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.clickable { onRemove() })
                Text("+", color = Pimentón, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.clickable { onAdd() })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartPreview() {
    QLessTheme { CartScreen(onConfirm = {}, onBack = {}) }
}