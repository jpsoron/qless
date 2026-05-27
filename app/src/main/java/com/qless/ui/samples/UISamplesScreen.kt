package com.qless.ui.samples

/**
 * UISamplesScreen
 *
 * Pantalla de referencia del Design System. No forma parte del flujo de la app.
 * Para acceder durante desarrollo, agregar una ruta temporaria en AppNavigation
 * o lanzarla directamente desde un test/preview.
 *
 * El contenido original está en MainActivity.kt (historial de git antes del refactor).
 * Se preserva en este archivo para referencia del equipo.
 */

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.R
import com.qless.ui.theme.*

@Composable
fun UISamplesScreen() {
    var darkMode by remember { mutableStateOf(false) }

    QLessTheme(darkTheme = darkMode) {
        Scaffold(
            topBar = {
                UISamplesTopBar(
                    darkMode = darkMode,
                    onDarkModeToggle = { darkMode = it }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SectionTitle("Roles Material 3")

                M3RoleRow("primary / onPrimary", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
                M3RoleRow("primaryContainer / onPrimaryContainer", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                M3RoleRow("secondary / onSecondary", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
                M3RoleRow("secondaryContainer / onSecondaryContainer", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
                M3RoleRow("tertiary / onTertiary", MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
                M3RoleRow("surface / onSurface", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
                M3RoleRow("error / onError", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError)

                HorizontalDivider()

                SectionTitle("Estados del pedido")

                OrderStatusRow("Disponible / Entregado", QLessStatusColors.disponible, QLessStatusColors.disponibleSurface)
                OrderStatusRow("En preparación", QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
                OrderStatusRow("En camino / Tracking", QLessStatusColors.enCamino, QLessStatusColors.enCaminoSurface)
                OrderStatusRow("Agotado / Error", QLessStatusColors.agotadoError, QLessStatusColors.agotadoSurface)

                HorizontalDivider()

                SectionTitle("Componentes con el tema")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}) { Text("Pedir") }
                    OutlinedButton(onClick = {}) { Text("Ver menú") }
                    TextButton(onClick = {}) { Text("Cancelar") }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Combo Big Classic", style = MaterialTheme.typography.titleMedium)
                        Text("Hamburguesa doble, papas y bebida", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("\$4.500", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                SectionTitle("Chips de categoría")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Popular", "Combos", "Bebidas", "Postres").forEachIndexed { i, label ->
                        if (i == 0) FilterChip(selected = true, onClick = {}, label = { Text(label) })
                        else FilterChip(selected = false, onClick = {}, label = { Text(label) })
                    }
                }

                var texto by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Notas para el local") },
                    placeholder = { Text("Sin cebolla, bien cocido...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun UISamplesTopBar(darkMode: Boolean, onDarkModeToggle: (Boolean) -> Unit) {
    Surface(color = MaterialTheme.colorScheme.primary, shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painter = painterResource(R.drawable.ic_qless_blanco), contentDescription = null, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("UI Samples", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(if (darkMode) "Dark" else "Light", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = darkMode, onCheckedChange = onDarkModeToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                )
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
}

@Composable
private fun M3RoleRow(label: String, bg: Color, fg: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(bg).padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = fg, style = MaterialTheme.typography.bodySmall)
        Text(text = "#%06X".format((bg.value shr 32 and 0xFFFFFFuL).toInt()), color = fg.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun OrderStatusRow(label: String, dotColor: Color, surfaceColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(surfaceColor).padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(6.dp)).background(dotColor))
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = dotColor, fontWeight = FontWeight.SemiBold)
        }
        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(dotColor).padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text("Badge", color = Color.White, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(showBackground = true, name = "UI Samples Light")
@Composable
private fun UISamplesPreview() {
    QLessTheme(darkTheme = false) { UISamplesScreen() }
}