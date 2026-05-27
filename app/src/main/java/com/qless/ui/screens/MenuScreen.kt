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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*
import kotlinx.coroutines.delay

private data class MenuItem(
    val emoji: String,
    val name: String,
    val description: String,
    val price: Int,
    val isPopular: Boolean = false,
    val category: String,
)

private val menuItems = listOf(
    MenuItem("🍔", "Combo Big Classic", "Hamburguesa doble, papas y bebida a elección", 4500, true, "Popular"),
    MenuItem("🧀", "Combo Doble Cheddar", "2 hamburguesas con cheddar, papas y bebida", 5800, false, "Popular"),
    MenuItem("🥩", "Hamburguesa Simple", "Carne, lechuga, tomate y aderezo de la casa", 3200, false, "Hamburguesas"),
    MenuItem("🍟", "Papas Fritas Grandes", "Crocantes con salsa a elección", 1200, false, "Papas"),
    MenuItem("🥤", "Gaseosa 500 ml", "Coca-Cola, Sprite o Fanta", 700, false, "Bebidas"),
)

private val categories = listOf("🔥 Popular", "Combos", "Hamburguesas", "Papas", "Bebidas", "Postres")

@Composable
fun MenuScreen(
    onViewCart: () -> Unit,
    onBack: () -> Unit,
) {
    val quantities = remember { mutableStateMapOf<String, Int>() }
    val cartCount = quantities.values.sum()
    val cartTotal = menuItems.sumOf { (quantities[it.name] ?: 0) * it.price }
    var selectedCategory by remember { mutableStateOf("🔥 Popular") }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(1500L); isLoading = false }
    val shimmerBrush = shimmerBrush()

    Box(modifier = Modifier.fillMaxSize().background(CremaCálida)) {
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
                                    tint = Espresso
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
                            .background(CremaCálida)
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
                    HorizontalDivider(color = Melocotón)
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
                            .background(Melocotón)
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
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", modifier = Modifier.size(18.dp), tint = Espresso)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.7f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("···", color = Espresso, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🍔", fontSize = 56.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Big Pons", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold, color = Espresso)
                                Text("Hamburguesas & Snacks · San Isidro", style = MaterialTheme.typography.bodySmall, color = Madera)
                                Spacer(Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Chip("⭐ 4.8")
                                    Chip("⏱ 15–25 min")
                                    Chip("Mín. $1.500")
                                    Surface(shape = RoundedCornerShape(999.dp), color = Pimentón) {
                                        Text("10% OFF 1.er pedido", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
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
                            .background(CremaCálida)
                            .padding(vertical = 10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (cat == selectedCategory) Melocotón else Color.Transparent,
                                modifier = Modifier.clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    cat,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (cat == selectedCategory) Pimentón else Madera
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = Melocotón)
                }

                // Sección Popular
                item {
                    Text(
                        "🔥 Popular",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Espresso
                    )
                }

                items(menuItems.filter { it.isPopular }) { item ->
                    MenuItemCard(
                        item = item,
                        quantity = quantities[item.name] ?: 0,
                        onAdd = { quantities[item.name] = (quantities[item.name] ?: 0) + 1 },
                        onRemove = { if ((quantities[item.name] ?: 0) > 0) quantities[item.name] = quantities[item.name]!! - 1 }
                    )
                }

                item {
                    Text(
                        "🍔 Hamburguesas",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Espresso
                    )
                }

                items(menuItems.filter { !it.isPopular }) { item ->
                    MenuItemCard(
                        item = item,
                        quantity = quantities[item.name] ?: 0,
                        onAdd = { quantities[item.name] = (quantities[item.name] ?: 0) + 1 },
                        onRemove = { if ((quantities[item.name] ?: 0) > 0) quantities[item.name] = quantities[item.name]!! - 1 }
                    )
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
                color = Pimentón,
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
        Melocotón.copy(alpha = 0.9f),
        Mantequilla,
        Melocotón.copy(alpha = 0.9f),
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
        color = Mantequilla,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
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
private fun Chip(text: String) {
    Surface(shape = RoundedCornerShape(999.dp), color = Color.White) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Espresso)
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
        color = Mantequilla,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Melocotón),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 30.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (item.isPopular) {
                    Surface(shape = RoundedCornerShape(999.dp), color = Azafrán) {
                        Text("Más pedido", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Text(item.name, fontWeight = FontWeight.SemiBold, color = Espresso, style = MaterialTheme.typography.bodyMedium)
                Text(item.description, style = MaterialTheme.typography.bodySmall, color = Madera, lineHeight = 16.sp)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$${"%,d".format(item.price)}",
                        fontWeight = FontWeight.SemiBold,
                        color = Pimentón,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (quantity > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Melocotón)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("−", color = Pimentón, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, modifier = Modifier.clickable { onRemove() })
                            Text("$quantity", fontWeight = FontWeight.SemiBold, color = Espresso)
                            Text("+", color = Pimentón, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, modifier = Modifier.clickable { onAdd() })
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Pimentón)
                                .clickable { onAdd() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Light)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuPreview() {
    QLessTheme { MenuScreen(onViewCart = {}, onBack = {}) }
}
