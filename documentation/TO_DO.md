# QLess — Pendientes de UI

---

### Mis Locales

- [ ] **Buscador funcional** — el `TextField` "Buscar local..." existe visualmente pero no filtra. Agregar `var query by remember { mutableStateOf("") }` y `.filter { it.nombre.contains(query, ignoreCase = true) }` sobre `uiState.locales`.
- [ ] **Chips de categoría** — filtrar la lista por `local.categoria` con chips derivados de los datos reales. Estado: `var selectedCategory by remember { mutableStateOf<String?>(null) }`.
- [ ] **Ordenar locales** — dropdown con opciones (rating ↓, nombre A-Z, primero los abiertos). Solo `.sortedBy { }` sobre la lista.
- [ ] **Cerrar banner de geolocalización** — el botón "No" tiene `onClick = {}`. Ocultar el banner con `var showGeoBanner by remember { mutableStateOf(true) }`.

### Menú

- [ ] **Filtro por categoría que realmente filtra** — la barra de categorías ya tiene estado (`selectedCategory` en `MenuViewModel`) pero los `items(menuItems.filter { ... })` ignoran ese estado y muestran siempre las mismas secciones. Conectar `selectedCategory` al filtrado de la lista.

### Mis Pedidos

- [ ] **Tabs funcionales (Activos / Finalizados / Cancelados)** — los chips están renderizados con `selected = true/false` hardcodeado. Necesitan `var selectedTab by remember { mutableStateOf("Activos") }` y que cada tab muestre su lista correspondiente.
- [ ] **Estado vacío por tab** — cuando un tab no tiene pedidos, mostrar un mensaje del tipo "No tenés pedidos finalizados".

### Login

- [ ] **Dialog "Olvidé mi contraseña"** — el `TextButton` tiene `onClick = {}`. Mostrar un `AlertDialog` de Material 3 con campo de email y botón "Enviar" (solo UI, sin lógica real de envío).

### Ajustes

- [ ] **"Mi perfil" navega a algún lado** — el ítem tiene `onClick = {}` sin destino. Puede navegar a una pantalla nueva o abrir un `BottomSheet` con los datos del usuario (nombre, email) y opción de editar nombre.

### Carrito

- [ ] **Vaciar carrito** — no hay botón de "Vaciar todo". Agregar un `AlertDialog` de confirmación + `cartViewModel.clearCart()`.

### Order Ready

- [ ] **Placeholder de QR visual** — hay un comentario `// QR Code Placeholder`. Reemplazar con un recuadro decorativo que simule un QR (cuadrados anidados con colores del design system).

---

## Pantallas incompletas

Todas las pantallas existen y están ruteadas en `AppNavigation`. Estas necesitan trabajo adicional:

| Pantalla | Estado actual | Qué falta |
|---|---|---|
| `MisPedidosScreen` | UI hecha, datos hardcodeados | Tabs no funcionan, no hay estado vacío, las órdenes son datos fijos |
| `AjustesScreen` | UI hecha | "Mi perfil" no navega a ningún lado (`onClick = {}`) |
| `NotificacionesScreen` | UI hecha con toggles funcionales | Las notificaciones son datos hardcodeados (sin backend) |
| `MenuScreen` | UI hecha, categorías con estado | El filtro por categoría no se conecta con la lista de ítems |
| `MisLocalesScreen` | Datos reales desde Supabase | Buscador y chips de filtro son solo decorativos |
| `GoogleLoginScreen` | UI hecha | Los botones no hacen nada real (Google Auth no implementado) |
| `OrderSummaryScreen` | UI hecha | Datos hardcodeados (número de pedido, ítems, totales) |
