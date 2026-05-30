# QLess — Documentacion Tecnica

## Alcance del Proyecto

### Problema que resuelve

En locales gastronimicos de alta demanda, los usuarios deben hacer filas en caja para realizar su pedido, lo que genera fricciones en la experiencia y desperdicios de tiempo tanto para el cliente como para el local. QLess actua como un beeper virtual: el usuario arma y confirma su pedido desde su celular, y la app le notifica cuando esta listo para retirar.

### Usuarios objetivo

Dos actores principales:

- **Cliente final:** usuario que escanea el QR de la mesa o selecciona un local, arma su pedido, paga in-app y hace seguimiento del estado en tiempo real.
- **Operador del local (BackOffice):** encargado de caja o cocina que gestiona los pedidos entrantes, actualiza su estado y confirma el retiro.

### Casos de uso principales (Entrega 1)

1. Onboarding inicial con presentacion de la propuesta de valor y descuento de bienvenida del 10 %.
2. Registro e inicio de sesion (email/contrasena y Google).
3. Deteccion de local por GPS o escaneo de QR de mesa.
4. Navegacion del menu del local y armado del carrito.
5. Confirmacion del pedido y seleccion del metodo de pago.
6. Seguimiento del estado del pedido (beeper virtual) con timeline en tiempo real.
7. Confirmacion de retiro y calificacion del local.
8. Gestion de pedidos desde la vista BackOffice del local.

### Flujos de pantallas implementados

El prototipo cubre tres flujos diferenciados, cumpliendo RF2:

- **Flujo de autenticacion:** Splash → Onboarding → Login / Registro → Google Auth
- **Flujo de pedido:** Deteccion GPS / Scan QR → Seleccion de local → Menu → Carrito → Pago → Pedido confirmado → Seguimiento → Pedido listo → Retiro exitoso
- **Flujo de gestion:** BackOffice → Historial de pedidos → Actualizacion de estado de pedido

### Sensor utilizado

Camara del dispositivo para escaneo de codigos QR de mesa (requisito RF6).

### Design System

| Token | Valor |
|-------|-------|
| Crema calida | `#FFFBF5` — fondo principal |
| Mantequilla | `#FFF8EE` — superficies / cards |
| Espresso | `#2C1A0E` — texto principal |
| Madera | `#7A5C3E` — texto secundario |
| Pimenton | `#C44B1B` — CTA principal |
| Tomate | `#E8673A` — hover / destacados |
| Melocoton | `#FFEDE0` — chips / tags |
| Albahaca | `#1A7A4A` — entregado / disponible |
| Azafran | `#D4870E` — en preparacion |
| Arandano | `#1D6FA8` — en camino / tracking |
| Borgona | `#8C2A2A` — agotado / error |

Tipografia: **Sora** (titulos y UI primaria) + **Plus Jakarta Sans** (cuerpo y labels).

---

## Descripcion General

QLess es una aplicacion Android nativa orientada al pedido anticipado en locales gastronimicos. El usuario escanea un codigo QR o selecciona un local, arma su pedido desde el menu, lo confirma y realiza el pago desde la aplicacion, evitando filas en el punto de venta. El local recibe el pedido y notifica cuando esta listo para retirar.

---

## Stack Tecnologico

| Elemento | Tecnologia |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navegacion | Navigation Compose |
| Estado y ciclo de vida | ViewModel (AndroidViewModel), Compose State |
| Persistencia (Entrega 1) | SharedPreferences + org.json |
| Build system | Gradle con Version Catalogs (libs.versions.toml) |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |

---

## Estructura del Proyecto

```
com.qless
├── MainActivity.kt
├── navigation/
│   └── AppNavigation.kt
├── data/
│   ├── CartItem.kt
│   ├── CartRepository.kt
│   ├── PaymentMethod.kt
│   └── PaymentMethodRepository.kt
├── ui/
│   ├── viewmodel/
│   │   ├── CartViewModel.kt
│   │   └── PaymentMethodViewModel.kt
│   ├── components/
│   │   └── QLessBottomNav.kt
│   ├── screens/
│   │   ├── SplashScreen.kt
│   │   ├── OnboardingScreen.kt
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── GoogleLoginScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── LocationDetectedScreen.kt
│   │   ├── MisLocalesScreen.kt
│   │   ├── ScanearQrScreen.kt
│   │   ├── QrNoReconocidoScreen.kt
│   │   ├── MenuScreen.kt
│   │   ├── CartScreen.kt
│   │   ├── PaymentScreen.kt
│   │   ├── AgregarMetodoDePagoScreen.kt
│   │   ├── MetodosDePagoScreen.kt
│   │   ├── OrderConfirmedScreen.kt
│   │   ├── TrackingScreen.kt
│   │   ├── OrderReadyScreen.kt
│   │   ├── PickupSuccessScreen.kt
│   │   ├── OrderSummaryScreen.kt
│   │   ├── MisPedidosScreen.kt
│   │   ├── AjustesScreen.kt
│   │   ├── NotificacionesScreen.kt
│   │   ├── MetodosDePagoScreen.kt
│   │   ├── EliminarCuentaScreen.kt
│   │   ├── CerrarSesionScreen.kt
│   │   ├── BackOfficeScreen.kt
│   │   ├── BackOfficeHistoryScreen.kt
│   │   └── BackOfficeUpdateOrderScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
```

