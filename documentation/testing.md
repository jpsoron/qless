# QLess — Pruebas y métricas (RF5)

Documentación de la suite de pruebas: cómo se ejecuta y qué cubre cada prueba.
Los comandos se corren desde la raíz del proyecto.

Estructura:

- `app/src/test/` — pruebas unitarias JVM (ViewModels + casos de uso, con repos fake).
- `app/src/androidTest/` — pruebas instrumentadas (Compose, corren en dispositivo).
- `scripts/measure-metrics.sh` — medición de métricas no funcionales por `adb`.
- `documentation/H2/test_results/` — evidencia de ejecución (logs y reportes).

---

## 1. Ejecución

### Paso 1 — Pruebas unitarias (JVM, sin hardware)

```bash
./gradlew testDebugUnitTest
```

Ejecuta las pruebas de `app/src/test/` en la JVM local; no requieren emulador ni
teléfono. Es el primer filtro: si algo no compila o una dependencia no resuelve,
falla en este paso.

- Requisitos: JDK + Android SDK (`local.properties` con `sdk.dir`).
- Reporte HTML: `app/build/reports/tests/testDebugUnitTest/index.html`.
- Una sola clase: `./gradlew testDebugUnitTest --tests "com.qless.viewmodel.OrderViewModelTest"`.

### Paso 2 — Prueba instrumentada (requiere dispositivo)

```bash
./gradlew connectedDebugAndroidTest
```

Ejecuta `ActiveCartCardTest`, que renderiza Compose real: compila un APK de prueba,
lo instala en el dispositivo y corre los tests ahí.

- Requisitos: un emulador abierto o un teléfono conectado por USB con depuración
  activada (se confirma con `adb devices`).
- Reporte HTML: `app/build/reports/androidTests/connected/index.html`.

### Paso 3 — Cold start

```bash
./gradlew installRelease && ./scripts/measure-metrics.sh coldstart 10
```

`installRelease` compila e instala la build release (la métrica de arranque se mide
sobre la versión optimizada). `measure-metrics.sh coldstart 10` hace 10 arranques en
frío (force-stop + `am start -W` cada vez), lee el `TotalTime` y reporta promedio,
mínimo y máximo.

- Objetivo: cold start < 2.500 ms en Pixel 9 Pro.

### Paso 4 — Jank / fps de scroll

```bash
./scripts/measure-metrics.sh jank
```

Mide la fluidez del scroll. El script resetea las estadísticas
(`dumpsys gfxinfo … reset`), espera el scroll manual sobre la pantalla a medir
(~10 s) y muestra el porcentaje de "Janky frames" y los percentiles.

- Objetivo: jank < 10% (equivale a > 54 fps sostenido).
- Usa el APK del Paso 3 (no recompila).

La evidencia de ejecución se vuelca a `documentation/H2/test_results/`.

---

## 2. Cobertura

Las pruebas unitarias montan el ViewModel con un repositorio fake envuelto en los
casos de uso reales: se ejercita VM + caso de uso juntos, no el VM aislado.

### `OrderViewModelTest` — dominio principal (pedidos)

| Test | Qué verifica |
|------|--------------|
| `loadUserOrders exitoso publica los pedidos` | Camino feliz de carga; el flag `isLoadingUser` se apaga. |
| `loadUserOrders con error setea mensaje` | Un error de red deja el mensaje en `error` y no cuelga la UI en "cargando". |
| `filteredUserOrders separa por estado` | Los filtros ACTIVE / COMPLETED / CANCELLED devuelven los pedidos correctos. |
| `activeOrder devuelve el primer pedido en curso` | Fuente única de verdad del pedido activo (banner de Home, bloqueo de carrito, "Mis Pedidos"). |
| `placeOrder con carrito vacío no crea pedido` | No se envía un pedido vacío al backend. |
| `placeOrder exitoso guarda lastCreatedOrder y emite CheckoutSuccess` | Se crea el pedido, queda en `lastCreatedOrder` y se emite el evento que dispara confirmación + `clearCart()`. |
| `placeOrder fallido emite CheckoutError` | Un pago rechazado emite el evento de error con el mensaje correcto. |
| `confirmPickup actualiza el estado a picked_up` | Cierre del flujo: se llama `updateStatus(orderId, "picked_up")`. |
| `updateOrderStatus delega en el repo y recarga pedidos del local` | Camino BackOffice: cambia el estado y refresca la lista del local. |

