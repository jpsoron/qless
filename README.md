# QLess — Tu comida sin filas

Desarrollo De Aplicaciones 1 — Demo APK

---

## Estructura del proyecto

```
app/src/main/java/com/qless/
├── MainActivity.kt
├── navigation/
│   └── AppNavigation.kt       # Rutas y navegación (Jetpack Navigation Compose)
└── ui/
    ├── components/             # Componentes reutilizables (BottomNav, etc.)
    ├── samples/                # Pantallas de muestra/UI samples
    ├── screens/                # Todas las pantallas de la app
    └── theme/                  # Colores, tipografía y tema
```

La app es un prototipo navegable sin backend. Toda la lógica está mockeada en las pantallas.

---

## Cómo simular la app

### Flujo principal (cliente)

1. **Splash → Onboarding → Login**
   - Usá cualquier email/contraseña para ingresar como cliente.
2. **Detección de ubicación**
   - Confirmá la ubicación sugerida para ir directo al menú, o buscá otro local manualmente.
3. **Menú → Carrito → Pago**
   - Agregá ítems al carrito, revisá el resumen y confirmá el pago.
4. **Seguimiento del pedido**
   - En la pantalla de Tracking aparece el botón **"Simular: Pedido Listo"** — usalo para avanzar al estado de retiro.
5. **Retiro (QR)**
   - En la pantalla de pedido listo, confirmá el retiro para ver la pantalla de éxito.

### Flujo con escaneo de QR

- Desde Home o Mis Locales tocá **Escanear QR**.
- El botón central escanea el QR y navega al menú.
- **Para simular un QR fallido:** no tocás nada y esperás 5 segundos — la app navega automáticamente a la pantalla de error de QR no reconocido.

### Flujo BackOffice (gestión del local)

- En la pantalla de Login ingresá:
  - **Usuario:** `backoffice`
  - **Contraseña:** `backoffice`
- Accedés al panel de BackOffice con:
  - **Pedidos activos** y actualización de estado
  - **Historial** de pedidos

---

## Notas

- No hay autenticación real ni backend; todos los datos son estáticos/mockeados.
- La navegación con el botón físico de atrás de Android está soportada en todas las pantallas.
