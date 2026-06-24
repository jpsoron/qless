package com.qless.ui.screens.clients

import androidx.compose.foundation.BorderStroke
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
import com.qless.ui.theme.QLessTheme
import com.qless.ui.viewmodel.AuthNavEvent
import com.qless.ui.viewmodel.AuthViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AjustesScreen(
    authViewModel: AuthViewModel,
    userName: String,
    userEmail: String,
    isDarkTheme: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
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
    val authState by authViewModel.uiState.collectAsState()
    var gpsEnabled by remember { mutableStateOf(true) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var profileName by remember(userName) { mutableStateOf(userName.ifBlank { "Usuario" }) }
    var profileEmail by remember(userEmail) { mutableStateOf(userEmail) }

    // Cierra el bottom sheet cuando el perfil se guardó OK.
    LaunchedEffect(Unit) {
        authViewModel.navEvent.collect { event ->
            if (event is AuthNavEvent.ProfileUpdated) showProfileSheet = false
        }
    }

    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProfileSheet = false; authViewModel.clearProfileError() },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    "Mi perfil",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Datos personales y cuenta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = profileName,
                    onValueChange = { profileName = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = profileEmail,
                    onValueChange = { profileEmail = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                if (authState.profileError != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        authState.profileError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = { authViewModel.updateProfile(profileName, profileEmail) },
                    enabled = !authState.isSavingProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (authState.isSavingProfile) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Guardar cambios", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }

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
        containerColor = MaterialTheme.colorScheme.background
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
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(24.dp))

            // Tarjeta de Perfil
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) MaterialTheme.colorScheme.surfaceVariant
                                     else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
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
                        onClick = { showProfileSheet = true },
                        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            SettingsSection("CUENTA") {
                SettingsItem("👤", "Mi perfil", "Datos personales y cuenta", onClick = { showProfileSheet = true })
                SettingsItem(
                    icon = "💳",
                    title = "Métodos de pago",
                    description = "Pago en efectivo en el local",
                    onClick = onNavigateToMetodosDePago
                )
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
                SettingsToggleItem(
                    icon = "🌙",
                    title = "Modo oscuro",
                    description = "Cambiar la apariencia de la app",
                    checked = isDarkTheme,
                    onCheckedChange = onDarkModeToggle
                )
                SettingsToggleItem(
                    icon = "📡",
                    title = "Detección por GPS",
                    description = "Sugerir locales cercanos",
                    checked = gpsEnabled,
                    onCheckedChange = { gpsEnabled = it }
                )
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
                color = MaterialTheme.colorScheme.surfaceVariant, // Mismo color de fondo que las otras secciones
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Cerrar sesión",
                        color = MaterialTheme.colorScheme.error,
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
        Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp, bottom = 12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
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
            Text(title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        if (badge != null) { badge(); Spacer(Modifier.width(12.dp)) }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
    }
}

@Composable
private fun SettingsToggleItem(
    icon: String,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = Color.White) {
            Box(contentAlignment = Alignment.Center) { Text(icon, fontSize = 20.sp) }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
    }
}

@Preview(showBackground = true)
@Composable
@Suppress("ViewModelConstructorInComposable") // Solo preview; VM construido a mano a propósito.
private fun AjustesPreview() {
    QLessTheme {
        AjustesScreen(
            authViewModel = AuthViewModel(),
            userName = "María González",
            userEmail = "maria@email.com",
            isDarkTheme = false,
            onDarkModeToggle = {},
            onNavigateToInicio = {},
            onNavigateToMisLocales = {},
            onNavigateToScanQr = {},
            onNavigateToMisPedidos = {},
            onNavigateToNotificaciones = {},
            onNavigateToMetodosDePago = {},
            onNavigateToEliminarCuenta = {},
            onLogout = {},
        )
    }
}
