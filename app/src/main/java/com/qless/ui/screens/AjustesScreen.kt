package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
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
fun AjustesScreen(
    userName: String,
    userEmail: String,
    onNavigateToInicio: () -> Unit,
    onNavigateToMisLocales: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToMisPedidos: () -> Unit,
    onNavigateToNotificaciones: () -> Unit,
    onNavigateToMetodosDePago: () -> Unit,
    onNavigateToEliminarCuenta: () -> Unit,
    onLogout: () -> Unit,
) {
    val initial = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Scaffold(
        bottomBar = {
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
        },
        containerColor = CremaCálida
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Habilita el desplazamiento hacia abajo
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp).statusBarsPadding())

            Text(
                "Ajustes",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = Espresso
            )

            Spacer(Modifier.height(24.dp))

            // Tarjeta de Perfil
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Espresso)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Pimentón),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initial, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(userName.ifBlank { "Usuario" }, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        Text(userEmail.ifBlank { "" }, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(99.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF4CAF50), CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text("Cuenta activa · Google", color = Color.White, fontSize = 11.sp)
                        }
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            SettingsSection("CUENTA") {
                SettingsItem("👤", "Mi perfil", "Datos personales y cuenta")
                SettingsItem(
                    icon = "💳",
                    title = "Métodos de pago",
                    description = "Tarjetas y billeteras",
                    onClick = onNavigateToMetodosDePago
                ) {
                    Text(
                        "VISA •••• 4242", color = Color(0xFF4285F4), fontWeight = FontWeight.SemiBold, fontSize = 11.sp,
                        modifier = Modifier.background(Color(0xFF4285F4).copy(alpha = 0.1f), RoundedCornerShape(99.dp)).padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                SettingsItem(
                    icon = "🗑️",
                    title = "Eliminar cuenta",
                    description = "Borrar tus datos permanentemente",
                    onClick = onNavigateToEliminarCuenta
                )
            }

            Spacer(Modifier.height(20.dp))

            SettingsSection("PREFERENCIAS") {
                SettingsItem(
                    icon = "🔔",
                    title = "Notificaciones",
                    description = "Pedidos, promos y novedades",
                    onClick = onNavigateToNotificaciones
                ) {
                    Text(
                        "3 activas", color = Color(0xFF1A7A4A), fontWeight = FontWeight.SemiBold, fontSize = 11.sp,
                        modifier = Modifier.background(Color(0xFF1A7A4A).copy(alpha = 0.1f), RoundedCornerShape(99.dp)).padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                SettingsToggleItem("🌙", "Modo oscuro", "Seguir el sistema", false)
                SettingsToggleItem("📡", "Detección por GPS", "Sugerir locales cercanos", true)
            }

            Spacer(Modifier.height(20.dp))

            SettingsSection("SOPORTE") {
                SettingsItem("❓", "Ayuda y preguntas frecuentes", "Soporte y guías de uso")
                SettingsItem("📄", "Términos y condiciones", "Legal y privacidad")
            }

            Spacer(Modifier.height(32.dp))

            // --- SECCIÓN CERRAR SESIÓN ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() },
                shape = RoundedCornerShape(24.dp),
                color = Mantequilla, // Mismo color de fondo que las otras secciones
                border = androidx.compose.foundation.BorderStroke(1.dp, Borgoña.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Borgoña
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Cerrar sesión",
                        color = Borgoña,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(48.dp)) // Espacio extra al final para asegurar el scroll completo
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelMedium, color = Madera.copy(alpha = 0.6f), letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp, bottom = 12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = Mantequilla,
            border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) { content() }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: String,
    title: String,
    description: String,
    onClick: () -> Unit = {},
    badge: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = Color.White) {
            Box(contentAlignment = Alignment.Center) { Text(icon, fontSize = 20.sp) }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = Espresso, fontSize = 16.sp)
            Text(description, color = Madera, fontSize = 13.sp)
        }
        if (badge != null) { badge(); Spacer(Modifier.width(12.dp)) }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Madera.copy(alpha = 0.4f))
    }
}

@Composable
private fun SettingsToggleItem(icon: String, title: String, description: String, checked: Boolean) {
    var isChecked by remember { mutableStateOf(checked) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = Color.White) {
            Box(contentAlignment = Alignment.Center) { Text(icon, fontSize = 20.sp) }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = Espresso, fontSize = 16.sp)
            Text(description, color = Madera, fontSize = 13.sp)
        }
        Switch(checked = isChecked, onCheckedChange = { isChecked = it }, colors = SwitchDefaults.colors(checkedTrackColor = Pimentón))
    }
}

@Preview(showBackground = true)
@Composable
private fun AjustesPreview() {
    QLessTheme {
        AjustesScreen("María González", "maria@email.com", {}, {}, {}, {}, {}, {}, {}, {})
    }
}
