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
| Persistencia local | Room 2.7.1 (entidades + DAOs + Flow) |
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
│   └── AppNavigation.kt          — NavHost con 29 rutas (sealed class Screen)
├── data/
│   ├── CartItem.kt               — modelo de dominio
│   ├── CartRepository.kt         — acceso a datos del carrito
│   ├── PaymentMethod.kt          — modelo de dominio
│   ├── PaymentMethodRepository.kt
│   ├── User.kt                   — modelo de dominio
│   ├── UserRepository.kt         — registro, login, SHA-256
│   └── local/
│       ├── QLessDatabase.kt      — singleton RoomDatabase (v3)
│       ├── dao/
│       │   ├── CartItemDao.kt
│       │   ├── PaymentMethodDao.kt
│       │   └── UserDao.kt
│       └── entity/
│           ├── CartItemEntity.kt      — + extensiones toDomain / toEntity
│           ├── PaymentMethodEntity.kt — + extensiones toDomain / toEntity
│           └── UserEntity.kt          — + extensión toDomain
└── ui/
    ├── viewmodel/
    │   ├── AuthViewModel.kt          — AuthUiState + AuthNavEvent (SharedFlow)
    │   ├── CartViewModel.kt          — CartUiState
    │   ├── PaymentMethodViewModel.kt — PaymentMethodUiState
    │   ├── MenuViewModel.kt          — MenuUiState (isLoading, selectedCategory)
    │   └── MisLocalesViewModel.kt    — MisLocalesUiState (isLoading)
    ├── components/
    │   └── QLessBottomNav.kt
    ├── samples/
    │   └── UISamplesScreen.kt
    ├── screens/
    │   ├── SplashScreen.kt
    │   ├── OnboardingScreen.kt
    │   ├── LoginScreen.kt
    │   ├── RegisterScreen.kt
    │   ├── GoogleLoginScreen.kt
    │   ├── HomeScreen.kt
    │   ├── LocationDetectedScreen.kt
    │   ├── MisLocalesScreen.kt
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
    │   ├── BackOfficeScreen.kt
    │   ├── BackOfficeHistoryScreen.kt
    │   ├── BackOfficeUpdateOrderScreen.kt
    │   └── BackOfficeAjustesScreen.kt
    └── theme/
        ├── Color.kt              — tokens primitivos, LightColorScheme, DarkColorScheme, QLessStatusColors
        ├── Theme.kt              — QLessTheme(), KraftSurface, QLessExtras
        └── Type.kt               — AppTypography con Lora y Plus Jakarta Sans
