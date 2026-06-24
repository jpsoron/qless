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
4. Navegación del menú real del local (cargado desde Supabase) y armado del carrito.
5. Confirmación del pedido. **MVP: el único medio habilitado es el pago en
   efectivo al retirar en el local;** los métodos digitales (tarjeta, billeteras)
   se muestran grisados como "Próximamente" en `PaymentScreen` y
   `MetodosDePagoScreen`, y "Agregar método" queda deshabilitado.
6. Seguimiento del estado del pedido (beeper virtual) con timeline en tiempo real.
7. Confirmación de retiro y pantalla de éxito.
8. Gestión de pedidos desde la vista BackOffice del local.

### Flujos de pantallas implementados

- **Flujo de autenticación:** Splash → Onboarding → Login / Registro → Google Auth (simulado)
- **Flujo de pedido:** Detección GPS / Scan QR → Selección de local → Menú → Carrito → Pago → Pedido confirmado → Seguimiento → Pedido listo → Retiro exitoso
- **Flujo de gestión:** BackOffice → Historial de pedidos → Actualización de estado → Ajustes BackOffice

### Sensor utilizado

Cámara del dispositivo para escaneo de códigos QR de mesa (RF6). Implementado con CameraX + ML Kit.

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

QLess es una aplicación Android nativa orientada al pedido anticipado en locales gastronómicos. El usuario escanea un código QR o selecciona un local, arma su pedido desde el menú real del local (cargado en tiempo real desde Supabase), lo confirma y paga en efectivo al retirar (MVP: el pago digital en la app está
previsto pero deshabilitado), evitando filas en el punto de venta. El local recibe el pedido y notifica cuando está listo para retirar.

---

## Stack Tecnológico

