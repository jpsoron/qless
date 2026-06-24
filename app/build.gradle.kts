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
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SUPABASE_URL", "\"${secret("SUPABASE_URL", "supabase.url")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${secret("SUPABASE_ANON_KEY", "supabase.anon.key")}\"")
        // Web Client ID para Google Auth (inyectado desde local.properties o env)
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${secret("GOOGLE_WEB_CLIENT_ID", "google.web.client.id")}\"")

        // API key de Google Maps. Va por local.properties / env (NO se commitea).
        // Se inyecta en el manifest como ${MAPS_API_KEY}.
        manifestPlaceholders["MAPS_API_KEY"] =
            System.getenv("MAPS_API_KEY") ?: localProperties["maps.api.key"]?.toString() ?: ""
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Firma con la clave de debug solo para poder instalar una build
            // NO-debuggable y medir métricas (cold start / fps). No es para distribución.
            signingConfig = signingConfigs.getByName("debug")
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

    // Credential Manager y Google ID (Nombres corregidos para coincidir con libs.versions.toml)
    implementation(libs.credentials.core)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

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
    implementation(libs.play.services.location)

    // Mapa real (Google Maps Compose)
    implementation("com.google.maps.android:maps-compose:8.3.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // Permissions
    implementation(libs.accompanist.permissions)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