---

## Arquitectura Actual (Entrega 1)

La aplicacion implementa una arquitectura de dos capas con patron MVVM.

### Capa de Datos (`data/`)

Contiene los modelos de dominio y los repositorios que acceden a SharedPreferences.

**Modelos:**

- `CartItem`: representa un producto en el carrito. Campos: `emoji`, `name`, `detail`, `unitPrice`, `quantity`.
- `PaymentMethod`: representa un metodo de pago guardado. Campos: `id`, `tipo`, `nombre`, `ultimosDigitos`, `vencimiento`, `esPrincipal`, `esBilletera`.

**Repositorios:**

- `CartRepository`: serializa y deserializa la lista de `CartItem` en SharedPreferences (`qless_cart`) usando `org.json`.
- `PaymentMethodRepository`: serializa y deserializa la lista de `PaymentMethod` en SharedPreferences (`qless_payment_methods`) usando `org.json`.

### Capa de Presentacion (`ui/`)

**ViewModels:**

- `CartViewModel` (`AndroidViewModel`): expone `items: SnapshotStateList<CartItem>`. Provee `addItem()`, `removeItem()` y `clearCart()`. Persiste el estado en cada modificacion.
- `PaymentMethodViewModel` (`AndroidViewModel`): expone `methods: SnapshotStateList<PaymentMethod>`. Provee `addMethod()` y `removeMethod()`. Al iniciarse por primera vez siembra tres metodos por defecto (Visa, Mercado Pago, Mastercard).

**Screens con estado persistido:**

- `MenuScreen`: lee cantidades desde `CartViewModel` y delega las acciones de agregar/quitar al mismo.
- `CartScreen`: renderiza `CartViewModel.items` directamente. El carrito inicia vacio si no hay datos guardados.
- `MetodosDePagoScreen`: renderiza `PaymentMethodViewModel.methods`.
- `AgregarMetodoDePagoScreen`: invoca `PaymentMethodViewModel.addMethod()` al confirmar el formulario y regresa a la pantalla anterior.

### Navegacion

`AppNavigation.kt` define un `NavHost` con 28 rutas tipadas mediante la sealed class `Screen`. Los ViewModels se instancian una unica vez dentro de `AppNavigation` con `viewModel()` y se pasan como parametros a las pantallas que los necesitan, garantizando un estado compartido y consistente durante toda la sesion.

---

## Flujo Principal del Usuario

```
Splash -> Onboarding -> Login / Registro
       -> LocationDetected / Scan QR
       -> Menu (armar pedido)
       -> Cart (revisar pedido)
       -> Payment (seleccionar metodo de pago)
       -> OrderConfirmed
       -> Tracking
       -> OrderReady -> PickupSuccess
```

---

## Limitaciones de la Entrega 1

- La persistencia se basa en SharedPreferences con serializacion manual a JSON. Esta solucion es funcional pero no escala ante modelos complejos ni relaciones entre entidades.
- No existe una capa de dominio (casos de uso). Los ViewModels acceden directamente a los repositorios concretos, lo que acopla la presentacion a la implementacion de datos.
- No se aplica inyeccion de dependencias. Los repositorios son instanciados dentro de los ViewModels.
- Los datos de menu y locales son estaticos (hardcodeados). No hay integracion con backend.

---

## Plan para la Entrega 2

### Migracion de Persistencia: SharedPreferences a Room

Se reemplazaran los repositorios basados en SharedPreferences por una base de datos local usando **Room** (parte de Android Jetpack).

Cambios previstos:

- Definicion de entidades Room (`@Entity`) para `CartItem` y `PaymentMethod`.
- Creacion de DAOs (`@Dao`) con operaciones CRUD.
- Instancia unica de la base de datos mediante `RoomDatabase`.
- Los repositorios pasaran a depender de los DAOs en lugar de `SharedPreferences`.

### Implementacion de Clean Architecture

Se introducira una capa de dominio entre la presentacion y los datos, siguiendo el esquema de tres capas de Clean Architecture:

```
ui (presentacion)
    |
domain (casos de uso + interfaces de repositorio)
    |
data (implementaciones de Room, API, etc.)
```

Cambios previstos:

- Creacion del paquete `domain/` con interfaces de repositorio (`ICartRepository`, `IPaymentMethodRepository`) y casos de uso (`AddCartItemUseCase`, `GetCartItemsUseCase`, `AddPaymentMethodUseCase`, etc.).
- Los ViewModels dependerin de los casos de uso, no de los repositorios concretos.
- Los repositorios concretos implementarin las interfaces definidas en `domain/`.
- Incorporacion de **Hilt** para inyeccion de dependencias, eliminando la instanciacion manual de repositorios dentro de los ViewModels.

---

## Dependencias Principales

```toml
# libs.versions.toml
androidx-lifecycle-runtime-ktx       = "2.10.0"
androidx-lifecycle-viewmodel-compose = "2.10.0"
androidx-navigation-compose          = "2.9.0"
androidx-compose-bom                 = "2024.09.00"
kotlin                               = "2.2.10"
agp                                  = "9.1.1"
```