| Elemento | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose 2.9.0 |
| Estado y ciclo de vida | ViewModel (AndroidViewModel / ViewModel), StateFlow, SharedFlow |
| Persistencia local | Room 2.7.1 (carrito, métodos de pago, caché offline de locales y menú) + DataStore Preferences (tema, onboarding, sesión) |
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
│   └── AppNavigation.kt             — NavHost con 30 rutas (sealed class Screen)
├── data/
│   ├── SessionStorage.kt            — persistencia de sesión en DataStore (qless_session)
│   ├── local/
│   │   ├── QLessDatabase.kt         — singleton RoomDatabase (v8)
│   │   ├── dao/
│   │   │   ├── CartItemDao.kt
│   │   │   ├── LocalDao.kt
│   │   │   ├── MenuItemDao.kt
│   │   │   ├── NotificationDao.kt
│   │   │   ├── PaymentMethodDao.kt
│   │   │   └── UserDao.kt
│   │   └── entity/
│   │       ├── CartItemEntity.kt       — incluye campo localId
│   │       ├── LocalEntity.kt          — caché offline de locales (incluye latitud/longitud)
│   │       ├── MenuItemEntity.kt       — caché offline de menú (con columna orden)
│   │       ├── NotificationEntity.kt   — avisos persistidos por userId
│   │       ├── PaymentMethodEntity.kt
│   │       └── UserEntity.kt
│   ├── location/
│   │   └── FusedLocationProvider.kt   — impl de LocationProvider (Play Services)
│   ├── notification/
│   │   └── AndroidSystemNotifier.kt   — impl de SystemNotifier (NotificationManager, canal order_status)
│   ├── remote/
│   │   ├── SupabaseClient.kt              — singleton (URL + anon key desde BuildConfig); instala Auth + Postgrest + Realtime
│   │   ├── AuthRemoteDataSource.kt        — signIn, signUp, signOut, getCurrentSessionJson, tryImportSession
│   │   ├── LocalesRemoteDataSource.kt     — fetchLocales(), fetchLocalesByIds(ids)
│   │   ├── MenuRemoteDataSource.kt        — fetchMenuForLocal(localId)
│   │   ├── OrderRemoteDataSource.kt       — CRUD de pedidos + observeUserOrderChanges() / observeLocalOrderChanges()
│   │   ├── ProfileRemoteDataSource.kt     — fetchProfile() desde tabla `perfiles`
│   │   └── dto/
│   │       ├── LocalDto.kt               — DTO @Serializable + toDomain()
│   │       ├── MenuItemDto.kt            — DTO @Serializable + toDomain()
│   │       ├── OrderDto.kt               — DTO @Serializable + toDomain()
│   │       └── Perfil.kt                 — DTO @Serializable (id, nombre, email, rol, favoritos)
│   ├── repository/
│   │   ├── CartRepositoryImpl.kt
│   │   ├── LocalesRepositoryImpl.kt      — network-first + fallback a caché Room
│   │   ├── MenuRepositoryImpl.kt         — network-first + fallback a caché Room
│   │   ├── NotificationRepositoryImpl.kt
│   │   ├── OrderRepositoryImpl.kt
│   │   ├── PaymentMethodRepositoryImpl.kt
│   │   ├── ThemeRepositoryImpl.kt
│   │   └── UserRepositoryImpl.kt
│   └── session/
│       └── SupabaseSessionProvider.kt    — impl de SessionProvider
├── di/
│   └── AppModule.kt                      — composition root manual; inicializado en MainActivity.onCreate
├── domain/
│   ├── location/
│   │   └── LocationProvider.kt           — contrato (suspend fun currentLocation(): Coordinates?)
│   ├── model/
│   │   ├── AppNotification.kt
│   │   ├── AuthUser.kt
│   │   ├── CachedResult.kt               — wrapper data + fromCache para modo offline
│   │   ├── CartItem.kt
│   │   ├── Coordinates.kt
│   │   ├── Local.kt
│   │   ├── MenuItem.kt
│   │   ├── Order.kt
│   │   ├── PaymentMethod.kt
│   │   └── User.kt
│   ├── notification/
│   │   └── SystemNotifier.kt             — contrato (interface)
│   ├── repository/
│   │   ├── CartRepository.kt
│   │   ├── LocalesRepository.kt
│   │   ├── MenuRepository.kt
│   │   ├── NotificationRepository.kt
│   │   ├── OrderRepository.kt
│   │   ├── PaymentMethodRepository.kt
│   │   ├── ThemeRepository.kt
│   │   └── UserRepository.kt
│   ├── session/
│   │   └── SessionProvider.kt            — contrato (interface)
│   └── usecase/
│       ├── AuthUseCases.kt
│       ├── CartUseCases.kt
│       ├── LocalesUseCases.kt
│       ├── LocationUseCases.kt           — haversineMeters, RankLocalsByDistanceUseCase, GetCurrentLocationUseCase
│       ├── MenuUseCases.kt
│       ├── NotificationUseCases.kt
│       ├── OrderUseCases.kt
│       ├── PaymentUseCases.kt
│       └── ThemeUseCases.kt
└── ui/
    ├── components/
    │   ├── ActiveCartCard.kt             — card de carrito activo (reutilizable)
    │   ├── BackOfficeBottomNav.kt
    │   ├── OfflineBanner.kt
    │   ├── PulseRings.kt
    │   └── QLessBottomNav.kt
    ├── samples/
    │   └── UISamplesScreen.kt
    ├── screens/
    │   ├── AgregarMetodoDePagoScreen.kt
    │   ├── AjustesScreen.kt              — "Mi perfil" abre BottomSheet con edición de nombre
    │   ├── CartScreen.kt                 — incluye vaciar carrito con AlertDialog
    │   ├── CerrarSesionScreen.kt
    │   ├── EliminarCuentaScreen.kt
    │   ├── GoogleLoginScreen.kt
    │   ├── HomeScreen.kt                 — favoritos reales; badge de notificaciones; tap navega al menú del local
    │   ├── LocationDetectedScreen.kt
    │   ├── LoginScreen.kt                — checkbox "Mantener sesión" funcional
    │   ├── MenuScreen.kt                 — menú real desde Supabase; header dinámico por local
    │   ├── MetodosDePagoScreen.kt
    │   ├── MisLocalesScreen.kt           — locales reales; buscador, chips y orden funcionales
    │   ├── MisPedidosScreen.kt           — pedidos reales con tabs Activos / Finalizados / Cancelados
    │   ├── NotificacionesScreen.kt       — preferencias funcionales (estado del pedido / pedido listo / sonido+vibración) persistidas en DataStore y respetadas por NotifyOrderUpdateUseCase
    │   ├── NotificationCenterScreen.kt   — centro de notificaciones; marca leídas al abrir; badge en Home
    │   ├── OnboardingScreen.kt
    │   ├── OrderConfirmedScreen.kt
    │   ├── OrderReadyScreen.kt
    │   ├── OrderSummaryScreen.kt
    │   ├── PaymentScreen.kt
    │   ├── PickupSuccessScreen.kt
    │   ├── QrNoReconocidoScreen.kt
    │   ├── RegisterScreen.kt
    │   ├── ScanearQrScreen.kt
    │   ├── SplashScreen.kt
    │   ├── TrackingScreen.kt
    │   └── backoffice/
    │       ├── BackOfficeAjustesScreen.kt
    │       ├── BackOfficeHistoryScreen.kt
    │       ├── BackOfficeScreen.kt
    │       └── BackOfficeUpdateOrderScreen.kt
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    └── viewmodel/
        ├── AuthViewModel.kt              — AuthUiState + AuthNavEvent + checkExistingSession()
        ├── CartViewModel.kt              — incluye cartLocalId y addItem con localId
        ├── HomeViewModel.kt              — HomeUiState (favoritos) + loadFavoritos(ids)
        ├── MenuViewModel.kt              — carga menú real desde Supabase por localId (scoped por entry)
        ├── MisLocalesViewModel.kt        — MisLocalesUiState (locales desde Supabase)
        ├── NotificationViewModel.kt      — lista de notificaciones + unreadCount
        ├── OrderViewModel.kt             — pedidos en tiempo real vía Supabase Realtime
        ├── PaymentMethodViewModel.kt
        └── ThemeViewModel.kt             — dark mode + onboarding
