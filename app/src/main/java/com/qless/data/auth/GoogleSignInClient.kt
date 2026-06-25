package com.qless.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.qless.BuildConfig
import java.security.MessageDigest
import java.util.UUID

/** Token de Google + el nonce crudo que hay que reenviar a Supabase. */
data class GoogleCredential(val idToken: String, val rawNonce: String)

/** El usuario cerró el selector de Google sin elegir cuenta (no es un error real). */
class GoogleSignInCancelled : Exception()

/**
 * Envuelve Credential Manager + Google Identity Services para obtener un ID token.
 * Vive en la capa de datos: aísla el SDK de Android del resto de la app. Necesita un
 * `Context` de Activity (lo provee la pantalla), por eso se construye por invocación.
 *
 * Seguridad: genera un nonce aleatorio, lo manda **hasheado** (SHA-256) a Google y
 * devuelve el **crudo** para que Supabase valide el hash (anti-replay).
 */
class GoogleSignInClient(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun getIdToken(): Result<GoogleCredential> = runCatching {
        val rawNonce = UUID.randomUUID().toString()
        val hashedNonce = sha256(rawNonce)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            // false = permite elegir cualquier cuenta y dar de alta una nueva (registro).
            .setFilterByAuthorizedAccounts(false)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = try {
            credentialManager.getCredential(context, request)
        } catch (e: GetCredentialCancellationException) {
            throw GoogleSignInCancelled()
        }

        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            GoogleCredential(
                idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken,
                rawNonce = rawNonce,
            )
        } else {
            throw IllegalStateException("Credencial de Google inesperada")
        }
    }

    private fun sha256(input: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
