package com.qless.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.qless.ui.theme.QLessTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Test de composable *stateless* (no necesita ViewModel): verifica render y callback.
 */
class ActiveCartCardTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val cart = ActiveCartUi(
        localId = "l1",
        localNombre = "Big Pons",
        localEmoji = "🍔",
        itemCount = 2,
        totalAmount = 4500,
    )

    @Test
    fun muestra_local_y_etiqueta_de_carrito() {
        composeRule.setContent {
            QLessTheme { ActiveCartCard(cart = cart, onVer = {}) }
        }

        composeRule.onNodeWithText("CARRITO ACTIVO").assertIsDisplayed()
        composeRule.onNodeWithText("Big Pons", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("2 productos", substring = true).assertIsDisplayed()
    }

    @Test
    fun tocar_ver_dispara_callback() {
        var clicked = false
        composeRule.setContent {
            QLessTheme { ActiveCartCard(cart = cart, onVer = { clicked = true }) }
        }

        composeRule.onNodeWithText("Ver →").performClick()

        assertTrue(clicked)
    }
}
