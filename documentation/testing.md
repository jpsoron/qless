# QLess — Pruebas y métricas (RF5)

Guía de la suite de tests: **cómo ejecutarla** y **qué cubre cada test**.

Todos los comandos se corren desde la raíz del proyecto.
Estructura:

- `app/src/test/` — unit tests JVM (ViewModels + casos de uso, con repos fake).
- `app/src/androidTest/` — tests instrumentados (Compose, corren en dispositivo).
- `scripts/measure-metrics.sh` — medición de métricas no funcionales por `adb`.
- `documentation/metrics/results.md` — plantilla de evidencia de métricas.

---

## 1. Cómo ejecutarlos

### Paso 1 — Unit tests (JVM, rápidos, sin hardware)

```bash
./gradlew testDebugUnitTest
```

Corre los tests de `app/src/test/`. Se ejecutan en la JVM de tu máquina: **no**
necesitan emulador ni teléfono. Es lo más rápido y el primer filtro real (si algo
no compila o una dependencia no resuelve, salta acá).

- **Requisitos:** JDK + Android SDK (`local.properties` con `sdk.dir`).
- **Resultado:** `BUILD SUCCESSFUL` o el detalle del test que falló.
- **Reporte HTML:** `app/build/reports/tests/testDebugUnitTest/index.html`
- **Correr una sola clase:**
  `./gradlew testDebugUnitTest --tests "com.qless.viewmodel.OrderViewModelTest"`

### Paso 2 — Test instrumentado (necesita dispositivo)

```bash
./gradlew connectedDebugAndroidTest
```

Corre `ActiveCartCardTest` (renderiza Compose de verdad). Compila un APK de test,
lo instala en el dispositivo y ejecuta los tests ahí.

- **Requisitos:** un emulador abierto o un teléfono conectado por USB con
  depuración activada. Verificá con `adb devices` que aparezca uno.
- **Resultado / reporte:** `app/build/reports/androidTests/connected/index.html`
- Más lento (compila + instala). Si no tenés device a mano, se puede saltear y
  correr el día de la defensa.

### Paso 3 — Cold start (métrica de arranque)

```bash
./gradlew installRelease && ./scripts/measure-metrics.sh coldstart 10
```

Son dos comandos encadenados (`&&`: el segundo corre solo si el primero salió bien).

- `installRelease` compila la build **release** y la instala. Se usa release (no
  debug) porque la métrica de arranque debe medirse sobre la versión optimizada.
- `measure-metrics.sh coldstart 10` hace 10 arranques en frío (force-stop +
  `am start -W` cada vez), lee el `TotalTime` y reporta **promedio / min / max**.
- **Objetivo:** cold start < 2.500 ms en Pixel 9 Pro.
- **Requisitos:** el mismo device/emulador conectado.
- **Firma:** la `release` se firma con la **clave de debug**
  (`signingConfig = signingConfigs.getByName("debug")` en `app/build.gradle.kts`)
  solo para poder instalar una build **no-debuggable** y medir. No es para
  distribución. Sin esto, AGP no genera la tarea `installRelease`.

### Paso 4 — Jank / fps de scroll (interactivo)

```bash
./scripts/measure-metrics.sh jank
```

Mide la fluidez del scroll. Es interactivo: el script resetea las estadísticas
(`dumpsys gfxinfo … reset`), te pide abrir la pantalla a medir (ej. *Mis Locales*)
y **scrollear ~10 s**, y al apretar Enter muestra el % de "Janky frames" y los
percentiles.

- **Objetivo:** jank < 10% (equivale a > 54 fps sostenido).
- **Requisitos:** la app ya instalada (del Paso 3) y el device conectado.
- No recompila: usa el APK del Paso 3.

> **Orden recomendado:** Paso 1 siempre (rápido, sin hardware) → Paso 2 si hay
> device → Pasos 3 y 4 para la evidencia de métricas, sobre la misma instalación.
> Volcar los números a `documentation/metrics/results.md`.

---

## 2. Qué cubre cada test

Los unit tests montan el ViewModel con un **repositorio fake** envuelto en los
**casos de uso reales**: así se ejercita VM + caso de uso juntos, no el VM aislado.

### `OrderViewModelTest` — dominio principal (pedidos)

| Test | Qué verifica / qué bug ataja |
|------|------------------------------|
| `loadUserOrders exitoso publica los pedidos` | Camino feliz de carga; el flag `isLoadingUser` se apaga. |
| `loadUserOrders con error setea mensaje` | Un error de red deja el mensaje en `error` y no cuelga la UI en "cargando". |
| `filteredUserOrders separa por estado` | Los filtros ACTIVE / COMPLETED / CANCELLED devuelven los pedidos correctos (blinda los tabs de "Mis Pedidos"). |
| `activeOrder devuelve el primer pedido en curso` | La fuente única de verdad del pedido activo (de ella dependen el banner de Home, el bloqueo de carrito y "Mis Pedidos"). |
| `placeOrder con carrito vacío no crea pedido` | No se manda un pedido vacío al backend. |
| `placeOrder exitoso guarda lastCreatedOrder y emite CheckoutSuccess` | Se crea el pedido, queda en `lastCreatedOrder` y se emite el evento de navegación que dispara la confirmación + `clearCart()`. |
| `placeOrder fallido emite CheckoutError` | Un pago rechazado emite el evento de error con el mensaje correcto. |
| `confirmPickup actualiza el estado a picked_up` | Cierre del flujo: se llama `updateStatus(orderId, "picked_up")`. |
| `updateOrderStatus delega en el repo y recarga pedidos del local` | Camino backoffice: cambia el estado y refresca la lista del local. |