```

---

## Arquitectura

La aplicación implementa MVVM con patrón Repository sobre tres capas.

### Capa de Datos (`data/`)

**Entidades Room:**

- `CartItemEntity`: producto en carrito. PK: `name`. Campos: `emoji`, `detail`, `unitPrice`, `quantity`.
- `PaymentMethodEntity`: método de pago. PK: `id` (UUID). Campos: `tipo`, `nombre`, `ultimosDigitos`, `vencimiento`, `esPrincipal`, `esBilletera`.
- `UserEntity`: usuario registrado. PK: `email`. Campos: `name`, `passwordHash` (SHA-256), `role` (`USER` o `BACK_OFFICE`).

**DAOs:**

- `CartItemDao`: `getAll(): Flow<List<CartItemEntity>>`, `upsert()`, `deleteByName()`, `deleteAll()`.
- `PaymentMethodDao`: `getAll(): Flow`, `count()`, `insert()`, `insertAll()`, `deleteById()`, `clearPrincipal()`.
- `UserDao`: `findByEmail()`, `insert()`, `deleteByEmail()`.

**Repositorios:**

- `CartRepository`: expone un `Flow` del carrito. `addItem()` hace upsert (incrementa cantidad). `updateQuantity()` elimina la fila si la nueva cantidad es 0.
- `PaymentMethodRepository`: expone un `Flow` de métodos. `add()` infiere el tipo de tarjeta por el primer dígito. `seedDefaults()` siembra tres métodos por defecto en la primera instalación.
- `UserRepository`: `register()` valida email único y hashea la contraseña con SHA-256. `login()` compara hash. `seedBackOffice()` crea el usuario de backoffice si no existe.

**Base de datos:**

`QLessDatabase` es un singleton con `@Volatile` + `synchronized`. Versión 3, `fallbackToDestructiveMigration()` activo (aceptable en dev, requiere reemplazarse por migraciones explícitas antes de producción).

### Capa de Presentación (`ui/`)

#### Patrón de estado

Todos los ViewModels exponen estado mediante `StateFlow<UiState>` y siguen el mismo esquema:

```kotlin
private val _uiState = MutableStateFlow(XxxUiState())
val uiState: StateFlow<XxxUiState> = _uiState.asStateFlow()
```

Las pantallas observan el estado con `collectAsState()`:

```kotlin
val uiState by viewModel.uiState.collectAsState()
```

Las actualizaciones de estado son siempre inmutables usando `_uiState.update { it.copy(...) }`.

#### Eventos de navegación (one-shot)

Para operaciones asíncronas que disparan navegación (login, registro, eliminación de cuenta), `AuthViewModel` expone un `SharedFlow<AuthNavEvent>` en lugar de recibir callbacks:

```kotlin
private val _navEvent = MutableSharedFlow<AuthNavEvent>()
val navEvent: SharedFlow<AuthNavEvent> = _navEvent.asSharedFlow()
```

Las pantallas colectan los eventos en un `LaunchedEffect`:

```kotlin
LaunchedEffect(Unit) {
    authViewModel.navEvent.collect { event ->
        when (event) {
            AuthNavEvent.LoginSuccess -> onLoginSuccess()
            AuthNavEvent.LoginBackOffice -> onNavigateToBackOffice()
            else -> Unit
        }
    }
}
```

Esto elimina el acoplamiento entre el ViewModel y la lógica de navegación, siguiendo el principio de Unidirectional Data Flow (UDF).

#### ViewModels

| ViewModel | UiState | Eventos (SharedFlow) | Descripción |
|---|---|---|---|
| `AuthViewModel` | `AuthUiState` | `AuthNavEvent` | Login, registro, logout, deleteAccount. Siembra el usuario backoffice en `init`. |
| `CartViewModel` | `CartUiState` | — | Colecciona el Flow del repositorio. Expone addItem, removeItem, getQuantity, clearCart. |
| `PaymentMethodViewModel` | `PaymentMethodUiState` | — | Siembra métodos por defecto si la tabla está vacía. Expone addMethod, removeMethod. |
| `MenuViewModel` | `MenuUiState` | — | Gestiona isLoading (simulado con delay de 1.5s en init) y selectedCategory. |
| `MisLocalesViewModel` | `MisLocalesUiState` | — | Gestiona isLoading (simulado con delay de 1.5s en init). |

**UiState types:**

```kotlin
data class AuthUiState(
    val currentUserName: String = "",
    val currentUserEmail: String = "",
    val loginError: String? = null,
    val registerError: String? = null,
    val isLoading: Boolean = false,
)

data class CartUiState(val items: List<CartItem> = emptyList())

data class PaymentMethodUiState(val methods: List<PaymentMethod> = emptyList())

data class MenuUiState(val isLoading: Boolean = true, val selectedCategory: String = "🔥 Popular")

data class MisLocalesUiState(val isLoading: Boolean = true)
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

- `LoginScreen` / `RegisterScreen`: colectan `AuthUiState` y el `SharedFlow` de nav events. El botón muestra `CircularProgressIndicator` mientras `isLoading = true`.
- `CerrarSesionScreen` / `EliminarCuentaScreen`: colectan `AuthUiState` para mostrar nombre y email del usuario activo.
- `MenuScreen`: recibe `CartViewModel` + `MenuViewModel`. Lee `selectedCategory` e `isLoading` del `MenuUiState`; las categorías del menú actualizan el estado via `menuViewModel.selectCategory()`.
- `MisLocalesScreen`: recibe `MisLocalesViewModel`. Lee `isLoading` del `MisLocalesUiState`.
- `CartScreen` / `PaymentScreen`: colectan `CartUiState` para obtener la lista de ítems y los totales.
- `MetodosDePagoScreen` / `AgregarMetodoDePagoScreen`: colectan `PaymentMethodUiState`.

### Navegación

