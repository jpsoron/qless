package com.qless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.qless.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Podés alternar darkTheme = true para verificar dark mode
            QLessTheme(darkTheme = false) {
                ThemePreviewScreen()
            }
        }
    }
}

@Composable
fun ThemePreviewScreen() {
    var darkMode by remember { mutableStateOf(false) }

    QLessTheme(darkTheme = darkMode) {
        Scaffold(
            topBar = {
                QLessTopBar(
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

                // ── Sección: Roles M3 principales ──────────────────────
                SectionTitle("Roles Material 3")

                M3RoleRow(
                    label = "primary / onPrimary",
                    bg = MaterialTheme.colorScheme.primary,
                    fg = MaterialTheme.colorScheme.onPrimary
                )
                M3RoleRow(
                    label = "primaryContainer / onPrimaryContainer",
                    bg = MaterialTheme.colorScheme.primaryContainer,
                    fg = MaterialTheme.colorScheme.onPrimaryContainer
                )
                M3RoleRow(
                    label = "secondary / onSecondary",
                    bg = MaterialTheme.colorScheme.secondary,
                    fg = MaterialTheme.colorScheme.onSecondary
                )
                M3RoleRow(
                    label = "secondaryContainer / onSecondaryContainer",
                    bg = MaterialTheme.colorScheme.secondaryContainer,
                    fg = MaterialTheme.colorScheme.onSecondaryContainer
                )
                M3RoleRow(
                    label = "tertiary / onTertiary",
                    bg = MaterialTheme.colorScheme.tertiary,
                    fg = MaterialTheme.colorScheme.onTertiary
                )
                M3RoleRow(
                    label = "surface / onSurface",
                    bg = MaterialTheme.colorScheme.surface,
                    fg = MaterialTheme.colorScheme.onSurface
                )
                M3RoleRow(
                    label = "surfaceVariant / onSurfaceVariant",
                    bg = MaterialTheme.colorScheme.surfaceVariant,
                    fg = MaterialTheme.colorScheme.onSurfaceVariant
                )
                M3RoleRow(
                    label = "error / onError",
                    bg = MaterialTheme.colorScheme.error,
                    fg = MaterialTheme.colorScheme.onError
                )
                M3RoleRow(
                    label = "errorContainer / onErrorContainer",
                    bg = MaterialTheme.colorScheme.errorContainer,
                    fg = MaterialTheme.colorScheme.onErrorContainer
                )

                HorizontalDivider()

                // ── Sección: Estados de pedido ──────────────────────────
                SectionTitle("Estados del pedido")

                OrderStatusRow("Disponible / Entregado", QLessStatusColors.disponible, QLessStatusColors.disponibleSurface)
                OrderStatusRow("En preparación",         QLessStatusColors.enPreparacion, QLessStatusColors.enPreparacionSurface)
                OrderStatusRow("En camino / Tracking",   QLessStatusColors.enCamino, QLessStatusColors.enCaminoSurface)
                OrderStatusRow("Agotado / Error",        QLessStatusColors.agotadoError, QLessStatusColors.agotadoSurface)

                HorizontalDivider()

                // ── Sección: Componentes M3 con el tema aplicado ────────
                SectionTitle("Componentes con el tema")

                // Botones
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}) { Text("Pedir") }
                    OutlinedButton(onClick = {}) { Text("Ver menú") }
                    TextButton(onClick = {}) { Text("Cancelar") }
                }

                // FAB
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FloatingActionButton(onClick = {}) {
                        Text("QR", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "FAB — usa primary",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Combo Big Classic",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Hamburguesa doble, papas y bebida",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\$4.500",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Chips de categoría
                SectionTitle("Chips de categoría")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Popular", "Combos", "Bebidas", "Postres").forEachIndexed { i, label ->
                        if (i == 0) {
                            FilterChip(selected = true, onClick = {}, label = { Text(label) })
                        } else {
                            FilterChip(selected = false, onClick = {}, label = { Text(label) })
                        }
                    }
                }

                // TextField
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

// ── Top bar de marca ─────────────────────────────────────────────────────────

@Composable
fun QLessTopBar(
    darkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Isotipo blanco
            Image(
                painter = painterResource(R.drawable.ic_qless_blanco),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Nombre de la app en Lora
            Text(
                text = "QLess",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Toggle dark mode
            Text(
                text = if (darkMode) "Dark" else "Light",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = darkMode,
                onCheckedChange = onDarkModeToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                ),
            )
        }
    }
}

// ── Composables auxiliares ───────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun M3RoleRow(label: String, bg: Color, fg: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = fg, style = MaterialTheme.typography.bodySmall)
        // Muestra el hex del color de fondo como referencia
        Text(
            text = "#%06X".format((bg.value shr 32 and 0xFFFFFFuL).toInt()),
            color = fg.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun OrderStatusRow(label: String, dotColor: Color, surfaceColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(surfaceColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(dotColor)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = dotColor,
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(dotColor)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Badge",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// ── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun ThemePreviewLight() {
    QLessTheme(darkTheme = false) { ThemePreviewScreen() }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun ThemePreviewDark() {
    QLessTheme(darkTheme = true) { ThemePreviewScreen() }
}