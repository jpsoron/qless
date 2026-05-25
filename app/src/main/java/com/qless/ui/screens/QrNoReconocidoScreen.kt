package com.qless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

@Composable
fun QrNoReconocidoScreen(
    onRetry: () -> Unit,
    onManualInput: () -> Unit
) {
    val darkBackground = Color(0xFF0D0806) // Fondo casi negro con tinte Espresso
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        // --- ILUSTRACIÓN SUPERIOR (Marco + Icono Error) ---
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Marco de esquinas (L)
            Canvas(modifier = Modifier.size(160.dp)) {
                val cornerLength = 30.dp.toPx()
                val strokeWidth = 3.dp.toPx()
                val color = Borgoña.copy(alpha = 0.6f)

                // Arriba izquierda
                drawLine(color, Offset(0f, 0f), Offset(cornerLength, 0f), strokeWidth)
                drawLine(color, Offset(0f, 0f), Offset(0f, cornerLength), strokeWidth)
                // Arriba derecha
                drawLine(color, Offset(size.width, 0f), Offset(size.width - cornerLength, 0f), strokeWidth)
                drawLine(color, Offset(size.width, 0f), Offset(size.width, cornerLength), strokeWidth)
                // Abajo izquierda
                drawLine(color, Offset(0f, size.height), Offset(cornerLength, size.height), strokeWidth)
                drawLine(color, Offset(0f, size.height), Offset(0f, size.height - cornerLength), strokeWidth)
                // Abajo derecha
                drawLine(color, Offset(size.width, size.height), Offset(size.width - cornerLength, size.height), strokeWidth)
                drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - cornerLength), strokeWidth)
            }

            // Glow rojo detrás del icono
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .blur(20.dp)
                    .background(Borgoña.copy(alpha = 0.3f), CircleShape)
            )

            // Círculo central con X
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Borgoña
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        // --- TÍTULOS ---
        Text(
            text = "QR no reconocido",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(12.dp))
        
        Text(
            text = "Este código no corresponde a ningún local registrado en QLess",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp),
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(40.dp))

        // --- LISTA DE CONSEJOS ---
        TipCard(icon = "💡", text = "Asegurate de apuntar al QR de la mesa o mostrador del local")
        Spacer(Modifier.height(12.dp))
        TipCard(icon = "☀️", text = "Mejorá la iluminación o limpiá la cámara e intentá de nuevo")
        Spacer(Modifier.height(12.dp))
        TipCard(icon = "🤖", text = "El local puede no estar asociado a QLess todavía")

        Spacer(modifier = Modifier.weight(1f))

        // --- BOTONES ---
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Pimentón),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Escanear de nuevo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onManualInput,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text("Ingresar código manualmente", fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun TipCard(icon: String, text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 18.sp)
            Spacer(Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )
        }
    }
}

@Preview
@Composable
private fun QrNoReconocidoPreview() {
    QLessTheme {
        QrNoReconocidoScreen(onRetry = {}, onManualInput = {})
    }
}
