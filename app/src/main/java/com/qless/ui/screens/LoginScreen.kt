package com.qless.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.R
import com.qless.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CremaCálida)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        // Logo
        Image(
            painter = painterResource(R.drawable.ic_qless_pimenton),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "QLess",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Pimentón,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            "Tu comida, sin filas.",
            style = MaterialTheme.typography.bodyMedium,
            color = Madera
        )

        Spacer(Modifier.height(36.dp))

        Text(
            "Bienvenido de nuevo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Espresso,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // Email
        Text(
            "Correo electrónico",
            style = MaterialTheme.typography.labelMedium,
            color = Espresso,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("nombre@ejemplo.com", color = Madera.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Melocotón,
                focusedBorderColor = Pimentón,
                unfocusedContainerColor = Mantequilla,
                focusedContainerColor = Mantequilla,
            ),
            singleLine = true
        )

        Spacer(Modifier.height(14.dp))

        // Password
        Text(
            "Contraseña",
            style = MaterialTheme.typography.labelMedium,
            color = Espresso,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("••••••••", color = Madera.copy(alpha = 0.5f)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Melocotón,
                focusedBorderColor = Pimentón,
                unfocusedContainerColor = Mantequilla,
                focusedContainerColor = Mantequilla,
            ),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        // Olvidé contraseña
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = {}) {
                Text("Olvidé mi contraseña", color = Pimentón, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Botón principal — para el demo cualquier input navega
        Button(
            onClick = onLoginSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Pimentón)
        ) {
            Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(20.dp))

        // Divider
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Melocotón)
            Text(" o ", color = Madera.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Melocotón)
        }

        Spacer(Modifier.height(16.dp))

        // Google
        OutlinedButton(
            onClick = onLoginSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Mantequilla),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Melocotón)
        ) {
            Text("G", fontWeight = FontWeight.Bold, color = Color(0xFF4285F4), fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Text("Continuar con Google", color = Espresso, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(24.dp))

        // Registro
        Row(horizontalArrangement = Arrangement.Center) {
            Text("¿No tenés cuenta? ", color = Madera, style = MaterialTheme.typography.bodyMedium)
            TextButton(
                onClick = onNavigateToRegister,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Crear cuenta", color = Pimentón, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    QLessTheme { LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {}) }
}