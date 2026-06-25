# QLess — Checklist de Heurísticas de Nielsen + Accesibilidad

> Evidencia pedida por la consigna (UI/UX y CX → "Aplicar Material Design 3 y
> heurísticas de Nielsen (evidenciar en un checklist)" + RF7 de accesibilidad).
> Relevado contra el código. Estados: ✅ cumplido · 🟡 parcial · ⛔ pendiente.

---

## Parte 1 — Las 10 heurísticas de Nielsen

### 1. Visibilidad del estado del sistema ✅
El usuario siempre sabe qué está pasando.
- **Seguimiento en tiempo real** del pedido (beeper virtual): anillo de progreso
  animado + timeline de estados (`TrackingScreen`), actualizado por Supabase Realtime.
- **Badges de estado** de pedido con color semántico (`QLessStatusColors`: ámbar
  "En preparación", verde "Listo", rojo "Cancelado").
- **Banner offline** ("Sin conexión — mostrando los últimos datos guardados",
  `OfflineBanner`) cuando se sirve caché.
- **Indicadores de carga**: `CircularProgressIndicator` / skeletons en Home, MisLocales, Menú.
- **Badge de notificaciones no leídas** sobre la campana en Home.
- **Card de carrito activo** (`ActiveCartCard`) visible mientras hay ítems.

### 2. Coincidencia entre el sistema y el mundo real ✅
Lenguaje del dominio gastronómico, no jerga técnica.
- Estados en términos humanos: "Pago confirmado", "En preparación", "Listo para
  retirar", "Retiro en mostrador".
- Metáfora reconocible del **beeper virtual** de los locales de comida.
- Emojis de locales e ítems del menú; precios en formato local ($).

### 3. Control y libertad del usuario 🟡
Salidas claras y reversibilidad donde tiene sentido.
- Botón **Volver** consistente en pantallas internas.
- Diálogo de **conflicto de carrito** ("Nuevo pedido" / "Mantener pedido") al
  cambiar de local.
- **Vaciar carrito** con confirmación.
- **Cancelar pedido** (BackOffice) con confirmación; del lado cliente, la pantalla
  de seguimiento muestra estado "Pedido cancelado" + **"Volver al inicio"**.
- 🟡 Gap honesto: cancelar un pedido es **irreversible** (se avisa en el diálogo).
  No hay "deshacer" general; aceptable para el dominio.

### 4. Consistencia y estándares ✅
- **Design system unificado** (`documentation/DESIGN_SYSTEM.md`): tokens de color,
  tipografía (Lora + Plus Jakarta Sans), radios, componentes.
- **Material 3** en toda la app; navegación inferior consistente (cliente y BackOffice).
- Patrones repetidos: cards, chips de estado, botones primarios/destructivos iguales
  en todas las pantallas.

### 5. Prevención de errores 🟡
- **Confirmaciones** (`AlertDialog`) antes de acciones destructivas: vaciar carrito,
  cancelar pedido, cerrar sesión, eliminar cuenta.
- **Gate de carrito por local**: evita mezclar ítems de locales distintos sin avisar.
- **Bloqueo de carrito nuevo** mientras hay un pedido en curso (banner + CTA "Ver pedido").
- **Validaciones de negocio**: `PlaceOrderUseCase` rechaza carrito vacío; email único
  al editar perfil (`EmailAlreadyInUseException`); QR validado contra local existente.
- 🟡 Gap: validación de formularios de pago no aplica (pago digital deshabilitado en MVP).

### 6. Reconocer antes que recordar ✅
- **Favoritos** y local más cercano (GPS) ofrecidos en Home: el usuario elige de una
  lista, no recuerda nombres.
- **Carrito y métodos de pago persistentes** (Room): no hay que recargar nada.
- **Resumen del pedido** siempre visible antes de confirmar; código de retiro a la vista.
- **Escaneo QR** evita tipear el local.

### 7. Flexibilidad y eficiencia de uso 🟡
- **Múltiples caminos al mismo objetivo**: detectar local por GPS, por QR o por lista.
- **"Mantener sesión"** evita re-login.
- **Deep-link**: tocar la notificación abre directo el seguimiento del pedido.
- 🟡 Gap: sin atajos avanzados / personalización para usuarios expertos (no crítico
  para el alcance).

### 8. Diseño estético y minimalista ✅
- Jerarquía tipográfica clara; una acción primaria por pantalla.
- Paleta acotada y con intención (estados con color semántico, no decorativo).
- Sin sobrecarga de información; texturas sutiles (`KraftSurface`) sin ruido.

### 9. Ayudar a reconocer, diagnosticar y recuperarse de errores 🟡
- **Mensajes de error en español** mapeados desde Supabase (`AuthViewModel`): credenciales
  inválidas, email en uso, cuenta inactiva, etc.
- **`QrNoReconocidoScreen`** cuando el QR no corresponde a un local registrado.
- **Banner offline** explica por qué los datos pueden no estar frescos.
- **Errores de checkout** se muestran al usuario (no fallan en silencio).
- 🟡 Gap: algunos errores de red caen a un mensaje genérico; convendría más especificidad.

### 10. Ayuda y documentación 🟡
- **Onboarding inicial** presenta la propuesta de valor y cómo funciona el flujo.
- Textos de apoyo contextuales ("Mostrá este código al retirar", subtítulos de pasos).
- 🟡 Gap: no hay centro de ayuda / FAQ in-app. Para una app de este alcance, el
  onboarding + microcopys cubren lo esencial; un FAQ queda como mejora futura.

**Resumen Nielsen:** 4 ✅ plenas, 6 🟡 parciales con gaps menores y justificados.
Ninguna heurística sin abordar.

---

## Parte 2 — Accesibilidad (RF7)

### Lo que YA está ✅

| Ítem | Estado | Evidencia |
|---|---|---|
| **Tema oscuro** | ✅ | `LightColorScheme` / `DarkColorScheme` completos en `Color.kt`; respeta el modo del sistema y toggle manual. |
| **Tamaños de fuente escalables** | ✅ | **139** tamaños declarados en `sp` y **0 en `dp`** → el texto respeta el ajuste de fuente del sistema. Tipografía vía `MaterialTheme.typography`. |
| **Material 3** | ✅ | Componentes M3 en toda la app (roles de color, estados, ripple). |
| **`contentDescription` en íconos** | ✅ | Íconos accionables descritos por su acción (ej. "Volver", "Notificaciones", "Editar perfil", "Pedido cancelado"); íconos decorativos en `null` (criterio correcto de Android). No quedan `IconButton` interactivos sin descripción. |
| **Labels asociados en campos de texto** | ✅ | Login, Registro y método de pago: cada campo expone su etiqueta a lectores de pantalla vía `Modifier.semantics { contentDescription = ... }`, además del label visible. |
| **Semántica de controles** | ✅ | `Switch`, `Checkbox`, `RadioButton` de M3 traen rol/estado para lectores de pantalla por defecto (preferencias de notificación, "Mantener sesión", método de pago). |

> Este checklist respalda el punto "evidenciar heurísticas de Nielsen +
> accesibilidad" de la consigna. El dark mode y el escalado de fuente se pueden
> evidenciar en vivo sobre la app.
