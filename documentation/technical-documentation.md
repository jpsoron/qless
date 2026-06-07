# QLess — Documentación Técnica

## Alcance del Proyecto

### Problema que resuelve

En locales gastronómicos de alta demanda, los usuarios deben hacer filas en caja para realizar su pedido, lo que genera fricción en la experiencia y desperdicio de tiempo tanto para el cliente como para el local. QLess actúa como un beeper virtual: el usuario arma y confirma su pedido desde su celular, y la app le notifica cuando está listo para retirar.

### Usuarios objetivo

Dos actores principales:

- **Cliente final:** usuario que escanea el QR del local o lo selecciona por GPS, arma su pedido, paga in-app y hace seguimiento del estado en tiempo real.
- **Operador del local (BackOffice):** encargado de caja o cocina que gestiona los pedidos entrantes, actualiza su estado y confirma el retiro.

### Casos de uso principales

1. Onboarding inicial con presentación de la propuesta de valor y descuento de bienvenida del 10 %.
2. Registro e inicio de sesión con email/contraseña. Login federado con Google (simulado).
3. Detección de local por GPS o escaneo de QR de mesa.
4. Navegación del menú del local y armado del carrito.
5. Confirmación del pedido y selección del método de pago.
6. Seguimiento del estado del pedido (beeper virtual) con timeline en tiempo real.
7. Confirmación de retiro y pantalla de éxito.
8. Gestión de pedidos desde la vista BackOffice del local.

### Flujos de pantallas implementados

- **Flujo de autenticación:** Splash → Onboarding → Login / Registro → Google Auth (simulado)
- **Flujo de pedido:** Detección GPS / Scan QR → Selección de local → Menú → Carrito → Pago → Pedido confirmado → Seguimiento → Pedido listo → Retiro exitoso
- **Flujo de gestión:** BackOffice → Historial de pedidos → Actualización de estado → Ajustes BackOffice

### Sensor utilizado

Cámara del dispositivo para escaneo de códigos QR de mesa (RF6). Actualmente simulado con un timer de 5 segundos.

### Design System

| Token | Valor |
|-------|-------|
| Crema cálida | `#FFFBF5` — fondo principal |
| Mantequilla | `#FFF8EE` — superficies / cards |
| Espresso | `#2C1A0E` — texto principal |
| Madera | `#7A5C3E` — texto secundario |
| Pimentón | `#C44B1B` — CTA principal |
| Tomate | `#E8673A` — inversePrimary / hover |
| Melocotón | `#FFEDE0` — chips / tags / bordes |
| Albahaca | `#1A7A4A` — entregado / disponible |
| Azafrán | `#D4870E` — en preparación |
| Arándano | `#1D6FA8` — en camino / tracking |
| Borgoña | `#8C2A2A` — agotado / error |

Los tokens del design system están mapeados a roles de Material 3 (`MaterialTheme.colorScheme.*`) en `LightColorScheme` y `DarkColorScheme`. Los colores de estado de pedido (`Albahaca`, `Azafrán`, `Arándano`) se exponen a través del objeto `QLessStatusColors` para no contaminar el esquema de color estructural.

Tipografía: **Lora** (Display, Headline) + **Plus Jakarta Sans** (UI funcional: Title, Body, Label).

---

## Descripción General

QLess es una aplicación Android nativa orientada al pedido anticipado en locales gastronómicos. El usuario escanea un código QR o selecciona un local, arma su pedido desde el menú, lo confirma y realiza el pago desde la aplicación, evitando filas en el punto de venta. El local recibe el pedido y notifica cuando está listo para retirar.

---

## Stack Tecnológico

| Elemento | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose 2.9.0 |
| Estado y ciclo de vida | ViewModel (AndroidViewModel / ViewModel), StateFlow, SharedFlow |
| Persistencia local | Room 2.7.1 (carrito y métodos de pago) + DataStore Preferences (tema, onboarding, sesión) |
| Backend / Auth | Supabase Auth 3.1.4 (email + password) |
| Backend / Base de datos | Supabase PostgREST (postgrest-kt 3.1.4) sobre PostgreSQL |
| Cliente HTTP | Ktor OkHttp Engine 3.1.3 |
| Serialización | Kotlin Serialization (plugin + kotlinx-serialization-json) |
| Build system | Gradle con Version Catalogs (libs.versions.toml) |
| Generación de código | KSP 2.2.10-2.0.2 |
| Compose BOM | 2026.05.01 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |

---