```

---

## Arquitectura

La aplicación implementa MVVM con patrón Repository sobre tres capas.

### Capa de Datos (`data/`)

**Entidades Room (persistencia local):**

- `CartItemEntity`: producto en carrito. PK: `name`. Incluye `localId: String` para rastrear a qué local pertenece cada ítem del carrito.
- `PaymentMethodEntity`: método de pago. PK: `id` (UUID).
- `UserEntity`: disponible para caché de perfil (actualmente no usado para auth).

**DAOs:**

- `CartItemDao`: `getAll(): Flow<List<CartItemEntity>>`, `upsert()`, `deleteByName()`, `deleteAll()`.
- `PaymentMethodDao`: `getAll(): Flow`, `count()`, `insert()`, `insertAll()`, `deleteById()`, `clearPrincipal()`.
- `UserDao`: `findByEmail()`, `insert()`, `deleteByEmail()`.

**Modelos de dominio:**

- `Local`: representa un local gastronómico. Campos: `id`, `emoji`, `nombre`, `categoria`, `barrio`, `rating`, `tiempoEntrega`, `abierto`, `tienePromo`, `destacado`.
- `MenuItem`: ítem del menú de un local. Campos: `id`, `localId`, `emoji`, `nombre`, `descripcion`, `precio`, `categoria`, `esPopular`, `disponible`.
- `RemoteUser`: usuario autenticado. Campos: `name`, `email`, `role`, `favoritos: List<String>`.

**Repositorios:**

- `CartRepository`: expone un `Flow` del carrito. `addItem(emoji, name, detail, unitPrice, currentQuantity, localId)` hace upsert guardando el `localId` de procedencia.
- `PaymentMethodRepository`: expone un `Flow` de métodos de pago.
- `LocalesRepository`: `getLocales()` trae todos los locales desde Supabase. `getFavoritos(ids)` trae solo los locales cuyas UUIDs coinciden con la lista provista. `getLocalById(id)` resuelve un único local por id (network-first + fallback a caché Room); `data == null` significa que el id no corresponde a ningún local registrado (no es error de red). Lo usa el flujo de escaneo de QR.
- `MenuRepository`: `getMenu(localId)` trae todos los ítems del menú del local indicado desde Supabase, ordenados por `orden`.
- `UserRepository`: orquesta auth + perfil + persistencia de sesión. `login(email, password, rememberMe)` encadena sign-in + fetch de perfil; si `rememberMe=true`, guarda el JSON de sesión en `SessionStorage`. `logout()` hace sign-out y borra la sesión guardada. `tryRestoreSession()` carga el JSON de sesión desde `SessionStorage`, importa la sesión en supabase-kt (con auto-refresh del token) y retorna `RemoteUser?`.

**RemoteDataSources:**

- `AuthRemoteDataSource`: wrappea Supabase Auth SDK. `signIn()` y `signUp()` retornan `Result<Unit>`. `getCurrentSessionJson()` serializa la sesión activa a JSON. `tryImportSession(json)` deserializa e importa la sesión con `auth.importSession(session, autoRefresh = true)`, que renueva el access token si expiró.
- `LocalesRemoteDataSource`: `fetchLocales()` trae todos los registros de la tabla `locales` vía PostgREST. `fetchLocalesByIds(ids)` filtra por `id IN (...)` usando el DSL de filtros de supabase-kt.
- `MenuRemoteDataSource`: `fetchMenuForLocal(localId)` filtra `menu_items` por `local_id = localId` y ordena por `orden`.
- `ProfileRemoteDataSource`: `fetchProfile()` retorna el perfil del usuario autenticado desde la tabla `perfiles` (RLS garantiza que solo se retorna el propio perfil).

**DTOs:**

- `Perfil`: `id`, `nombre`, `email`, `rol`, `favoritos: List<String>`, `descuento1ra: Boolean` (`@SerialName("descuento_1ra")`), `activo: Boolean` — mapea la tabla `perfiles` incluyendo favoritos, elegibilidad al descuento de bienvenida y el flag de baja lógica. `ProfileRemoteDataSource` también expone `updateProfile(nombre, email)` (valida email único contra otros perfiles → `EmailAlreadyInUseException`; solo cambia la tabla `perfiles`, no el email de auth/login) y `deactivateAccount()` (baja lógica `activo = false`).
- `LocalDto`: mapea la tabla `locales`. `toDomain()` convierte al modelo `Local`.
- `MenuItemDto`: mapea la tabla `menu_items`. Campos: `id`, `local_id`, `emoji`, `nombre`, `descripcion`, `precio`, `categoria`, `es_popular`, `disponible`, `orden`. `toDomain()` convierte al modelo `MenuItem`.

**Persistencia de sesión (`SessionStorage`):**

Guarda el JSON de la sesión de Supabase en un DataStore propio (`qless_session`). Expone tres operaciones: `save(json)`, `load(): String?` y `clear()`. El JSON almacenado incluye `access_token` y `refresh_token`. Al restaurar, supabase-kt renueva automáticamente el access token si está vencido usando el refresh token (que en Supabase dura 60 días por defecto).

**Supabase (Postgres):**

- Tabla `perfiles`: `id` (uuid FK → `auth.users`), `nombre`, `email`, `rol` (`USER` | `BACK_OFFICE`), `favoritos` (`uuid[]`, default `{}`), `descuento_1ra` (`boolean`, NOT NULL, default `true`), `activo` (`boolean`, NOT NULL, default `true` — baja lógica). RLS habilitado: SELECT con `using(true)`, UPDATE con `auth.uid() = id`. Trigger `on_auth_user_created` inserta automáticamente al registrarse (con `descuento_1ra = true` y `activo = true` por el default). **Baja lógica de cuenta:** "Eliminar cuenta" setea `activo = false` (`deactivateAccount`) y cierra sesión; login y restore rechazan perfiles con `activo = false` (`AccountInactiveException`). **Edición de perfil:** nombre/email se persisten vía `updateProfile` con validación de email único. El descuento de bienvenida (10%) se aplica una sola vez: el carrito/pago lo descuentan solo si `descuento_1ra = true`, el total persistido del pedido ya viene rebajado, y al confirmar el primer pedido se setea `descuento_1ra = false` (`ProfileRemoteDataSource.consumeFirstOrderDiscount`), por lo que nunca se vuelve a aplicar.
- Tabla `locales`: `id` (uuid PK), `nombre`, `emoji`, `categoria`, `barrio`, `direccion`, `rating`, `tiempo_entrega`, `abierto`, `tiene_promo`, `destacado`. RLS con `using(true)`.
- Tabla `menu_items`: `id` (uuid PK), `local_id` (uuid FK → `locales(id)` ON DELETE CASCADE), `nombre`, `descripcion`, `emoji`, `precio` (integer), `categoria`, `es_popular` (boolean), `disponible` (boolean), `orden` (integer). RLS con `using(true)`. Cada local tiene su propia carta; los ítems se filtran por `local_id`.

**Modo offline (RF4) — caché de locales y menú en Room:**

`LocalesRepositoryImpl` y `MenuRepositoryImpl` implementan una estrategia
*network-first con fallback a caché*:

1. Piden los datos al `RemoteDataSource` (Supabase).
2. **Éxito:** reescriben la copia local en Room (`LocalDao.replaceAll` /
   `MenuItemDao.replaceForLocal`) y devuelven los datos frescos.
3. **Falla de red:** leen la última copia de Room. Si hay caché, la sirven; si
   está vacía, recién ahí propagan el error.

El origen del dato viaja a la UI envuelto en `domain/model/CachedResult<T>`
(`data` + `fromCache`). Cuando `fromCache == true`, los ViewModels
(`MisLocales`, `Menu`, `Home`) marcan `isOffline` y las pantallas muestran el
componente `OfflineBanner` ("Sin conexión — mostrando los últimos datos
guardados"). Un `Result.failure` significa que falló la red **y** no había caché.

Entidades Room dedicadas: `LocalEntity` (tabla `locales`) y `MenuItemEntity`
(tabla `menu_items`, con columna `orden` para reconstruir la carta en el orden
original). La caché se llena sola al navegar online; el menú se cachea por local.

Además, `LocalesRepositoryImpl.getLocales()` tiene una **caché en memoria con
single-flight** (TTL 30 s, protegida por `Mutex`): Home (cálculo del local más
cercano) y MisLocales piden la misma lista casi simultáneamente, y al ser el repo
un singleton del `AppModule` el segundo colector reusa lo que trajo el primero en
vez de pegarle de nuevo a la red. Es ortogonal al fallback offline: la copia en
memoria es siempre dato fresco (`fromCache = false`).

> Alcance: el modo offline cubre **lectura** (catálogo de locales y menús). Una
> cola de tareas offline para **escritura** queda pendiente hasta que los pedidos
> persistan en backend (A1) — hoy no habría operación de escritura que encolar.

**Ubicación / cercanía (GPS):**

La tabla `locales` tiene columnas `latitud`/`longitud` (mapeadas por `LocalDto`)
que se persisten también en la caché Room (`LocalEntity`, DB v7) para que la
distancia funcione offline. Locales con `(0,0)` se consideran "sin ubicación".

- **`LocationProvider`** (contrato en `domain/location`, implementación
  `FusedLocationProvider` en `data/location`) abstrae el SDK de Play Services:
  expone `suspend fun currentLocation(): Coordinates?` (null si no hay permiso o
  fix). Así los ViewModels/Composables no dependen de Android Location.
- **`domain/usecase/LocationUseCases.kt`** — Kotlin puro y testeable:
  `haversineMeters(...)`, `RankLocalsByDistanceUseCase` (anota `distanciaMetros`
  en cada local y ordena por cercanía; los sin ubicación quedan al final) y
  `GetCurrentLocationUseCase`. Constante `NEARBY_THRESHOLD_METERS = 50`.
- **Flujo:** los Composables (`Home`, `MisLocales`) piden el permiso con
  accompanist; al concederse, llaman `viewModel.refreshNearestLocal()`, que
  obtiene la ubicación y rankea los locales.

**Comportamiento según distancia al más cercano:**

- **≤ 50 m (estás en el local):**
  - Tras login / abrir la app con sesión, `AppNavigation` muestra una vez por
    sesión la pantalla **`LocationDetectedScreen` ("¿Estás en {nombre}?")**, ya
    **data-driven** (nombre, emoji, rating, distancia reales — sin hardcode). El
    gate vive en el composable de `Home` (`LaunchedEffect` sobre `closestLocal`)
    con guarda `locationPromptShown` (`rememberSaveable`).
  - En `Home`, la card del local cambia su título a **"¿ESTÁS ACÁ?"**.
- **> 50 m:**
  - `Home` **no** muestra la card.
  - `MisLocales` muestra el local **debajo del buscador y arriba de los filtros**
    con el texto **"EL MÁS CERCANO A VOS"**, y soporta el sort "Más cercano".

`NEARBY_THRESHOLD_METERS = 50` es la fuente única del umbral.

**Rendimiento del fix:** `FusedLocationProvider` usa primero la **última ubicación
conocida** (`lastLocation`, instantánea) si es reciente (< 2 min); solo si no hay,
pide un fix nuevo de alta precisión (`getCurrentLocation`, más lento). Esto acelera
mucho la aparición de la pantalla "¿Estás en X?".

**Mapa real:** `LocationDetectedScreen` usa **Google Maps Compose** dentro de un
`BottomSheetScaffold` (sheet arrastrable). El mapa es **interactivo** (scroll/zoom),
centrado en las coordenadas del local con un marker; `contentPadding` empuja el
centro por encima del sheet para que el pin quede centrado en el área visible. Se
ocultan los POIs con un `MapStyleOptions` (menos ruido). Si el local no tiene
coordenadas, cae a un mapa decorativo. La API key se inyecta en el manifest vía
`manifestPlaceholders["MAPS_API_KEY"]` desde `local.properties` (`maps.api.key`) o
el env `MAPS_API_KEY` — **no se commitea** (consigna). Deps:
`com.google.maps.android:maps-compose` + `com.google.android.gms:play-services-maps`.

---

**Escaneo de QR de local (RF6):**

La cámara lee QR con **CameraX + ML Kit** (`ScanearQrScreen` / `CameraPreview`), que
es un sensor "tonto": solo reporta el texto crudo del código vía `onQrDetected`. El
contenido del QR es **el id (UUID) del local**.

La regla de negocio "¿este QR corresponde a un local registrado?" no vive en la
navegación sino en dominio + un ViewModel dedicado:

- `GetLocalByIdUseCase` → `LocalesRepository.getLocalById(id)` valida la existencia
  contra Supabase (con fallback a caché Room para que funcione offline si el local
  ya fue visitado).
- `QrScanViewModel` (scopeado al backstack entry de `ScanQr`) recibe el valor crudo
  en `onQrScanned(raw)`: lo normaliza (`trim`), descarta lo que no sea un UUID **sin
  tocar la red**, y emite un evento one-shot por `SharedFlow<QrScanEvent>`:
  `Resolved(localId)` o `NotRecognized`. Se ignoran lecturas repetidas (ML Kit
  dispara por frame) con un guard interno.
- `AppNavigation` colecta el evento: `Resolved` → `navigateToMenu(localId)` (reusa el
  mismo gate de conflicto de carrito que el resto del flujo); `NotRecognized` →
  `QrNoReconocidoScreen`. Esto reemplazó la heurística previa (`qrData == "error"` y
  `length == 36`), que ni validaba existencia ni manejaba un QR ajeno a QLess.

**Recuperación de contraseña (link por mail):**

Flujo de "Olvidé mi contraseña" sobre Supabase Auth, en dos pasos conectados por un
**deep link**:

1. **Pedir el mail:** `ForgotPasswordScreen` toma el correo y dispara
   `UserRepository.sendPasswordReset(email)` → `auth.resetPasswordForEmail(email,
   redirectUrl = "qless://reset-password")`. La UI muestra un mensaje **neutro** ("si el
   correo está registrado, te enviamos un enlace") para no filtrar qué correos existen.
2. **Volver del mail:** el link rebota a la app por el deep link `qless://reset-password`
   (esquema propio declarado en el `AndroidManifest` con `launchMode=singleTop`). En
   `SupabaseClient` el plugin `Auth` se configura con `scheme`/`host` y
   `flowType = IMPLICIT` (el token viaja en el fragment, sin code-verifier que deba
   sobrevivir al cierre de la app). `MainActivity` le pasa el intent a
   `supabase.handleDeeplinks(...)`, que abre una **sesión de recuperación** y emite la
   señal `openResetPasswordSignal` (mismo patrón que el deep link de notificaciones).
