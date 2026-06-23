package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.viewmodel.AuthViewModel
import com.qless.ui.components.QLessBottomNav
import com.qless.ui.theme.*

@Composable
fun CerrarSesionScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onConfirmLogout: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToMisLocales: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
) {
    val uiState by authViewModel.uiState.collectAsState()
    val userName = uiState.currentUserName
    val userEmail = uiState.currentUserEmail
    val initial = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.zIndex(100f).graphicsLayer(clip = false)) {
                QLessBottomNav(
                    selectedTab = 3,
                    onTabSelected = { tab ->
                        when (tab) {
                            0 -> onNavigateToInicio()
                            1 -> onNavigateToMisLocales()
                            2 -> onNavigateToMisPedidos()
                            4 -> onNavigateToScanQr()
                        }
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(12.dp).statusBarsPadding())

            // Back button and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    "Cerrar sesión",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Vas a salir de tu cuenta en este dispositivo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Warning Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("!", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "Se va a cerrar tu sesión actual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Vas a volver a la pantalla de ingreso. Tus pedidos y métodos guardados seguirán asociados a tu cuenta.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // User Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initial, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            userName.ifBlank { "Usuario" },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            userEmail.ifBlank { "" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // Action Buttons
            Button(
                onClick = {
                    authViewModel.logout()
                    onConfirmLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Cerrar sesión", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}

