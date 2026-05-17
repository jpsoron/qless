package com.qless.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

private data class NavItem(val icon: String, val label: String)

private val navItems = listOf(
    NavItem("🏠", "Inicio"),
    NavItem("📍", "Mis Locales"),
    NavItem("📄", "Mis Pedidos"),
    NavItem("⚙️", "Ajustes"),
)

@Composable
fun QLessBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ítems normales (antes del QR)
            navItems.take(2).forEachIndexed { index, item ->
                NavTabItem(
                    item = item,
                    isSelected = selectedTab == index,
                    onClick = { onTabSelected(index) }
                )
            }

            // Botón QR central
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(4) }
                    .offset(y = (-12).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Pimentón),
                    contentAlignment = Alignment.Center
                ) {
                    Text("▦", fontSize = 22.sp, color = Color.White)
                }
                Text(
                    "Escanear",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Madera.copy(alpha = 0.6f)
                )
            }

            // Ítems normales (después del QR)
            navItems.takeLast(2).forEachIndexed { index, item ->
                NavTabItem(
                    item = item,
                    isSelected = selectedTab == index + 2,
                    onClick = { onTabSelected(index + 2) }
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp, 28.dp)
                .then(
                    if (isSelected) Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Melocotón)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(item.icon, fontSize = 18.sp)
        }
        Text(
            item.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Pimentón else Madera.copy(alpha = 0.6f)
        )
    }
}