## Estructura del Proyecto

```
com.qless
├── MainActivity.kt
├── navigation/
│   └── AppNavigation.kt             — NavHost con 29 rutas (sealed class Screen)
├── data/
│   ├── CartItem.kt                  — modelo de dominio
│   ├── CartRepository.kt
│   ├── Local.kt                     — modelo de dominio (local gastronómico)
│   ├── LocalesRepository.kt         — getLocales() + getFavoritos(ids)
│   ├── PaymentMethod.kt             — modelo de dominio
│   ├── PaymentMethodRepository.kt
│   ├── SessionStorage.kt            — persistencia de sesión en DataStore (qless_session)
│   ├── ThemeRepository.kt           — dark mode + onboarding en DataStore (qless_settings)
│   ├── UserRepository.kt            — auth + perfil + sesión persistente
│   ├── local/
│   │   ├── QLessDatabase.kt         — singleton RoomDatabase (v3)
│   │   ├── dao/
│   │   │   ├── CartItemDao.kt
│   │   │   ├── PaymentMethodDao.kt
│   │   │   └── UserDao.kt
│   │   └── entity/
│   │       ├── CartItemEntity.kt
│   │       ├── PaymentMethodEntity.kt
│   │       └── UserEntity.kt
│   └── remote/
│       ├── SupabaseClient.kt              — singleton (URL + anon key desde BuildConfig)
│       ├── AuthRemoteDataSource.kt        — signIn, signUp, signOut, getCurrentSessionJson, tryImportSession
│       ├── LocalesRemoteDataSource.kt     — fetchLocales(), fetchLocalesByIds(ids)
│       ├── ProfileRemoteDataSource.kt     — fetchProfile() desde tabla `perfiles`
│       └── dto/
│           ├── LocalDto.kt               — DTO @Serializable + toDomain()
│           └── Perfil.kt                 — DTO @Serializable (id, nombre, email, rol, favoritos)
└── ui/
    ├── viewmodel/
    │   ├── AuthViewModel.kt           — AuthUiState + AuthNavEvent + checkExistingSession()
    │   ├── CartViewModel.kt
    │   ├── HomeViewModel.kt           — HomeUiState (favoritos) + loadFavoritos(ids)
    │   ├── MisLocalesViewModel.kt     — MisLocalesUiState (locales desde Supabase)
    │   ├── MenuViewModel.kt
    │   ├── PaymentMethodViewModel.kt
    │   └── ThemeViewModel.kt          — dark mode + onboarding
    ├── components/
    │   └── QLessBottomNav.kt
    ├── screens/
    │   ├── SplashScreen.kt
    │   ├── OnboardingScreen.kt
    │   ├── LoginScreen.kt             — checkbox "Mantener sesión" funcional
    │   ├── RegisterScreen.kt
    │   ├── GoogleLoginScreen.kt
    │   ├── HomeScreen.kt              — favoritos reales desde Supabase (HomeViewModel)
    │   ├── LocationDetectedScreen.kt
    │   ├── MisLocalesScreen.kt        — locales reales desde Supabase (MisLocalesViewModel)
    │   ├── ScanearQrScreen.kt
    │   ├── QrNoReconocidoScreen.kt
    │   ├── MenuScreen.kt
    │   ├── CartScreen.kt
    │   ├── PaymentScreen.kt
    │   ├── AgregarMetodoDePagoScreen.kt
    │   ├── MetodosDePagoScreen.kt
    │   ├── OrderConfirmedScreen.kt
    │   ├── TrackingScreen.kt
    │   ├── OrderReadyScreen.kt
    │   ├── PickupSuccessScreen.kt
    │   ├── OrderSummaryScreen.kt
    │   ├── MisPedidosScreen.kt
    │   ├── AjustesScreen.kt
    │   ├── NotificacionesScreen.kt
    │   ├── EliminarCuentaScreen.kt
    │   ├── CerrarSesionScreen.kt
    │   └── backoffice/
    │       ├── BackOfficeScreen.kt
    │       ├── BackOfficeHistoryScreen.kt
    │       ├── BackOfficeUpdateOrderScreen.kt
    │       └── BackOfficeAjustesScreen.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## Arquitectura

La aplicación implementa MVVM con patrón Repository sobre tres capas.

### Capa de Datos (`data/`)

**Entidades Room (persistencia local):**

- `CartItemEntity`: producto en carrito. PK: `name`.
- `PaymentMethodEntity`: método de pago. PK: `id` (UUID).
- `UserEntity`: disponible para caché de perfil (actualmente no usado para auth).

**DAOs:**

- `CartItemDao`: `getAll(): Flow<List<CartItemEntity>>`, `upsert()`, `deleteByName()`, `deleteAll()`.
- `PaymentMethodDao`: `getAll(): Flow`, `count()`, `insert()`, `insertAll()`, `deleteById()`, `clearPrincipal()`.
- `UserDao`: `findByEmail()`, `insert()`, `deleteByEmail()`.

**Modelos de dominio:**

- `Local`: representa un local gastronómico. Campos: `id`, `emoji`, `nombre`, `categoria`, `barrio`, `rating`, `tiempoEntrega`, `abierto`, `tienePromo`, `destacado`.
- `RemoteUser`: usuario autenticado. Campos: `name`, `email`, `role`, `favoritos: List<String>`.

**Repositorios:**

- `CartRepository`: expone un `Flow` del carrito. `addItem()` hace upsert.
- `PaymentMethodRepository`: expone un `Flow` de métodos de pago.
- `LocalesRepository`: `getLocales()` trae todos los locales desde Supabase. `getFavoritos(ids)` trae solo los locales cuyas UUIDs coinciden con la lista provista.
- `UserRepository`: orquesta auth + perfil + persistencia de sesión. `login(email, password, rememberMe)` encadena sign-in + fetch de perfil; si `rememberMe=true`, guarda el JSON de sesión en `SessionStorage`. `logout()` hace sign-out y borra la sesión guardada. `tryRestoreSession()` carga el JSON de sesión desde `SessionStorage`, importa la sesión en supabase-kt (con auto-refresh del token) y retorna `RemoteUser?`.

**RemoteDataSources:**

- `AuthRemoteDataSource`: wrappea Supabase Auth SDK. `signIn()` y `signUp()` retornan `Result<Unit>`. `getCurrentSessionJson()` serializa la sesión activa a JSON. `tryImportSession(json)` deserializa e importa la sesión con `auth.importSession(session, autoRefresh = true)`, que renueva el access token si expiró.
- `LocalesRemoteDataSource`: `fetchLocales()` trae todos los registros de la tabla `locales` vía PostgREST. `fetchLocalesByIds(ids)` filtra por `id IN (...)` usando el DSL de filtros de supabase-kt.
- `ProfileRemoteDataSource`: `fetchProfile()` retorna el perfil del usuario autenticado desde la tabla `perfiles` (RLS garantiza que solo se retorna el propio perfil).

**DTOs:**

- `Perfil`: `id`, `nombre`, `email`, `rol`, `favoritos: List<String>` — mapea la tabla `perfiles` incluyendo el array de UUIDs favoritos.
- `LocalDto`: mapea la tabla `locales`. `toDomain()` convierte al modelo `Local`.

**Persistencia de sesión (`SessionStorage`):**

Guarda el JSON de la sesión de Supabase en un DataStore propio (`qless_session`). Expone tres operaciones: `save(json)`, `load(): String?` y `clear()`. El JSON almacenado incluye `access_token` y `refresh_token`. Al restaurar, supabase-kt renueva automáticamente el access token si está vencido usando el refresh token (que en Supabase dura 60 días por defecto).

**Supabase (Postgres):**

- Tabla `perfiles`: `id` (uuid FK → `auth.users`), `nombre`, `email`, `rol` (`USER` | `BACK_OFFICE`), `favoritos` (`uuid[]`, default `{}`). RLS habilitado: SELECT con `using(true)`, UPDATE con `auth.uid() = id`. Trigger `on_auth_user_created` inserta automáticamente al registrarse.
- Tabla `locales`: `id` (uuid PK), `nombre`, `emoji`, `categoria`, `barrio`, `direccion`, `rating`, `tiempo_entrega`, `abierto`, `tiene_promo`, `destacado`. RLS con `using(true)`. Setup en `SUPABASE_LOCALES.md`.

---

### Capa de Presentación (`ui/`)

#### Patrón de estado

Todos los ViewModels exponen estado mediante `StateFlow<UiState>`:

```kotlin
private val _uiState = MutableStateFlow(XxxUiState())
val uiState: StateFlow<XxxUiState> = _uiState.asStateFlow()
```

Las actualizaciones son siempre inmutables: `_uiState.update { it.copy(...) }`.

#### Eventos de navegación (one-shot)

Para operaciones asíncronas que disparan navegación, `AuthViewModel` expone un `SharedFlow<AuthNavEvent>`:

```kotlin
private val _navEvent = MutableSharedFlow<AuthNavEvent>()
val navEvent: SharedFlow<AuthNavEvent> = _navEvent.asSharedFlow()
```

Las pantallas colectan eventos en un `LaunchedEffect(Unit)`.

#### ViewModels

| ViewModel | UiState | Descripción |
|---|---|---|
| `AuthViewModel` | `AuthUiState` | Login, registro, logout, deleteAccount. Restaura sesión en `init {}`. Mapea errores de Supabase a mensajes en español. |
| `HomeViewModel` | `HomeUiState` | Carga los locales favoritos del usuario por ID desde Supabase. `loadFavoritos(ids)` es llamado desde AppNavigation con `LaunchedEffect`. |
| `MisLocalesViewModel` | `MisLocalesUiState` | Carga la lista completa de locales desde Supabase en `init {}`. |
| `CartViewModel` | `CartUiState` | Colecciona el Flow del repositorio de carrito. |
| `PaymentMethodViewModel` | `PaymentMethodUiState` | Siembra métodos por defecto si la tabla está vacía. |
| `MenuViewModel` | `MenuUiState` | Gestiona `isLoading` y `selectedCategory`. |
| `ThemeViewModel` | — | Dark mode + onboarding completado (DataStore). |

**UiState relevantes:**

```kotlin
data class AuthUiState(
    val currentUserName: String = "",
    val currentUserEmail: String = "",
    val currentUserRole: String = "",
    val currentUserFavoritos: List<String> = emptyList(),
    val sessionCheckDone: Boolean = false,   // true cuando checkExistingSession() terminó
    val sessionRestored: Boolean = false,    // true si se restauró una sesión persistida
    val loginError: String? = null,
    val registerError: String? = null,
    val isLoading: Boolean = false,
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val favoritos: List<Local> = emptyList(),
)