### `CartViewModelTest` — carrito

Usa `FakeCartRepository` (un `MutableStateFlow` en memoria que imita Room).

| Test | Qué verifica |
|------|--------------|
| `observa el contenido inicial del carrito` | La suscripción al flow refleja el contenido en `uiState`. |
| `addItem incrementa la cantidad del producto` | El cálculo de `currentQuantity` es correcto; cada tap suma. |
| `cartLocalId refleja el local del carrito` | Base de la lógica "un carrito por local" y del bloqueo por conflicto. |
| `removeItem baja la cantidad y elimina al llegar a cero` | No queda un ítem en cantidad 0. |
| `clearCart vacía el carrito` | Se vacía y el repo recibe la orden de limpiar (tras el checkout). |

### `MisLocalesViewModelTest` — locales + modo offline

El VM carga en su `init`, así que el escenario se define en el fake antes de construirlo.

| Test | Qué verifica |
|------|--------------|
| `carga inicial exitosa desde la red marca isOffline false` | Dato fresco ⇒ `isOffline = false`. |
| `datos servidos desde cache marcan isOffline true` | Dato de caché ⇒ `isOffline = true` (decide el `OfflineBanner`, RF4). |
| `fallo de carga setea error` | Sin red ni caché, la pantalla avisa con un mensaje. |

### `MenuViewModelTest` — menú + categoría inicial

| Test | Qué verifica |
|------|--------------|
| `loadMenu con localId vacío no llama al caso de uso` | Guard temprano: con `""` no se toca el repo. |
| `loadMenu selecciona Popular cuando hay items populares` | La categoría inicial es "🔥 Popular" si hay populares. |
| `loadMenu sin populares usa la primera categoría y propaga cache` | Sin populares, la categoría inicial es la del primer ítem; propaga el origen a `isOffline`. |
| `loadMenu fallido setea error` | Fallo ⇒ mensaje en `error`, loading en `false`. |

### `ActiveCartCardTest` — UI (instrumentado, Compose)

Único que corre en dispositivo. Es stateless (no necesita ViewModel).

| Test | Qué verifica |
|------|--------------|
| `muestra_local_y_etiqueta_de_carrito` | Render correcto: "CARRITO ACTIVO", nombre del local y "N productos". |
| `tocar_ver_dispara_callback` | Tocar "Ver →" ejecuta el callback `onVer`. |

### Andamiaje

- `MainDispatcherRule` reemplaza `Dispatchers.Main` por un `UnconfinedTestDispatcher`.
  Sin esto, cualquier `viewModelScope.launch { }` falla en una prueba JVM. Al ser
  unconfined, las corrutinas corren de forma ansiosa: después de llamar `loadX()` el
  estado ya está actualizado y se puede afirmar sin `advanceUntilIdle()`.
- `FakeRepositories` son implementaciones en memoria de los contratos de dominio
  (`OrderRepository`, `CartRepository`, `LocalesRepository`, `MenuRepository`) más
  builders de muestra (`sampleOrder`, `sampleLocal`, `sampleMenuItem`). Se eligieron
  fakes en lugar de MockK por ser deterministas y legibles.

---

## 3. Integración continua (`.github/workflows/ci.yml`)

- Job `build` (en cada push/PR a `dev` y `main`): `lintDebug` → `testDebugUnitTest`
  → `assembleDebug`. Corre las pruebas unitarias JVM en cada cambio y sube el APK y
  los reportes como artifacts.
- Job `instrumented` (solo en PRs): levanta un emulador (API 30, x86_64) y corre
  `connectedDebugAndroidTest`. Se limita a PRs porque el emulador tarda ~8 min.

Las métricas (cold start / fps) no corren en CI: requieren un dispositivo físico y
un scroll manual. Se miden con `scripts/measure-metrics.sh` y la evidencia queda en
`documentation/H2/test_results/`.

---

## 4. Alcance

Las pruebas unitarias cubren la lógica de presentación y las reglas de negocio en
ViewModels y casos de uso. No cubren la capa de datos real (Supabase / Room), que
se verificaría con pruebas de integración. Es la cobertura adecuada para RF5.
