# QLess — Tu comida sin filas

Desarrollo De Aplicaciones 1 — Demo APK

---

## Estructura del proyecto

```
app/src/main/java/com/qless/
├── MainActivity.kt
├── navigation/
│   └── AppNavigation.kt             # Rutas y navegación (Jetpack Navigation Compose)
├── data/
│   ├── CartItem.kt / CartRepository.kt
│   ├── Local.kt / LocalesRepository.kt
│   ├── MenuItem.kt / MenuRepository.kt  # Menú real desde Supabase
│   ├── PaymentMethod.kt / PaymentMethodRepository.kt
│   ├── SessionStorage.kt            # Persistencia de sesión (DataStore)
│   ├── ThemeRepository.kt           # Dark mode + onboarding (DataStore)
│   ├── UserRepository.kt            # Auth + perfil + sesión persistente
│   └── local/                       # Room: QLessDatabase (v4), DAOs y Entities
│   └── remote/                      # Supabase: Auth, Locales, Menu, Profile DataSources + DTOs
└── ui/
    ├── viewmodel/                   # AuthViewModel, HomeViewModel, MenuViewModel, ...
    ├── components/                  # QLessBottomNav
    ├── screens/                     # Todas las pantallas de la app
    └── theme/                       # Colores, tipografía y tema
```

---

## Credenciales de prueba

La autenticación usa **Supabase Auth** — las cuentas viven en la nube y son compartidas.

### Cliente

Registrate desde la pantalla de Login con cualquier email y contraseña (mínimo 8 caracteres).

Cuenta de prueba lista para usar:

| Campo | Valor |
|-------|-------|
| Email | juampi@qless.com |
| Contraseña | juampi1234 |

### BackOffice

| Campo | Valor |
|-------|-------|
| Email | backoffice@qless.com |
| Contraseña | backoffice |

---

## Cómo simular la app

### Flujo principal (cliente)

1. **Splash → Login** (o directo a Home si hay sesión guardada)
   - Si es la primera vez: pasás por el Onboarding antes del Login.
   - Registrate o iniciá sesión con las credenciales de la tabla de arriba.
   - Activá el checkbox **"Mantener sesión abierta"** para no tener que volver a loguearte en próximas aperturas.
2. **Selección de local**
   - Desde **Home**: tocá un local favorito para ir directo a su menú.
   - Desde **Mis Locales**: tocá cualquier local de la lista. Si el banner "¿Estás en Big Pons?" aparece, podés tocar **"Sí"** para ir directo a ese menú, o **"No"** para cerrarlo.
3. **Menú → Carrito → Pago**
   - El menú muestra los productos reales del local desde Supabase.
   - Si intentás entrar al menú de un local distinto teniendo ítems en el carrito, la app te avisa y te pregunta si querés limpiar el pedido actual o volver.
   - Agregá ítems, revisá el resumen y confirmá el pago.
4. **Seguimiento del pedido**
   - En la pantalla de Tracking aparece el botón **"Simular: Pedido Listo"** — usalo para avanzar al estado de retiro.
5. **Retiro**
   - En la pantalla de pedido listo, confirmá el retiro para ver la pantalla de éxito.

### Flujo con escaneo de QR

- Desde Home o Mis Locales tocá **Escanear QR**.
- Tocá el recuadro o el botón para simular una lectura exitosa y navegar al menú.
- **Para simular un QR fallido:** no tocás nada y esperás 5 segundos — la app navega automáticamente a la pantalla de error.

### Flujo BackOffice (gestión del local)

- En la pantalla de Login ingresá:
  - **Email:** `backoffice@qless.com`
  - **Contraseña:** `backoffice`
- Accedés al panel de BackOffice con:
  - **Pedidos activos** y actualización de estado
  - **Historial** de pedidos

---

## Notas

- **Autenticación:** conectada a Supabase Auth. Requiere conexión a internet para login y registro.
- **Perfil:** nombre, rol y favoritos se leen desde la tabla `perfiles` en Supabase Postgres.
- **Locales:** cargados en tiempo real desde la tabla `locales` en Supabase Postgres.
- **Favoritos:** la sección "Tus favoritos" en Home muestra los locales guardados en el perfil del usuario. Tocar uno navega directamente a su menú.
- **Menú:** cargado en tiempo real desde la tabla `menu_items` en Supabase Postgres. Cada local tiene su propia carta con categorías, precios y productos.
- **Carrito por local:** solo se pueden agregar productos de un mismo local. Al intentar entrar al menú de otro local con ítems en el carrito, aparece un diálogo de confirmación.
- **Sesión persistente:** si iniciás sesión con "Mantener sesión abierta", la app te reconoce automáticamente en próximas aperturas (hasta que cerrés sesión explícitamente).
- El carrito y los métodos de pago persisten entre sesiones (Room local).
- Los pedidos activos son simulados (sin backend todavía).
- La navegación con el botón físico de atrás de Android está soportada en todas las pantallas.