3. **Nueva contraseña:** `AppNavigation` navega a `ResetPasswordScreen` (gateando el
   Splash para no pisar el destino en cold start). La pantalla llama
   `updatePassword(newPassword)` → `auth.updateUser { password = ... }`, que **persiste el
   hash nuevo en `auth.users`**; luego el repositorio hace `signOut` + limpia la sesión
   guardada y la app vuelve a Login para entrar con la credencial nueva.

La lógica vive en `PasswordResetViewModel` (separado de `AuthViewModel` a propósito: así
la sesión de recuperación transitoria nunca toca el estado de auth global ni dispara
navegación a Home). La URL de redirect es única en
`AuthRemoteDataSource.PASSWORD_RESET_REDIRECT` y debe coincidir con el `intent-filter` del
manifest y con la allowlist de **Redirect URLs** del proyecto Supabase
(`Authentication → URL Configuration`). El cambio toca **solo `auth.users`**, no la tabla
`perfiles` ni Room.

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
| `CartViewModel` | `CartUiState` | Colecciona el Flow del repositorio de carrito. Expone `cartLocalId: String` (id del local del carrito activo). `addItem` incluye `localId` para asociar cada ítem a su local. |
| `PaymentMethodViewModel` | `PaymentMethodUiState` | Observa los métodos guardados. MVP cash-only: **no** siembra métodos por defecto; la lista arranca vacía. |
| `MenuViewModel` | `MenuUiState` | Carga el menú real del local desde Supabase con `loadMenu(localId)`. Deriva categorías y `selectedCategory` inicial de los ítems recibidos. Scoped por backstack entry. |
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

