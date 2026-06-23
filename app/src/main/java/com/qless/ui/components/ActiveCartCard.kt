package com.qless.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
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
import java.text.NumberFormat
import java.util.Locale

/**
 * Descriptor del carrito activo que se muestra en las pantallas de inicio,
 * mis locales y mis pedidos. Se arma en AppNavigation resolviendo el localId
 * del carrito contra la lista de locales cargada.
 */
data class ActiveCartUi(
    val localId: String,
    val localNombre: String,
    val localEmoji: String,
    val itemCount: Int,
    val totalAmount: Int,
)

/**
 * Card que avisa que hay un carrito cargado en un local y permite volver a su menú.
 * Diseño cohesivo con el banner de "pedido recibido": superficie clara con acento primario.
 */
@Composable
fun ActiveCartCard(
    cart: ActiveCartUi,
    onVer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val total = NumberFormat.getNumberInstance(Locale("es", "AR")).format(cart.totalAmount)
    val itemsLabel = if (cart.itemCount == 1) "1 producto" else "${cart.itemCount} productos"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onVer() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "CARRITO ACTIVO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.8.sp
                )
                Text(
                    "${cart.localEmoji} ${cart.localNombre}".trim(),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "$itemsLabel · $$total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                )
            }
            Text(
                "Ver →",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}
