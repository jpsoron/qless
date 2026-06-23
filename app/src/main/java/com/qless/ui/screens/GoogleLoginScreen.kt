package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.theme.*

@Composable
fun GoogleLoginScreen(
    onBack: () -> Unit,
    onContinueWithGoogle: () -> Unit,
    onUseEmail: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
    ) {
        // Botón de Volver
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 12.dp)
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Título y Subtítulo
        Text(
            "Conectar con Google",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Usá tu cuenta de Google para registrarte de forma segura y rápida",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(54.dp))

        // Círculo Central con la G
        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.CenterHorizontally)
                .background(Color.White, CircleShape)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "G",
                        color = Color(0xFF4285F4),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Puntos de colores Google
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Dot(Color(0xFF4285F4))
            Dot(Color(0xFFEA4335))
            Dot(Color(0xFFFBBC05))
            Dot(Color(0xFF34A853))
        }

        Spacer(Modifier.height(54.dp))

        // Lista de beneficios
        Text(
            "Al conectar con Google podés:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(20.dp))
        BenefitItem("Iniciar sesión sin recordar contraseñas")
        BenefitItem("Acceder de forma segura con tu cuenta Google")
        BenefitItem("Sincronizar tu perfil automáticamente")

        Spacer(Modifier.height(36.dp))

        // Caja de información
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Solo se accede a tu nombre y email. Nunca a contraseñas.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(28.dp))

        // Botón Google
        OutlinedButton(
            onClick = onContinueWithGoogle,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
        ) {
            Surface(
                modifier = Modifier.size(24.dp),
                shape = CircleShape,
                color = Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "G",
                        color = Color(0xFF4285F4),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Text(
                "Continuar con Google",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )
        }

        Spacer(Modifier.height(28.dp))

        // Enlace alternativo
        TextButton(
            onClick = onUseEmail,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                "Usar correo y contraseña en cambio",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(20.dp))

        // Footer Registro/Login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "¿Ya tenés cuenta? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp
            )
            TextButton(
                onClick = onNavigateToLogin,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Iniciar sesión",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(color, CircleShape)
    )
}

@Composable
private fun BenefitItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GoogleLoginPreview() {
    QLessTheme {
        GoogleLoginScreen(
            onBack = {},
            onContinueWithGoogle = {},
            onUseEmail = {},
            onNavigateToLogin = {}
        )
    }
}