data class MisLocalesUiState(
    val isLoading: Boolean = true,
    val locales: List<Local> = emptyList(),
    val error: String? = null,
)
```

**AuthNavEvent:**

```kotlin
sealed interface AuthNavEvent {
    data object LoginSuccess : AuthNavEvent
    data object LoginBackOffice : AuthNavEvent
    data object RegisterSuccess : AuthNavEvent
    data object AccountDeleted : AuthNavEvent
}
```

#### Pantallas con estado real

- `LoginScreen`: colecta `AuthUiState` y el SharedFlow de nav events. El checkbox "Mantener sesión" pasa el flag `rememberMe` a `authViewModel.login()`.
- `HomeScreen`: recibe `HomeViewModel`. Muestra los locales favoritos del usuario cargados desde Supabase, con shimmer skeleton durante la carga y mensaje vacío si no hay favoritos.
- `MisLocalesScreen`: recibe `MisLocalesViewModel`. Muestra la lista completa de locales desde Supabase con skeleton de carga.
- `CerrarSesionScreen` / `EliminarCuentaScreen`: muestran nombre y email del usuario activo desde `AuthUiState`.

### Navegación

`AppNavigation.kt` define un `NavHost` con 29 rutas. Los ViewModels se instancian una única vez y se pasan como parámetros.

**ViewModels instanciados en AppNavigation:**

```kotlin
val authViewModel: AuthViewModel = viewModel()
val cartViewModel: CartViewModel = viewModel()
val paymentViewModel: PaymentMethodViewModel = viewModel()
val menuViewModel: MenuViewModel = viewModel()
val misLocalesViewModel: MisLocalesViewModel = viewModel()
val homeViewModel: HomeViewModel = viewModel()
```

**Flujo de Splash con verificación de sesión:**

El composable de Splash espera dos condiciones simultáneas antes de navegar:

```kotlin
LaunchedEffect(splashAnimDone, authState.sessionCheckDone) {
    if (!splashAnimDone || !authState.sessionCheckDone) return@LaunchedEffect
    val destination = when {
        authState.sessionRestored && authState.currentUserRole == "BACK_OFFICE" -> Screen.BackOffice.route
        authState.sessionRestored -> Screen.Home.route
        onboardingCompleted -> Screen.Login.route
        else -> Screen.Onboarding.route
    }
    navController.navigate(destination) { popUpTo(Screen.Splash.route) { inclusive = true } }
}
```

`AuthViewModel.init {}` lanza `checkExistingSession()` al crearse (en paralelo con la animación del splash). Cuando ambas condiciones se cumplen —animación de 2 s y check de sesión completo— se determina el destino.

**Favoritos en Home:**

```kotlin
composable(Screen.Home.route) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(authState.currentUserFavoritos) {
        homeViewModel.loadFavoritos(authState.currentUserFavoritos)
    }
    HomeScreen(homeViewModel = homeViewModel, ...)
}
```

### BackOffice

La sección BackOffice tiene su propio stack con 4 pantallas y una bottom nav de 3 tabs. `BackOfficeAjustesScreen` muestra el perfil del operador y un botón de cierre de sesión con diálogo de confirmación.

---

## Flujo Principal del Usuario

```
Splash (verificación de sesión en paralelo)
  ├── sesión activa → Home / BackOffice (sin pasar por Login)
  └── sin sesión → Onboarding (primera vez) / Login
        └── Login con "Mantener sesión" ✓ → guarda tokens en DataStore
              → LocationDetected → Menú → Carrito → Pago
              → OrderConfirmed → Tracking → OrderReady → PickupSuccess