### `CartViewModelTest` — carrito

Usa `FakeCartRepository` (un `MutableStateFlow` en memoria que imita Room).

| Test | Qué verifica / qué bug ataja |
|------|------------------------------|
| `observa el contenido inicial del carrito` | La suscripción al flow refleja el contenido en `uiState`. |
| `addItem incrementa la cantidad del producto` | El cálculo de `currentQuantity` es correcto; cada tap suma (no reinicia a 1). |
| `cartLocalId refleja el local del carrito` | Base de la lógica "un carrito por local" y del bloqueo por conflicto. |
| `removeItem baja la cantidad y elimina al llegar a cero` | No queda un ítem fantasma en cantidad 0. |
| `clearCart vacía el carrito` | Se vacía y el repo recibe la orden de limpiar (se ejecuta tras el checkout). |

### `MisLocalesViewModelTest` — locales + modo offline

El VM carga en su `init`, así que el escenario se define en el fake antes de construirlo.

| Test | Qué verifica / qué bug ataja |
|------|------------------------------|
| `carga inicial exitosa desde la red marca isOffline false` | Dato fresco ⇒ `isOffline = false`. |
| `datos servidos desde cache marcan isOffline true` | Dato de caché ⇒ `isOffline = true` (decide si se muestra el `OfflineBanner`, RF4). |
| `fallo de carga setea error` | Sin red ni caché, la pantalla avisa con un mensaje. |

### `MenuViewModelTest` — menú + categoría inicial

| Test | Qué verifica / qué bug ataja |
|------|------------------------------|
| `loadMenu con localId vacío no llama al caso de uso` | Guard temprano: con `""` no se toca el repo (evita una llamada inútil). |
| `loadMenu selecciona Popular cuando hay items populares` | La categoría inicial es "🔥 Popular" si hay populares. |
| `loadMenu sin populares usa la primera categoría y propaga cache` | Sin populares, la categoría inicial es la del primer ítem; propaga el origen a `isOffline`. |
| `loadMenu fallido setea error` | Fallo ⇒ mensaje en `error`, loading en `false`. |

### `ActiveCartCardTest` — UI (instrumentado, Compose)

Único que corre en dispositivo. Es *stateless* (no necesita ViewModel).

| Test | Qué verifica |
|------|--------------|
| `muestra_local_y_etiqueta_de_carrito` | Render correcto: aparecen "CARRITO ACTIVO", el nombre del local y "N productos". |
| `tocar_ver_dispara_callback` | Tocar "Ver →" ejecuta el callback `onVer` (la card realmente navega). |

### Andamiaje (no son tests, los habilitan)

- **`MainDispatcherRule`** — reemplaza `Dispatchers.Main` por un
  `UnconfinedTestDispatcher`. Sin esto, cualquier `viewModelScope.launch { }`
  falla en un test JVM. Al ser *unconfined*, las corrutinas corren de forma ansiosa:
  después de llamar `loadX()` el estado ya está actualizado y se puede afirmar sin
  `advanceUntilIdle()`.
- **`FakeRepositories`** — implementaciones en memoria de los contratos de dominio
  (`OrderRepository`, `CartRepository`, `LocalesRepository`, `MenuRepository`) +
  builders de muestra (`sampleOrder`, `sampleLocal`, `sampleMenuItem`). Alternativa
  a MockK: deterministas y legibles.

---

## En CI (`.github/workflows/ci.yml`)

- **Job `build`** (en cada push/PR a `dev` y `main`): `lintDebug` →
  `testDebugUnitTest` → `assembleDebug`. Es el feedback rápido; corre los unit
  tests JVM en cada cambio. Sube el APK y los reportes como artifacts.
- **Job `instrumented`** (solo en PRs): levanta un emulador (API 30, x86_64, con
  KVM) y corre `connectedDebugAndroidTest` (`ActiveCartCardTest`). Está limitado a
  PRs porque el emulador tarda ~8 min; para correrlo en cada push, quitar el `if`
  del job. Sube el reporte de androidTest como artifact.

Las **métricas** (cold start / fps) **no** corren en CI: necesitan un dispositivo
físico y un scroll manual. Se miden a mano con `scripts/measure-metrics.sh` y se
documentan en `documentation/metrics/results.md`.

## Alcance (qué NO cubren)

Estos unit tests cubren **lógica de presentación y reglas de negocio** en
ViewModels y casos de uso. **No** cubren la capa de datos real (Supabase / Room):
eso se testearía con tests instrumentados o de integración. Es lo adecuado para
RF5, pero conviene tenerlo presente para la defensa.
