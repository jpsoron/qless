# QLess — Pendientes de UI

---

### Mis Locales

- [X] **Buscador funcional** — el `TextField` "Buscar local..." existe visualmente pero no filtra. Agregar `var query by remember { mutableStateOf("") }` y `.filter { it.nombre.contains(query, ignoreCase = true) }` sobre `uiState.locales`.
- [X] **Chips de categoría** — filtrar la lista por `local.categoria` con chips derivados de los datos reales. Estado: `var selectedCategory by remember { mutableStateOf<String?>(null) }`.
- [X] **Ordenar locales** — dropdown con opciones (rating ↓, nombre A-Z, primero los abiertos). Solo `.sortedBy { }` sobre la lista.
- [X] **Cerrar banner de geolocalización** — el botón "No" tiene `onClick = {}`. Ocultar el banner con `var showGeoBanner by remember { mutableStateOf(true) }`.

### Menú

- [x] **Filtro por categoría que realmente filtra** — implementado con datos reales desde Supabase (`menu_items`). Las categorías se derivan dinámicamente de los ítems cargados y el filtrado está conectado a `selectedCategory` en `MenuViewModel`.

### Mis Pedidos

- [X] **Tabs funcionales (Activos / Finalizados / Cancelados)** — los chips están renderizados con `selected = true/false` hardcodeado. Necesitan `var selectedTab by remember { mutableStateOf("Activos") }` y que cada tab muestre su lista correspondiente.
- [X] **Estado vacío por tab** — cuando un tab no tiene pedidos, mostrar un mensaje del tipo "No tenés pedidos finalizados".

### Login

- [X] **"Olvidé mi contraseña"** — flujo real de recuperación por link (Supabase). El `TextButton` navega a `ForgotPasswordScreen` (pide el correo y dispara `resetPasswordForEmail`); el link del mail rebota a la app vía deep link `qless://reset-password` y abre `ResetPasswordScreen` (define la nueva contraseña con `updateUser`). Ver doc técnica § "Recuperación de contraseña".

### Ajustes

- [X] **"Mi perfil" navega a algún lado** — el ítem tiene `onClick = {}` sin destino. Puede navegar a una pantalla nueva o abrir un `BottomSheet` con los datos del usuario (nombre, email) y opción de editar nombre.

### Carrito

- [X] **Vaciar carrito** — no hay botón de "Vaciar todo". Agregar un `AlertDialog` de confirmación + `cartViewModel.clearCart()`.

### Order Ready

- [X] **Placeholder de QR visual** — hay un comentario `// QR Code Placeholder`. Reemplazar con un recuadro decorativo que simule un QR (cuadrados anidados con colores del design system).

---

## Pantallas incompletas

Todas las pantallas existen y están ruteadas en `AppNavigation`. Estas necesitan trabajo adicional:

| Pantalla | Estado actual | Qué falta |
|---|---|---|
| `AjustesScreen` | ✓ "Mi perfil" abre BottomSheet con edición | — |
| `NotificacionesScreen` | Preferencias (toggles cosméticos por ahora) | Los toggles todavía no gatean nada |
| `NotificationCenterScreen` | ✓ Centro real de notificaciones | — |
| `MenuScreen` | ✓ Datos reales desde Supabase | — |
| `MisLocalesScreen` | ✓ Datos reales; buscador, chips y orden funcionales | — |
| `MisPedidosScreen` | ✓ Datos reales con tabs funcionales | — |
| `GoogleLoginScreen` | UI hecha | Los botones no hacen nada real (Google Auth no implementado) |
| `OrderSummaryScreen` | ✓ Datos reales desde `selectedOrder` | — |

---

## Datos hardcodeados / mockeados relevados

### ~~CartScreen~~ ✓ RESUELTO
- ~~`"Big Pons – San Isidro"` hardcodeado en el header del carrito.~~ `CartScreen` ahora recibe `localNombre`, `localEmoji` y `localBarrio`; `AppNavigation` los resuelve desde `misLocalesViewModel` usando `cartViewModel.cartLocalId`.

### PaymentScreen (`PaymentScreen.kt:159`)
- `"Big Pons · X ítems"` hardcodeado. La pantalla no recibe el nombre del local del carrito activo.

### ~~TrackingScreen~~ ✓ RESUELTO
- ~~Contador estático `"12"` + `"~15 min estimados"`.~~ Eliminados. El beeper ahora muestra una etiqueta de estado (`CONFIRMADO` / `EN COCINA` / `¡LISTO!`) derivada del campo `status` del pedido.
- ~~`"Retiro en Caja 1"` hardcodeado.~~ Cambiado a `"Retiro en mostrador"` (no hay campo `pickupPoint` en el modelo `Order`; cuando se agregue, actualizar).
- ~~QR placeholder `"▦▦\n▦▦"`.~~ Reemplazado por el composable `DecorativeQrCode` (el mismo que usa `OrderReadyScreen`).

### OrderReadyScreen
- `OrderReadyScreen.kt:179` — `"📍 Caja 1"` hardcodeado. Sin campo `pickupPoint` en `Order`.
- `OrderReadyScreen.kt:183` — `"⏱ Listo 13:24"` timestamp hardcodeado.
- `OrderReadyScreen.kt:197` — `"13:08"` (hora "Pedido recibido") hardcodeado.
- `OrderReadyScreen.kt:199` — `"13:11 — 13:24"` (rango "En preparación") hardcodeado. Los timestamps de transición requerirían columnas en la tabla `orders` de Supabase (`received_at`, `prepared_at`, `ready_at`).

### ~~AgregarMetodoDePagoScreen~~ ✓ RESUELTO
- ~~Campo `nombre` arrancaba con `"María González"` como valor real.~~ Todos los campos del formulario ahora inicializan en vacío.

### AppNavigation — lógica de negocio hardcodeada (`AppNavigation.kt:485`)
- Al confirmar ubicación en `LocationDetectedScreen`, el `localId` se resuelve buscando `"Big Pons"` por nombre con fallback al primer local. Si el local cambia de nombre o no existe, el flujo se rompe. Hay que pasar el `Local` detectado por GPS directamente en vez de buscarlo por nombre.

### LoginScreen (`LoginScreen.kt:174`)
- Botón `"Olvidé mi contraseña"` tiene `onClick = {}`. Pendiente en la sección de arriba, pero se lista acá como dato "funcional" faltante.

### Onboarding — demo decorativo (`OnboardingScreen.kt`)
- Slide de demo muestra ítems ficticios ("Combo Big Classic $4.500", "Papas Grandes $1.200", "Gaseosa $700") y botón `"Confirmar pedido · $6.400"` con `onClick = {}`. Es contenido intencionalmente decorativo para el onboarding; no bloquea funcionalidad.
