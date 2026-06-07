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
│   ├── PaymentMethod.kt / PaymentMethodRepository.kt
│   ├── SessionStorage.kt            # Persistencia de sesión (DataStore)
│   ├── ThemeRepository.kt           # Dark mode + onboarding (DataStore)
│   ├── UserRepository.kt            # Auth + perfil + sesión persistente
│   └── local/                       # Room: QLessDatabase, DAOs y Entities
│   └── remote/                      # Supabase: Auth, Locales, Profile DataSources + DTOs
└── ui/
    ├── viewmodel/                   # AuthViewModel, HomeViewModel, MisLocalesViewModel, ...
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
2. **Detección de ubicación**
   - Confirmá la ubicación sugerida para ir directo al menú, o buscá otro local manualmente.
3. **Menú → Carrito → Pago**
   - Agregá ítems al carrito, revisá el resumen y confirmá el pago.
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
- **Favoritos:** la sección "Tus favoritos" en Home muestra los locales guardados en el perfil del usuario.
- **Sesión persistente:** si iniciás sesión con "Mantener sesión abierta", la app te reconoce automáticamente en próximas aperturas (hasta que cerrés sesión explícitamente).
- El carrito y los métodos de pago persisten entre sesiones (Room local).
- Los datos de menú y pedidos son estáticos/simulados (sin backend todavía).
- La navegación con el botón físico de atrás de Android está soportada en todas las pantallas.
