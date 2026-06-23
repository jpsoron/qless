package com.qless.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.qless.ui.theme.QLessTheme

private data class BackOfficeNavItem(val icon: ImageVector, val label: String)

private val backOfficeNavItems = listOf(
    BackOfficeNavItem(Icons.AutoMirrored.Filled.List, "Pedidos en curso"),
    BackOfficeNavItem(Icons.Outlined.DateRange, "Historial"),
    BackOfficeNavItem(Icons.Default.Settings, "Ajustes"),
)

@Composable
fun BackOfficeBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    val isDark = QLessTheme.isDark
    val navBackground   = if (isDark) Color(0xFF9E7A5A) else MaterialTheme.colorScheme.surface
    val selectedColor   = if (isDark) Color.White else MaterialTheme.colorScheme.primary
    val unselectedColor = if (isDark) Color.White.copy(alpha = 0.50f)
                          else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

    Surface(
        color = navBackground,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(100f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            backOfficeNavItems.forEachIndexed { index, item ->
                BackOfficeNavTabItem(
                    icon = item.icon,
                    label = item.label,
                    isSelected = selectedTab == index,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    modifier = Modifier.weight(1f),
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun BackOfficeNavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) selectedColor else unselectedColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            label,
            fontSize = 10.sp,
            color = if (isSelected) selectedColor else unselectedColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
