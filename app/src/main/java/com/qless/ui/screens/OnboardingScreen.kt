package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.R
import com.qless.ui.theme.*
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val emoji: String,
    val eyebrow: String,
    val title: String,
    val titleHighlight: String,
    val body: String,
    val bgColor: Color,
    val illustrationBg: Color,
)

private val pages = listOf(
    OnboardingPage(
        emoji = "🛒",
        eyebrow = "01 — PEDÍ",
        title = "Tu pedido, desde tu ",
        titleHighlight = "celular",
        body = "Elegí del menú, personalizá tu pedido y confirmalo sin moverte de tu lugar.",
        bgColor = CremaCálida,
        illustrationBg = Pimentón,
    ),
    OnboardingPage(
        emoji = "🔔",
        eyebrow = "02 — SEGUÍ",
        title = "Tu beeper ",
        titleHighlight = "virtual",
        body = "Seguí el estado de tu pedido en tiempo real. Te avisamos cuando esté listo, sin que tengas que preguntar.",
        bgColor = Espresso,
        illustrationBg = Azafrán,
    ),
    OnboardingPage(
        emoji = "🎟️",
        eyebrow = "03 — AHORRÁ",
        title = "Empezá con ",
        titleHighlight = "10% de descuento",
        body = "Para activarlo, creá tu cuenta. El descuento se aplica automáticamente en tu primer pedido.",
        bgColor = CremaCálida,
        illustrationBg = Pimentón,
    ),
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPage(page = pages[page])
        }

        // Controles inferiores
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 40.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Saltar"
            TextButton(onClick = onFinish) {
                Text(
                    text = "Saltar",
                    color = if (currentPage == 1) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(pages.size) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == currentPage) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (i == currentPage) MaterialTheme.colorScheme.primary
                                else if (currentPage == 1) Color.White.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                            )
                    )
                }
            }

            // Botón avanzar / finalizar
            FilledIconButton(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier.size(52.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (currentPage < pages.size - 1) "→" else "✓",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun OnboardingPage(page: OnboardingPage) {
    val isDark = page.bgColor == Espresso
    val textColor = if (isDark) Color.White else MaterialTheme.colorScheme.onBackground
    val bodyColor = if (isDark) Color.White.copy(alpha = 0.65f) else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(page.bgColor)
    ) {
        // Ilustración superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.50f)
                .background(
                    Brush.verticalGradient(
                        listOf(page.illustrationBg, page.illustrationBg.copy(alpha = 0.85f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (page.eyebrow.contains("03")) {
                // Slide de descuento: ticket visual
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 32.dp, vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "TU PRIMER PEDIDO",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                "10% OFF",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Descuento automático",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "PRIMER10",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            } else if (page.eyebrow.contains("02")) {
                // Slide de tracking: timer circular
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
                        CircularProgressIndicator(
                            progress = { 0.6f },
                            modifier = Modifier.fillMaxSize(),
                            color = QLessStatusColors.enPreparacion,
                            trackColor = Color.White.copy(alpha = 0.2f),
                            strokeWidth = 8.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                            Text("12", fontSize = 36.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text("min", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = QLessStatusColors.enPreparacion.copy(alpha = 0.25f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("¡Tu pedido está listo para retirar!", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                // Slide de pedido: mini card del menú
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_qless_espresso),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "QLess",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        listOf(
                            Triple("🍔", "Combo Big Classic", "$4.500"),
                            Triple("🍟", "Papas Grandes", "$1.200"),
                            Triple("🥤", "Gaseosa 500ml", "$700"),
                        ).forEach { (emoji, name, price) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(emoji, fontSize = 20.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        "+",
                                        modifier = Modifier.padding(6.dp),
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Confirmar pedido · $6.400", color = Color.White)
                        }
                    }
                }
            }
        }

        // Texto
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 32.dp)
        ) {
            Text(
                text = page.eyebrow,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = page.title + page.titleHighlight,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                lineHeight = 34.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = page.body,
                style = MaterialTheme.typography.bodyLarge,
                color = bodyColor,
                lineHeight = 24.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreview() {
    QLessTheme { OnboardingScreen(onFinish = {}) }
}