data class MenuUiState(
    val isLoading: Boolean = true,
    val items: List<MenuItem> = emptyList(),
    val selectedCategory: String = "",
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
- `HomeScreen`: recibe `HomeViewModel` y `onLocalSelected: (localId: String) -> Unit`. Muestra los locales favoritos cargados desde Supabase; tocar una card navega directamente al menú de ese local (no a la lista de locales).
- `MisLocalesScreen`: recibe `MisLocalesViewModel`. Lista de locales desde Supabase con skeleton de carga. Banner de geolocalización con botón "No" funcional que oculta el banner mediante estado local (`var showGeoBanner`). Tocar "Sí" navega directo al menú del local detectado.
- `MenuScreen`: recibe `local: Local?` y `menuViewModel`. El header muestra `emoji`, `nombre`, `categoria`, `barrio`, `rating` y `tiempoEntrega` del local real. El chip de promo solo aparece si `tienePromo == true`. Los ítems y categorías provienen de `MenuUiState.items` cargados desde Supabase; el filtro por categoría está completamente conectado.
- `CerrarSesionScreen` / `EliminarCuentaScreen`: muestran nombre y email del usuario activo desde `AuthUiState`.

### Navegación

`AppNavigation.kt` define un `NavHost` con 30 rutas. La mayoría de ViewModels se instancian una única vez a nivel de `AppNavigation`; `MenuViewModel` es la excepción — se instancia dentro del composable de `Screen.Menu` para que quede scoped al backstack entry.

**ViewModels instanciados en AppNavigation:**

```kotlin
val authViewModel: AuthViewModel = viewModel()
val cartViewModel: CartViewModel = viewModel()
val orderViewModel: OrderViewModel = viewModel()
val paymentViewModel: PaymentMethodViewModel = viewModel()
val misLocalesViewModel: MisLocalesViewModel = viewModel()
val homeViewModel: HomeViewModel = viewModel()
val notificationViewModel: NotificationViewModel = viewModel()
// MenuViewModel se crea dentro del composable Screen.Menu (scoped por entry)
```

**Ruta del menú con argumento:**

```kotlin
object Menu : Screen("menu/{localId}") {
    fun route(localId: String) = "menu/$localId"
}

composable(
    route = Screen.Menu.route,
    arguments = listOf(navArgument("localId") { type = NavType.StringType })
) { backStackEntry ->
    val localId = backStackEntry.arguments?.getString("localId") ?: ""
    val local = misLocalesViewModel.uiState.value.locales.firstOrNull { it.id == localId }
    val menuViewModel: MenuViewModel = viewModel()
    LaunchedEffect(Unit) { menuViewModel.loadMenu(localId) }
    MenuScreen(cartViewModel, menuViewModel, local, ...)
}
```

**Protección de carrito por local (conflicto de pedido):**

Toda navegación al menú pasa por la función `navigateToMenu(localId, popUpRoute?)` definida en `AppNavigation`. Si el carrito ya contiene ítems de un local diferente, se muestra un `AlertDialog` con dos opciones:

- **"Nuevo pedido"**: limpia el carrito y navega al menú del nuevo local.
- **"Mantener pedido"**: cancela la navegación y preserva el carrito actual.

```kotlin
fun navigateToMenu(localId: String, popUpRoute: String? = null) {
    val cartLocalId = cartViewModel.cartLocalId
    if (cartLocalId.isNotEmpty() && cartLocalId != localId && items.isNotEmpty()) {
        pendingLocalId = localId   // dispara el AlertDialog
        pendingPopUpRoute = popUpRoute
    } else {
        // navegación directa
    }
}
```

**Bloqueo de carrito nuevo con pedido en curso:**

Mientras el usuario tiene un pedido activo (`status ∈ {pending, preparing, ready}`)
no puede empezar un carrito nuevo. La fuente única de verdad es la extensión
`OrdersUiState.activeOrder()` (en `OrderViewModel.kt`, junto a la constante
`ACTIVE_ORDER_STATUSES`), que reemplazó las derivaciones duplicadas dispersas por
las pantallas. El gate se aplica en el único punto donde nace un carrito —el
`Agregar` de `MenuScreen`—: `AppNavigation` calcula `blockNewCart =
orderState.activeOrder() != null` (y refresca `loadUserOrders()` al entrar al menú,
para que funcione incluso entrando por QR). Con `blockNewCart == true`, `MenuScreen`
muestra un banner ("Ya tenés un pedido en curso…") con CTA **"Ver pedido"** → Tracking
y deshabilita los botones `+`. No se acopla la lógica de pedidos a `CartViewModel`:
la regla vive en la capa de coordinación + UI. El invariante "pedido activo ⇒ carrito
vacío" se sostiene porque confirmar un pedido limpia el carrito.

**Card de carrito activo (`ActiveCartCard`):**

Cuando hay ítems en el carrito, `AppNavigation` arma un descriptor `ActiveCartUi`
(localId, nombre y emoji del local —resueltos contra la lista de `misLocalesViewModel`—,
cantidad de productos y total) y lo inyecta en las pantallas de inicio (`HomeScreen`,
debajo del banner de pedido en curso), `MisLocalesScreen` y `MisPedidosScreen`. El
componente reutilizable `ui/components/ActiveCartCard.kt` muestra "CARRITO ACTIVO", el
local y el resumen; el botón **"Ver"** invoca `navigateToMenu(localId)` para volver al
menú de ese local (al coincidir el local del carrito, no dispara el diálogo de conflicto).
El descriptor es `null` cuando el carrito está vacío, así que la card no se renderiza.

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
    HomeScreen(
        homeViewModel = homeViewModel,
        onLocalSelected = { localId -> navigateToMenu(localId) },
        ...
    )
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
              → Selección de local (GPS / QR / lista)
              → Menú (cargado desde Supabase para ese local)
              → Carrito → Pago
              → OrderConfirmed → Tracking → OrderReady → PickupSuccess
```

---

## Estado de los Datos

| Dato | Fuente | Persistencia |
|---|---|---|
| Sesión de usuario | Supabase Auth | DataStore (`qless_session`) si "Mantener sesión" activo |
| Perfil (`nombre`, `email`, `rol`) | Supabase `perfiles` | En memoria (leído en cada login/restore) |
| Favoritos del usuario (`favoritos uuid[]`) | Supabase `perfiles` | En `AuthUiState` durante la sesión |
| Locales gastronómicos | Supabase `locales` | Room (`locales`) como caché; fallback offline (RF4) |
| Menú del local | Supabase `menu_items` | Room (`menu_items`) como caché; fallback offline (RF4) |
| Carrito | Room (con `localId` por ítem) | Sobrevive a reinicios |
| Métodos de pago | Room | Sobrevive a reinicios |
| Tema oscuro / onboarding | DataStore (`qless_settings`) | Permanente |
| Pedidos (`orders` + `order_items`) | Supabase PostgREST | Cambios en vivo vía **Supabase Realtime** (Postgres Changes) |
| Notificaciones de pedido | Generadas localmente al detectar cambios de estado | Room (`notifications`, scoped por `userId`) |

---

## Tiempo real y notificaciones

### Estado de pedidos en vivo (Supabase Realtime)

El estado de los pedidos se actualiza por **Supabase Realtime** (Postgres Changes),
reemplazando el polling anterior (`getUserOrders()` cada 10 s):

- `SupabaseClient` instala `Realtime` (además de `Auth` + `Postgrest`). En Supabase,
  la tabla `orders` está en la publication `supabase_realtime`.
- `OrderRemoteDataSource` expone `observeUserOrderChanges()` (filtro `user_id`) y
  `observeLocalOrderChanges()` (filtro `local_id`) como `Flow<Unit>` de *señal*: emiten
  al (re)suscribirse y por cada evento. La señal dispara un **re-fetch** de la query REST
  existente (que ya trae los embeds `order_items`/`locales`), en vez de confiar en el
  payload del evento.
- Contratos en `OrderRepository`; casos de uso `Observe{User,Local}OrderChangesUseCase`.
- `OrderViewModel.observeUserOrders()` / `observeLocalOrders()` son `suspend` y se
  colectan según el ciclo de vida. El canal del **cliente** vive a nivel app mientras hay
  sesión + foreground (`ProcessLifecycleOwner.repeatOnLifecycle(STARTED)`); el del
  **BackOffice** está scopeado a sus pantallas.
- Limitación: Realtime entrega solo en foreground. Al volver de background se re-fetchea.

### Notificaciones de cambio de estado

Side-effect del mismo flujo del cliente, sin polling nuevo:

- `OrderViewModel` mantiene `lastKnownStatuses` y, ante una transición real (no la carga
  inicial ni el `picked_up` propio), llama `NotifyOrderUpdateUseCase(order)`.
- El use case **persiste** un `AppNotification` en Room (`notifications`, scoped por
  `userId`) **y** publica en la bandeja vía `SystemNotifier` (interface en `domain/`,
  impl `AndroidSystemNotifier` con `NotificationManager` + canal `order_status`). Permiso
  `POST_NOTIFICATIONS` (Android 13+) pedido en runtime.
- `NotificationCenterScreen` (ruta nueva) lista los avisos, marca leídos al abrir y permite
  borrar; la campana de Home navega ahí y muestra **badge** de no-leídas
  (`NotificationViewModel.unreadCount`). `NotificacionesScreen` queda como preferencias.
- Tap en la notificación → abre la app en el seguimiento (deep-link vía `MainActivity`).
- Limitaciones: sin FCM no hay push con la app cerrada; avisos locales por device; solo cliente.

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
supabase                             = "3.1.4"   # BOM: auth-kt + postgrest-kt + realtime-kt
ktor                                 = "3.1.3"   # ktor-client-okhttp
```

> `lifecycle-process` (mismo ref que `lifecycleRuntimeKtx`) se usa para mantener el
> canal Realtime del cliente vivo solo en foreground (`ProcessLifecycleOwner`).

---

## Pendiente para Entrega 2

### Backend e integración de red

- ~~Conectar locales a Supabase PostgREST~~ ✓ (tabla `locales` + RLS)
- ~~Favoritos del usuario desde Supabase~~ ✓ (columna `favoritos uuid[]` en `perfiles`)
- ~~Conectar menú a Supabase PostgREST~~ ✓ (tabla `menu_items` + RLS + FK a `locales`)
- Conectar pedidos a Supabase PostgREST.
- ~~Manejar errores de conectividad y modo offline básico (RF4): cachear el último menú y locales en Room~~ ✓ (ver "Modo offline" abajo). Pendiente: cola de tareas offline para escritura (cuando exista persistencia de pedidos / A1).

### Autenticación

- ~~Autenticación real con email/password~~ ✓ (Supabase Auth)
- ~~Perfil de usuario desde Postgres~~ ✓ (tabla `perfiles` + RLS + trigger)
- ~~Persistencia de sesión entre reinicios~~ ✓ (SessionStorage + DataStore + importSession)
- Google Sign-In real (RF3): pendiente.
- Cachear perfil en Room para lectura offline.
- Eliminar cuenta: requiere Supabase Edge Function con service role (actualmente solo hace sign-out).
- Agregar / quitar favoritos desde la UI (hoy solo se leen, la modificación se hace desde SQL).

### Clean Architecture — IMPLEMENTADO

La capa de dominio ya existe y el código respeta la regla de dependencias del
diagrama de `ARCHITECTURE.md`:

- `domain/model/` — modelos puros de negocio (`Order`, `MenuItem`, `Local`,
  `CartItem`, `PaymentMethod`, `User`, `AuthUser`). Sin anotaciones de
  serialización ni de Room.
- `domain/repository/` — **contratos** (interfaces) de repositorio.
- `domain/usecase/` — casos de uso agrupados por dominio (`OrderUseCases`,
  `MenuUseCases`, `LocalesUseCases`, `CartUseCases`, `PaymentUseCases`,
  `AuthUseCases`, `ThemeUseCases`). El dominio principal (pedidos) concentra la
  orquestación de reglas (p. ej. `PlaceOrderUseCase` valida carrito no vacío).
- `data/repository/` — `…RepositoryImpl` que **implementan** los contratos de
  dominio, delegando en los data sources remotos (Supabase) y locales (Room).
- `di/AppModule.kt` — composition root manual: arma el grafo
  impl → contrato → caso de uso. Se inicializa en `MainActivity.onCreate`.

Los ViewModels son `ViewModel` planos que dependen de casos de uso obtenidos de
`AppModule`, no de clases concretas de la capa de datos.

> Nota: el árbol de archivos de la sección "Estructura del proyecto" (más arriba)
> todavía refleja el layout previo (todo bajo `data/`) y debe regenerarse.

### Inyección de dependencias (Hilt)

- El cableado hoy es un composition root manual (`di/AppModule.kt`), suficiente
  para invertir dependencias y testear con fakes. Migrar a Hilt es opcional:
  reemplazaría `AppModule` por módulos `@Provides` y `@HiltViewModel`.

### Pruebas y métricas (RF5) — IMPLEMENTADO (falta correr)

**Testabilidad.** Los ViewModels que se testean exponen un constructor primario
con sus casos de uso (inyección) y un constructor secundario sin args que delega
en `AppModule` para que `viewModel()` siga funcionando en producción. Así el test
inyecta fakes sin tocar el grafo global (`OrderViewModel`, `CartViewModel`,
`HomeViewModel`, `MisLocalesViewModel`, `MenuViewModel`).

**Unit tests (JVM, `app/src/test`).** JUnit4 + `kotlinx-coroutines-test` +
repositorios fake (`com/qless/fakes/FakeRepositories.kt`). `MainDispatcherRule`
reemplaza `Dispatchers.Main` por un `UnconfinedTestDispatcher`. Cubren:
flujo de pedidos (carga, filtros por estado, `activeOrder()`, checkout
success/error, pickup, update de estado), carrito (observación, alta/baja de
cantidad, `cartLocalId`, limpiar) y el mapeo `CachedResult → isOffline` + errores
en locales/menú/favoritos. Se decidió usar **fakes en vez de MockK** (deterministas,
sin dependencia extra a resolver).

**Test de composable stateless (`app/src/androidTest`).** `ActiveCartCardTest`
verifica render y callback de `ActiveCartCard` con Compose Testing.

**Métricas no funcionales.** `scripts/measure-metrics.sh` mide cold start
(`am start -W`) y jank/fps (`dumpsys gfxinfo`) por `adb` sobre la release;
evidencia en `documentation/metrics/results.md`. Objetivos: cold start < 2.5 s,
scroll > 54 fps (jank < 10%) en Pixel 9 Pro. Se difirió el módulo Macrobenchmark
(cablear un módulo Gradle nuevo sin entorno de build es riesgoso); el método `adb`
da la misma evidencia para H2.

> Pendiente de **ejecución**: `./gradlew testDebugUnitTest` (unit),
> `connectedDebugAndroidTest` (instrumentados) y correr el script de métricas en
> el dispositivo. No hay JDK/dispositivo en el entorno donde se escribieron.

### Accesibilidad

- Auditar `contentDescription` en todos los elementos interactivos.
- Verificar tamaños de fuente escalables y contraste en dark mode.

### Sensor real

- ~~Integrar CameraX + ML Kit para lectura real de QR~~ ✓ (CameraX + ML Kit implementado)

### Migración de base de datos

- Reemplazar `fallbackToDestructiveMigration()` por migraciones Room explícitas antes del RC.
