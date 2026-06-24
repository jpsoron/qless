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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.qless.BuildConfig
import com.qless.ui.theme.*
import com.qless.ui.viewmodel.AuthNavEvent
import com.qless.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun GoogleLoginScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onLoginBackOffice: () -> Unit,
    onUseEmail: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState = authViewModel.uiState.collectAsStateWithLifecycle().value
    val credentialManager = CredentialManager.create(context)

    // Escuchar eventos de navegación exitosa
    LaunchedEffect(Unit) {
        authViewModel.navEvent.collect { event ->
            when (event) {
                is AuthNavEvent.LoginSuccess -> onLoginSuccess()
                is AuthNavEvent.LoginBackOffice -> onLoginBackOffice()
                else -> Unit
            }
        }
    }

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

        // Muestra error si falla el login
        if (uiState.loginError != null) {
            Text(
                text = uiState.loginError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Botón Google
        OutlinedButton(
            onClick = {
                scope.launch {
                    try {
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                            .setAutoSelectEnabled(true)
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        val result = credentialManager.getCredential(context, request)
                        val credential = result.credential

                        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            authViewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                        } else {
                            // En caso de que se reciba un tipo de credencial no esperado
                            authViewModel.clearErrors() // O manejar según necesidad
                        }
                    } catch (e: GetCredentialCancellationException) {
                        // El usuario canceló la operación, no mostramos error.
                    } catch (e: GetCredentialException) {
                        // Error real al obtener credenciales
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
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
        // Mock AuthViewModel o omitir para preview
    }
}