```

---

## Estado de los Datos

| Dato | Fuente | Persistencia |
|---|---|---|
| Sesión de usuario | Supabase Auth | DataStore (`qless_session`) si "Mantener sesión" activo |
| Perfil (`nombre`, `email`, `rol`) | Supabase `perfiles` | En memoria (leído en cada login/restore) |
| Favoritos del usuario (`favoritos uuid[]`) | Supabase `perfiles` | En `AuthUiState` durante la sesión |
| Locales gastronómicos | Supabase `locales` | Sin caché local (siempre desde red) |
| Carrito | Room | Sobrevive a reinicios |
| Métodos de pago | Room | Sobrevive a reinicios |
| Tema oscuro / onboarding | DataStore (`qless_settings`) | Permanente |
| Menú y pedidos activos | Hardcodeado / simulado | Sin persistencia |

---

## Dependencias Principales

```toml
# libs.versions.toml
kotlin                               = "2.2.10"
agp                                  = "9.1.1"
ksp                                  = "2.2.10-2.0.2"
composeBom                           = "2026.05.01"
lifecycleRuntimeKtx                  = "2.10.0"
navigationCompose                    = "2.9.0"
room                                 = "2.7.1"
supabase                             = "3.1.4"   # BOM: auth-kt + postgrest-kt
ktor                                 = "3.1.3"   # ktor-client-okhttp
```

---

## Pendiente para Entrega 2

### Backend e integración de red

- ~~Conectar locales a Supabase PostgREST~~ ✓ (tabla `locales` + RLS)
- ~~Favoritos del usuario desde Supabase~~ ✓ (columna `favoritos uuid[]` en `perfiles`)
- Conectar menú y pedidos a Supabase PostgREST.
- Manejar errores de conectividad y modo offline básico (RF4): cachear el último menú y locales en Room.

### Autenticación

- ~~Autenticación real con email/password~~ ✓ (Supabase Auth)
- ~~Perfil de usuario desde Postgres~~ ✓ (tabla `perfiles` + RLS + trigger)
- ~~Persistencia de sesión entre reinicios~~ ✓ (SessionStorage + DataStore + importSession)
- Google Sign-In real (RF3): pendiente.
- Cachear perfil en Room para lectura offline.
- Eliminar cuenta: requiere Supabase Edge Function con service role (actualmente solo hace sign-out).
- Agregar / quitar favoritos desde la UI (hoy solo se leen, la modificación se hace desde SQL).

### Clean Architecture

- Introducir capa `domain/` con modelos puros y casos de uso.
- Definir interfaces de repositorio en `domain/` e implementarlas en `data/`.

### Inyección de dependencias (Hilt)

- Incorporar Hilt para eliminar la instanciación manual de DAOs y repositorios dentro de los ViewModels.
- Migrar de `AndroidViewModel` a `ViewModel` regular una vez que el contexto lo provea Hilt.

### Pruebas (RF5)

- Unit tests de ViewModels con JUnit4 + MockK y repositorios falsos.
- Tests de composables stateless con Compose Testing.

### Accesibilidad

- Auditar `contentDescription` en todos los elementos interactivos.
- Verificar tamaños de fuente escalables y contraste en dark mode.

### Sensor real

- ~~Integrar CameraX + ML Kit para lectura real de QR~~ ✓ (implementado, simulado con timer de 5 s)

### Migración de base de datos

- Reemplazar `fallbackToDestructiveMigration()` por migraciones Room explícitas antes del RC.
