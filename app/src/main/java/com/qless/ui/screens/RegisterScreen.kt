package com.qless.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.ui.viewmodel.AuthNavEvent
import com.qless.ui.viewmodel.AuthViewModel
import com.qless.ui.theme.*

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    onNavigateToGoogleLogin: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.navEvent.collect { event ->
            if (event is AuthNavEvent.RegisterSuccess) onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(999.dp))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "Completá tus datos para comenzar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        QLessTextField(label = "Nombre completo", value = name, onValueChange = { name = it }, placeholder = "Ej: María González")
        Spacer(Modifier.height(14.dp))
        QLessTextField(label = "Correo electrónico", value = email, onValueChange = { email = it }, placeholder = "nombre@ejemplo.com", keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(14.dp))
        QLessTextField(label = "Contraseña", value = password, onValueChange = { password = it }, placeholder = "Mínimo 8 caracteres", isPassword = true)
        Spacer(Modifier.height(14.dp))
        QLessTextField(label = "Confirmar contraseña", value = confirmPassword, onValueChange = { confirmPassword = it }, placeholder = "Repetí tu contraseña", isPassword = true)

        Spacer(Modifier.height(8.dp))

        // Hint
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Usá al menos 8 caracteres, una mayúscula y un número",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Error de registro
        if (uiState.registerError != null) {
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
            ) {
                Text(
                    text = uiState.registerError ?: "",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { authViewModel.register(name, email, password, confirmPassword) },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Crear cuenta", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primaryContainer)
            Text(" o ", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primaryContainer)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateToGoogleLogin,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text("G", fontWeight = FontWeight.SemiBold, color = Color(0xFF4285F4), fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Text("Registrarme con Google", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("¿Ya tenés cuenta? ", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                Text("Iniciar sesión", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Al registrarte, aceptás los Términos y condiciones",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun QLessTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
) {
    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        singleLine = true
    )
}

