package com.qless.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

@Composable
fun LocationDetectedScreen(
    onConfirmLocation: () -> Unit,
    onRejectLocation: () -> Unit,
    onSearchAnother: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD6E8F5))
    ) {
        MapBackground()

        GpsStatusPill(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 32.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 190.dp)
                .size(128.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Pimentón.copy(alpha = 0.08f))
            )
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 31.dp,
                            topEnd = 31.dp,
                            bottomEnd = 8.dp,
                            bottomStart = 31.dp
                        )
                    )
                    .background(Pimentón),
                contentAlignment = Alignment.Center
            ) {
                Text("🍔", fontSize = 28.sp)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(CremaCálida)
                .padding(horizontal = 28.dp)
                .padding(top = 14.dp, bottom = 28.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color(0xFFD4C5B8))
            )

            Spacer(Modifier.height(28.dp))

            Text(
                "UBICACIÓN DETECTADA",
                style = MaterialTheme.typography.labelMedium,
                color = Pimentón,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.6.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "¿Estás acá?",
                style = MaterialTheme.typography.headlineMedium,
                color = Espresso,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(26.dp))

            DetectedRestaurantCard()

            Spacer(Modifier.height(24.dp))

            Text(
                "Detectamos que estás cerca de este local.\n¿Querés ver el menú y hacer un pedido?",
                style = MaterialTheme.typography.bodyMedium,
                color = Madera,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onRejectLocation,
                    modifier = Modifier
                        .weight(0.9f)
                        .height(72.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Mantequilla),
                    border = BorderStroke(1.dp, Melocotón)
                ) {
                    Text(
                        "No,\ngracias",
                        color = Espresso,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 18.sp
                    )
                }

                Button(
                    onClick = onConfirmLocation,
                    modifier = Modifier
                        .weight(1.55f)
                        .height(72.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Pimentón),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("¡Sí, pedir!", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                }
            }

            Spacer(Modifier.height(22.dp))

            TextButton(onClick = onSearchAnother) {
                Text(
                    "¿No es este local? ",
                    color = Madera,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Buscar otro",
                    color = Pimentón,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MapBackground() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.62f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFD6E8F5),
                        Color(0xFFD6E8F5),
                        Color(0xFFD6E8F5).copy(alpha = 0.86f)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 132.dp)
                .fillMaxWidth(0.9f)
                .height(190.dp)
                .background(Color(0xFFC5D2DD).copy(alpha = 0.38f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(74.dp)
                .fillMaxHeight()
                .background(Color.White.copy(alpha = 0.16f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp)
                .width(22.dp)
                .height(150.dp)
                .background(Color.White.copy(alpha = 0.16f))
        )
    }
}

@Composable
private fun GpsStatusPill(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Albahaca.copy(alpha = 0.65f))
            )
            Spacer(Modifier.width(10.dp))
            Text("GPS activo · precisión alta", color = Espresso, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun DetectedRestaurantCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = Mantequilla,
        border = BorderStroke(1.5.dp, Color(0xFFEAD5C5))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(96.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Pimentón)
            )
            Spacer(Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Melocotón),
                contentAlignment = Alignment.Center
            ) {
                Text("🍔", fontSize = 34.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Big Pons", color = Espresso, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                    Spacer(Modifier.weight(1f))
                    DistancePill()
                }
                Spacer(Modifier.height(4.dp))
                Text("San Isidro · Hamburguesas & Snacks", color = Madera, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                    SmallStatusPill("Abierto", Albahaca, AlbahacaClaro)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Azafrán, modifier = Modifier.size(12.dp))
                        Text("4.8", color = Madera, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                    SmallStatusPill("15–25 min", Azafrán, AzafránClaro)
                }
                Spacer(Modifier.height(7.dp))
                SmallStatusPill("10% OFF 1.er pedido", Pimentón, Melocotón)
            }
        }
    }
}

@Composable
private fun DistancePill() {
    Surface(shape = RoundedCornerShape(999.dp), color = Espresso) {
        Text(
            "~30 m",
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SmallStatusPill(text: String, color: Color, container: Color) {
    Surface(shape = RoundedCornerShape(999.dp), color = container) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationDetectedPreview() {
    QLessTheme {
        LocationDetectedScreen(onConfirmLocation = {}, onRejectLocation = {}, onSearchAnother = {})
    }
}
