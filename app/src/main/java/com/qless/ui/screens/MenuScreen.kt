package com.qless.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.data.Local
import com.qless.data.MenuItem
import com.qless.ui.viewmodel.CartViewModel
import com.qless.ui.viewmodel.MenuViewModel
import com.qless.ui.theme.*

@Composable
fun MenuScreen(
    cartViewModel: CartViewModel,
    menuViewModel: MenuViewModel,
    local: Local? = null,
    isDarkTheme: Boolean = false,
    onViewCart: () -> Unit,
    onBack: () -> Unit,
) {
    val cartUiState by cartViewModel.uiState.collectAsState()
    val menuUiState by menuViewModel.uiState.collectAsState()
    val cartCount = cartUiState.items.sumOf { it.quantity }
    val cartTotal = cartUiState.items.sumOf { it.unitPrice * it.quantity }
    val selectedCategory = menuUiState.selectedCategory
    val isLoading = menuUiState.isLoading
    val items = menuUiState.items
    val categories = buildList {
        if (items.any { it.esPopular }) add("🔥 Popular")
        addAll(items.map { it.categoria }.distinct())
    }
    val filteredItems = when {
        selectedCategory == "🔥 Popular" -> items.filter { it.esPopular }
        selectedCategory.isEmpty() -> items
        else -> items.filter { it.categoria == selectedCategory }
    }
    val shimmerBrush = shimmerBrush()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (cartCount > 0) 100.dp else 16.dp)
        ) {
            if (isLoading) {
                // Skeleton hero
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(shimmerBrush)
                            .statusBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.7f))
                                    .clickable { onBack() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                // Skeleton category bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(5) {
                            Box(
                                modifier = Modifier
                                    .size(width = 70.dp, height = 30.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(shimmerBrush)
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer)
                }
                // Skeleton section header
                item {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .size(100.dp, 18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush)
                    )
                }
                // Skeleton menu item cards
                repeat(4) {
                    item { SkeletonMenuItemCard(shimmerBrush) }
                }
            } else {
                // Hero
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDarkTheme) Madera else MaterialTheme.colorScheme.primaryContainer)
                            .statusBarsPadding()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.7f))
                                        .clickable { onBack() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurface)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.7f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("···", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(local?.emoji ?: "🍽️", fontSize = 56.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(local?.nombre ?: "", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                Text("${local?.categoria ?: ""} · ${local?.barrio ?: ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    local?.rating?.let { Chip("⭐ $it", isDarkTheme) }
                                    local?.tiempoEntrega?.let { Chip("⏱ $it", isDarkTheme) }
                                    if (local?.tienePromo == true) {
                                        Surface(shape = RoundedCornerShape(999.dp), color = if (isDarkTheme) Albahaca else MaterialTheme.colorScheme.primary) {
                                            Text("10% OFF 1.er pedido", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Category bar
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (cat == selectedCategory) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                modifier = Modifier.clickable { menuViewModel.selectCategory(cat) }
                            ) {
                                Text(
                                    cat,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (cat == selectedCategory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer)
                }

                if (selectedCategory.isNotEmpty()) {
                    item {
                        Text(
                            selectedCategory,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                items(filteredItems) { item ->
                    MenuItemCard(
                        item = item,
                        quantity = cartViewModel.getQuantity(item.nombre),
                        onAdd = { cartViewModel.addItem(item.emoji, item.nombre, item.descripcion, item.precio, local?.id ?: "") },
                        onRemove = { cartViewModel.removeItem(item.nombre) }
                    )
                }

                if (filteredItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Sin ítems en esta categoría",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Carrito flotante
        if (cartCount > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .clickable { onViewCart() },
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) Pimentón else MaterialTheme.colorScheme.primary,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.2f)) {
                        Text(
                            "$cartCount",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Ver carrito", fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.weight(1f))
                    Text("$${"%,d".format(cartTotal)}", fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 400f, 0f),
        end = Offset(translateAnim, 0f)
    )
}

@Composable
private fun SkeletonMenuItemCard(brush: Brush) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.fillMaxWidth(0.6f).height(14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Box(Modifier.fillMaxWidth(0.9f).height(10.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Box(Modifier.fillMaxWidth(0.35f).height(10.dp).clip(RoundedCornerShape(4.dp)).background(brush))
            }
        }
    }
}

@Composable
private fun Chip(text: String, isDarkTheme: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (isDarkTheme) PimentónDark else Color.White
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MenuItemCard(
    item: MenuItem,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 30.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (item.esPopular) {
                    Surface(shape = RoundedCornerShape(999.dp), color = QLessStatusColors.enPreparacion) {
                        Text("Más pedido", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Text(item.nombre, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                Text(item.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$${"%,d".format(item.precio)}",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (quantity > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .clickable { onRemove() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Remove,
                                    contentDescription = "Quitar",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text("$quantity", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .clickable { onAdd() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Agregar",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { onAdd() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Agregar",
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

