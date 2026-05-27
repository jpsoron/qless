package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun MetodosDePagoScreen(
    onBack: () -> Unit,
    onNavigateToAgregarMetodo: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToMisLocales: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
    onNavigateToAjustes: () -> Unit
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp).statusBarsPadding())

            // Header con botón de volver
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Melocotón)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Pimentón
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "Métodos de pago",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Espresso
                )
            }

            Spacer(Modifier.height(24.dp))

            // Subtítulo y botón de agregar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Administrá tarjetas y billeteras\nasociadas",
                    color = Madera,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = onNavigateToAgregarMetodo,
                    colors = ButtonDefaults.buttonColors(containerColor = Pimentón),
                    shape = RoundedCornerShape(99.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Agregar método", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Lista de métodos de pago
            PaymentMethodCard(
                icon = "VISA",
                iconBgColor = Color(0xFF1A1F71),
                iconTextColor = Color.White,
                title = "Visa crédito •••• 4242",
                subtitle = "Vence 08/29",
                description = "Predeterminada para pedidos",
                tag = "Principal"
            )

            Spacer(Modifier.height(16.dp))

            PaymentMethodCard(
                icon = "MP",
                iconBgColor = Color(0xFFFFE100),
                iconTextColor = Color(0xFF2D3277),
                title = "Mercado Pago",
                subtitle = "Cuenta vinculada",
                description = "Pagos rápidos y promociones",
                tag = "Billetera"
            )

            Spacer(Modifier.height(16.dp))

            PaymentMethodCard(
                icon = "MC",
                iconBgColor = Color(0xFFF79E1B),
                iconTextColor = Color.White,
                title = "Mastercard débito •••• 1034",
                subtitle = "Vence 03/28",
                description = "Usada por última vez ayer"
            )

            Spacer(Modifier.height(32.dp))

            // Texto informativo al pie
            Text(
                "Elegí el medio de pago al confirmar tu carrito. Los locales no ven el número completo.",
                color = Madera.copy(alpha = 0.6f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
fun PaymentMethodCard(
    icon: String,
    iconBgColor: Color,
    iconTextColor: Color,
    title: String,
    subtitle: String,
    description: String,
    tag: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Mantequilla,
        border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del método
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    color = iconTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.width(16.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Espresso,
                    fontSize = 16.sp
                )
                Text(
                    text = subtitle,
                    color = Madera,
                    fontSize = 13.sp
                )
                Text(
                    text = description,
                    color = Madera.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            // Tag opcional
            if (tag != null) {
                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Madera.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Madera,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MetodosDePagoPreview() {
    QLessTheme {
        MetodosDePagoScreen({}, {}, {}, {}, {}, {}, {})
    }
}
