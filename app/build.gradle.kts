import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().also { props ->
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { props.load(it) }
}

// Credenciales de firma del release de distribución (NO se commitea).
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().also { props ->
    keystorePropsFile.takeIf { it.exists() }?.inputStream()?.use { props.load(it) }
}

// Prefiere variable de entorno (CI) y cae a local.properties (desarrollo local).
// Escapa el valor para que sea un string literal de Java válido en BuildConfig.
fun secret(envKey: String, propKey: String): String =
    (System.getenv(envKey) ?: localProperties[propKey]?.toString() ?: "")
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")

android {
    namespace = "com.qless"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.qless"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0-rc1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SUPABASE_URL", "\"${secret("SUPABASE_URL", "supabase.url")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${secret("SUPABASE_ANON_KEY", "supabase.anon.key")}\"")

        // Web client ID de Google OAuth (serverClientId del login con Google).
        // No es secreto, pero va por local.properties / env para no commitearlo.
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${secret("GOOGLE_WEB_CLIENT_ID", "google.web.client.id")}\"")

        // API key de Google Maps. Va por local.properties / env (NO se commitea).
        // Se inyecta en el manifest como ${MAPS_API_KEY}.
        manifestPlaceholders["MAPS_API_KEY"] =
            System.getenv("MAPS_API_KEY") ?: localProperties["maps.api.key"]?.toString() ?: ""
    }

    signingConfigs {
        // Clave de release para el APK RC. Solo se activa si existe keystore.properties.
        if (keystorePropsFile.exists()) {
            create("release") {
                // rootProject.file: la ruta de storeFile se resuelve desde la raíz
                // del proyecto (donde está el .jks), no desde el módulo app/.
                storeFile = rootProject.file(keystoreProps["storeFile"] as String)
                storePassword = keystoreProps["storePassword"] as String
                keyAlias = keystoreProps["keyAlias"] as String
                keyPassword = keystoreProps["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Con keystore.properties firma con la clave de release (RC de distribución);
            // sin él cae a la clave de debug (para builds locales / métricas).
            signingConfig = if (keystorePropsFile.exists())
                signingConfigs.getByName("release")
            else
                signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.okhttp)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // ML Kit Barcode Scanning
    implementation(libs.barcode.scanning)

    // Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Mapa real (Google Maps Compose, usado en lite mode)
    implementation("com.google.maps.android:maps-compose:6.4.1")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // Permissions
    implementation(libs.accompanist.permissions)

    // Google Sign-In (Credential Manager + Google Identity Services)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
