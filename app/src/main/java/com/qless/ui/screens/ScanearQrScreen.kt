package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanearQrScreen(
    onBack: () -> Unit,
    onQrDetected: (String) -> Unit
) {
    // Timer de 5 segundos: si no hay acción, envía "error" para ir a la pantalla de QrNoReconocido
    LaunchedEffect(Unit) {
        delay(5000)
        onQrDetected("error")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Escanear QR", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Volver", 
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF251A16)
                )
            )
        },
        containerColor = Color(0xFF120C0A) 
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Colocá el código QR del local\ndentro del recuadro",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- MARCO DE ESCANEO ---
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .border(4.dp, Color.White, RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .clickable { onQrDetected("success") }, // Click manual -> Éxito (Menú)
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(3.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                
                Text(
                    "TOCÁ AQUÍ PARA SIMULAR",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(56.dp))

            // --- BOTÓN DE SIMULACIÓN ---
            Button(
                onClick = { onQrDetected("success") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    "SIMULAR LECTURA EXITOSA", 
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "O espera 5 segundos para simular un error",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
        }
    }
}
