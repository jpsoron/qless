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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qless.R
import com.qless.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToBackOffice: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToGoogleLogin: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CremaCálida)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Logo
        Image(
            painter = painterResource(R.drawable.ic_qless_pimenton),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "QLess",
            fontSize = 40.sp,
            fontWeight = FontWeight.SemiBold,
            color = Pimentón,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            "Tu comida, sin filas.",
            fontSize = 16.sp,
            color = Madera,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(48.dp))

        Text(
            "Bienvenido de nuevo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            color = Espresso,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // Email
        Text(
            "Correo electrónico",
            style = MaterialTheme.typography.labelMedium,
            color = Madera,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("nombre@ejemplo.com", color = Madera.copy(alpha = 0.4f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Melocotón,
                focusedBorderColor = Pimentón,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            ),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        // Password
        Text(
            "Contraseña",
            style = MaterialTheme.typography.labelMedium,
            color = Madera,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("••••••", color = Madera.copy(alpha = 0.4f)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Melocotón,
                focusedBorderColor = Pimentón,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            ),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        // Row for Checkbox and Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Pimentón,
                        uncheckedColor = Melocotón
                    ),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    "Mantener sesión\nabierta",
                    style = MaterialTheme.typography.bodySmall,
                    color = Espresso,
                    lineHeight = 14.sp,
                    fontSize = 12.sp
                )
            }
            TextButton(
                onClick = {},
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Olvidé mi contraseña",
                    color = Pimentón,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // Botón principal
        Button(
            onClick = {
                if (email.lowercase() == "backoffice" && password.lowercase() == "backoffice") {
                    onNavigateToBackOffice()
                } else {
                    onLoginSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Pimentón)
        ) {
            Text("Iniciar sesión", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        }

        Spacer(Modifier.height(24.dp))

        // Divider
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Melocotón.copy(alpha = 0.5f))
            Text(
                " o ",
                color = Madera.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Melocotón.copy(alpha = 0.5f))
        }

        Spacer(Modifier.height(24.dp))

        // Google
        OutlinedButton(
            onClick = onNavigateToGoogleLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(1.dp, Melocotón)
        ) {
            Surface(
                modifier = Modifier.size(24.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
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
            Spacer(Modifier.width(12.dp))
            Text("Continuar con Google", color = Espresso, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(40.dp))

        // Registro
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "¿No tenés cuenta? ",
                color = Espresso,
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(
                onClick = onNavigateToRegister,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Crear cuenta",
                    color = Pimentón,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    QLessTheme { 
        LoginScreen(
            onLoginSuccess = {}, 
            onNavigateToBackOffice = {},
            onNavigateToRegister = {}, 
            onNavigateToGoogleLogin = {}
        ) 
    }
}