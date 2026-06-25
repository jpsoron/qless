@file:OptIn(ExperimentalMaterial3Api::class)

package com.qless.ui.screens.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.viewmodel.PasswordResetViewModel

/**
 * Paso 1 del reset por link: la persona ingresa su correo y se le manda el mail
 * con el link de recuperación. No revela si el correo existe (mensaje neutro).
 */
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: PasswordResetViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Recuperar contraseña", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.emailSent) {
                EmailSentContent(email = email, onBack = onBack)
                return@Column
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "¿Olvidaste tu contraseña?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Ingresá tu correo y te enviamos un enlace para crear una nueva.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Correo electrónico",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (uiState.error != null) viewModel.clearError()
                },
                placeholder = { Text("nombre@ejemplo.com", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.error != null,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                ),
                singleLine = true
            )

            if (uiState.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.sendResetEmail(email) },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Enviar enlace", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun EmailSentContent(email: String, onBack: () -> Unit) {
    Spacer(Modifier.height(48.dp))
    Surface(
        modifier = Modifier.size(96.dp),
        shape = androidx.compose.foundation.shape.CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                Icons.Outlined.MarkEmailRead,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
    }
    Spacer(Modifier.height(24.dp))
    Text(
        "Revisá tu correo",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(12.dp))
    Text(
        "Si $email está registrado, te enviamos un enlace para cambiar tu contraseña. " +
            "Abrilo desde este dispositivo. Revisá también el spam.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
    Spacer(Modifier.height(32.dp))
    OutlinedButton(
        onClick = onBack,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("Volver a iniciar sesión", fontWeight = FontWeight.SemiBold)
    }
}
