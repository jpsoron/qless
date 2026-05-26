package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*

@Composable
fun EliminarCuentaScreen(
    onBack: () -> Unit,
    onConfirmDelete: () -> Unit,
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
        containerColor = CremaCálida
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(12.dp).statusBarsPadding())

            // Header: Botón Volver + Título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Melocotón, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Pimentón
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    "Eliminar cuenta",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Espresso
                )
            }

            Spacer(Modifier.height(32.dp))

            // Info de Usuario
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Melocotón),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "M",
                        color = Pimentón,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "María González",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Espresso
                    )
                    Text(
                        "maria@email.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Madera.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            // El resto de la pantalla según la imagen queda vacío por ahora, 
            // pero agrego lógica de advertencia y botón para que sea funcional.
            
            Text(
                "¿Estás seguro de que quieres eliminar tu cuenta? Esta acción borrará permanentemente todos tus datos, historial de pedidos y métodos de pago.",
                style = MaterialTheme.typography.bodyLarge,
                color = Madera,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onConfirmDelete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Borgoña)
            ) {
                Text("Eliminar cuenta definitivamente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Madera)
            ) {
                Text("Cancelar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EliminarCuentaPreview() {
    QLessTheme {
        EliminarCuentaScreen({}, {}, {}, {}, {}, {}, {})
    }
}