`AppNavigation.kt` define un `NavHost` con 29 rutas. Los ViewModels se instancian una única vez dentro de `AppNavigation` con `viewModel()` y se pasan como parámetros, garantizando estado compartido durante toda la sesión. Las pantallas reciben callbacks de navegación específicos (`onBack`, `onLoginSuccess`, etc.) y nunca tienen referencia directa al `NavController`.

**ViewModels instanciados en AppNavigation:**

```kotlin
val authViewModel: AuthViewModel = viewModel()
val cartViewModel: CartViewModel = viewModel()
val paymentViewModel: PaymentMethodViewModel = viewModel()
val menuViewModel: MenuViewModel = viewModel()
val misLocalesViewModel: MisLocalesViewModel = viewModel()
```

### BackOffice

La sección BackOffice tiene su propio stack de navegación con 4 pantallas y una bottom nav de 3 tabs:

| Tab | Pantalla | Índice |
|---|---|---|
| Pedidos en curso | `BackOfficeScreen` | 0 |
| Historial | `BackOfficeHistoryScreen` | 1 |
| Ajustes | `BackOfficeAjustesScreen` | 2 |

`BackOfficeAjustesScreen` muestra el perfil del operador (avatar con iniciales, nombre, email) y un botón de cierre de sesión con diálogo de confirmación. Recibe `userName` y `userEmail` como `String` desde `AppNavigation`, y `onLogout` / `onNavigateToOrders` / `onNavigateToHistory` como callbacks.

---

## Flujo Principal del Usuario

```
Splash → Onboarding → Login / Registro
       → LocationDetected / Scan QR
       → Menú (armar pedido)
       → Carrito (revisar pedido)
       → Pago (seleccionar método)
       → OrderConfirmed
       → Tracking
       → OrderReady → PickupSuccess
```

---

## Estado de los Datos (actual)

- **Carrito y métodos de pago**: persistidos en Room. Sobreviven a reinicios de la app.
- **Sesión de usuario**: en memoria (`AuthUiState` en el ViewModel). Se pierde si el proceso es eliminado.
- **Menú y locales**: hardcodeados en las pantallas. Sin backend.
- **Pedidos activos**: sin persistencia. Simulados con estado local en la sesión.

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
```

---

## Pendiente para Entrega 2

### Backend e integración de red

- Implementar capa de red con Retrofit2 + Gson (o Ktor).
- Conectar menú, locales y pedidos a una API REST.
- Manejar errores de conectividad y modo offline básico (RF4): cachear el último menú en Room.

### Autenticación real

- Integrar Firebase Auth o similar para email/password y Google Sign-In real (RF3).
- Persistir sesión entre process deaths con DataStore (actualmente se pierde al forzar cierre).

### Clean Architecture

- Introducir capa `domain/` con modelos puros y casos de uso (`LoginUseCase`, `AddToCartUseCase`, etc.).
- Definir interfaces de repositorio en `domain/` e implementarlas en `data/`.
- Crear mappers explícitos en `data/mapper/` (actualmente las extension functions `toDomain` / `toEntity` viven en los archivos de entidad).

### Inyección de dependencias (Hilt)

- Incorporar Hilt para eliminar la instanciación manual de DAOs y repositorios dentro de los ViewModels.
- Anotar ViewModels con `@HiltViewModel` + `@Inject constructor`.
- Migrar de `AndroidViewModel` (que requiere `Application`) a `ViewModel` regular una vez que el contexto lo provea Hilt.

### Interfaces de repositorio

- Convertir `UserRepository`, `CartRepository` y `PaymentMethodRepository` de clases concretas a implementaciones de interfaces, para permitir dobles de test.

### Pruebas (RF5)

- Unit tests de ViewModels con JUnit4 + MockK y repositorios falsos.
- Tests de composables stateless con Compose Testing.
- Métricas de cobertura.

### Accesibilidad

- Auditar `contentDescription` en todos los elementos interactivos.
- Verificar tamaños de fuente escalables y contraste en dark mode.
- Internacionalización (strings.xml en inglés como base).

### Sensor real

- Integrar CameraX + ML Kit para lectura real de QR (reemplazar la simulación actual).

### Migración de base de datos

- Reemplazar `fallbackToDestructiveMigration()` por migraciones Room explícitas antes del RC.
