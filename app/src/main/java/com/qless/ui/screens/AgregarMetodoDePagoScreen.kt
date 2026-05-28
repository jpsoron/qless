package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*

@Composable
fun AgregarMetodoDePagoScreen(
    onBack: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToMisLocales: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
    onNavigateToAjustes: () -> Unit
) {
    var nombre by remember { mutableStateOf("María González") }
    var numero by remember { mutableStateOf("4242 4242 4242 4242") }
    var vencimiento by remember { mutableStateOf("08/29") }
    var cvc by remember { mutableStateOf("123") }
    var esPrincipal by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Tarjeta, 1: Billetera

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
        containerColor = CremaCálida
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp).statusBarsPadding())

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Melocotón)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Pimentón)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "Agregar método",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Espresso
                )
            }
            Text(
                "Completá los datos del medio de pago",
                color = Madera,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 52.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Tabs Tarjeta / Billetera
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TabButton("Tarjeta", selectedTab == 0) { selectedTab = 0 }
                TabButton("Billetera", selectedTab == 1) { selectedTab = 1 }
            }

            Spacer(Modifier.height(16.dp))

            // Formulario
            Text("Nombre en la tarjeta", color = Madera, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            CustomTextField(value = nombre, onValueChange = { nombre = it })

            Spacer(Modifier.height(16.dp))

            Text("Número", color = Madera, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            CustomTextField(value = numero, onValueChange = { numero = it })

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Vencimiento", color = Madera, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    CustomTextField(value = vencimiento, onValueChange = { vencimiento = it })
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Código de seguridad", color = Madera, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    CustomTextField(value = cvc, onValueChange = { cvc = it })
                }
            }

            Spacer(Modifier.height(24.dp))

            // Guardar como principal
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { esPrincipal = !esPrincipal },
                shape = RoundedCornerShape(12.dp),
                color = Mantequilla,
                border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (esPrincipal) Pimentón else Madera.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Guardar como método principal", fontWeight = FontWeight.SemiBold, color = Espresso, fontSize = 15.sp)
                        Text("Se sugiere primero en el checkout", color = Madera, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Botones de acción
            Button(
                onClick = { /* TODO: Guardar */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Pimentón),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar método", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Pimentón)
            ) {
                Text("Cancelar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Borgoña)
            }

            Spacer(Modifier.height(24.dp))

            // Volver a pagos link
            Text(
                "← Volver a pagos",
                color = Pimentón,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.clickable { onBack() }
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Los datos sensibles se procesan de forma segura y no quedan visibles para el local.",
                color = Madera.copy(alpha = 0.6f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(99.dp),
        color = if (isSelected) Pimentón else Color.Transparent,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Melocotón) else null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = if (isSelected) Color.White else Madera,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Melocotón,
            focusedBorderColor = Pimentón,
            unfocusedTextColor = Espresso,
            focusedTextColor = Espresso
        ),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun AgregarMetodoDePagoPreview() {
    QLessTheme {
        AgregarMetodoDePagoScreen({}, {}, {}, {}, {}, {})
    }
}
