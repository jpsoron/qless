package com.qless.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.qless.ui.theme.*

private data class NavItem(val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem(Icons.Default.Home, "Inicio"),
    NavItem(Icons.Default.LocationOn, "Mis Locales"),
    NavItem(Icons.Default.ReceiptLong, "Mis Pedidos"),
    NavItem(Icons.Default.Settings, "Ajustes"),
)

@Composable
fun QLessBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .zIndex(100f)
    ) {
        // White background — height matches only the normal tab items, not the QR button
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.take(2).forEachIndexed { index, item ->
                    NavTabItem(
                        item = item,
                        isSelected = selectedTab == index,
                        onClick = { onTabSelected(index) }
                    )
                }
                // Reserve space so the row layout matches the QR button's width
                Spacer(Modifier.width(72.dp))
                navItems.takeLast(2).forEachIndexed { index, item ->
                    NavTabItem(
                        item = item,
                        isSelected = selectedTab == index + 2,
                        onClick = { onTabSelected(index + 2) }
                    )
                }
            }
        }

        // QR button — bottom-aligned with the Box; protrudes above the white Surface
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable { onTabSelected(4) }
                .navigationBarsPadding()
                .padding(bottom = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Pimentón, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Escanear QR",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                "Escanear",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Madera.copy(alpha = 0.6f)
            )
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
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isSelected) Pimentón else Madera.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            item.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Pimentón else Madera.copy(alpha = 0.6f)
        )
    }
}
