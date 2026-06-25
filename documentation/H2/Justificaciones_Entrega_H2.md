# QLess — Justificaciones de Entrega H2

> Mapeo punto por punto de la consigna (`CONSIGNA.md`) contra el estado real del
> proyecto, con evidencia. Estados: ✅ cumplido · 🟡 parcial · ⛔ pendiente.
>
> **Cómo leer este documento:** cada requisito de la consigna tiene su estado y un
> puntero a la evidencia concreta. El "Índice de anexos" de abajo dice exactamente
> dónde está cada cosa.

---

## 0. Índice de anexos (dónde encontrar cada evidencia)

Todo lo de esta entrega vive en `documentation/`. Los anexos de H2 están en
`documentation/H2/`:

```
documentation/
├── CONSIGNA.md                      ← consigna original
├── ARCHITECTURE.md                  ← arquitectura (diagramas Mermaid, fuente)
├── technical-documentation.md       ← documentación técnica completa
├── DESIGN_SYSTEM.md                 ← design system (colores, tipografía, componentes)
├── testing.md                       ← estrategia y detalle de pruebas
└── H2/                              ← ANEXOS DE ESTA ENTREGA
    ├── Justificaciones_Entrega_H2.md          ← ESTE documento
    ├── Uso responsable de IA en QLess.docx    ← declaración de uso de IA (prompts + revisión humana)
    ├── nielsen_checklist.md                   ← heurísticas de Nielsen + accesibilidad
    ├── diagrama_arquitectura.png              ← diagrama de arquitectura (PNG)
    ├── diagrama_responsabilidad.png           ← diagrama de responsabilidad de capas (PNG)
    ├── diagramas_secuenciales/                ← diagramas de secuencia
    │   ├── diagramas_secuenciales.md          ← explicación de los 4 diagramas
    │   ├── Client_flow_1.png                  ← cliente: confirmar pedido (Create)
    │   ├── Client_flow_2.png                  ← cliente: seguimiento en vivo + notificación (Read)
    │   ├── Backoffice_flow_1.png              ← backoffice: avanzar estado (Update)
    │   └── Backoffice_flow_2.png              ← backoffice: cancelar pedido (Delete)
    └── test_results/                          ← EVIDENCIA DE PRUEBAS Y MÉTRICAS
        ├── unit-tests.log                     ← salida de testDebugUnitTest
        ├── instrumented.log                   ← salida de connectedDebugAndroidTest
        ├── unit-html-report/index.html        ← reporte HTML de unit tests (33 tests)
        ├── coldstart.log                      ← métrica de cold start (RNF1)
        └── jank.log                           ← métrica de jank / fps (RNF1)
```

| Buscás… | Andá a… |
|---|---|
| Diagrama de arquitectura | `H2/diagrama_arquitectura.png` (fuente Mermaid en `ARCHITECTURE.md`) |
| Diagrama de responsabilidad de capas | `H2/diagrama_responsabilidad.png` (fuente en `ARCHITECTURE.md`) |
| Diagramas de secuencia (4) | `H2/diagramas_secuenciales/` (PNGs + `.md` explicativo) |
| Evidencia de pruebas (logs + reporte) | `H2/test_results/` |
| Evidencia de métricas (cold start / fps) | `H2/test_results/coldstart.log` y `jank.log` |
| Código fuente de las pruebas | `app/src/test/` (unit) y `app/src/androidTest/` (instrumentados) |
| Heurísticas de Nielsen + accesibilidad | `H2/nielsen_checklist.md` |
| Declaración de uso de IA (prompts + revisión) | `H2/Uso responsable de IA en QLess.docx` |

---

## 1. Hitos

| Hito | Estado | Evidencia |
|---|---|---|
| **H1** (Figma, flujo de pantallas, repo, tablero, ≥2 casos de uso, APK demo, diagrama de arquitectura) | ✅ | **Entregado en H1** (prototipo Figma, flujo de pantallas, repositorio inicializado, tablero de seguimiento, casos de uso, APK demo, diagrama inicial de arquitectura). |
| **H2** (feature set completo, pruebas, métricas, APK RC, documentación) | ✅ | Feature set, documentación, **pruebas y métricas ejecutadas OK** (§5, §6) y **APK RC firmado generado** (`versionName 1.0-rc1`, §9). |

---

## 2. Gestión del proyecto

| Requisito | Estado | Evidencia |
|---|---|---|
| Equipo de 3 con roles | ✅ | Equipo con roles definidos (PO / Tech Lead / UX-UI / QA). **Entregado en H1**, vigente. |
| Seguimiento de backlog e historias de usuario | ✅ | Tablero con backlog e historias accesible a la cátedra. **Entregado y en uso desde H1**. |
| Trabajo en grupo evidenciado | ✅ | `git shortlog` muestra **múltiples contribuidores reales** (Juampi, Ramiro, lema-23, tomipro) con commits distribuidos. |

---

## 3. Objetivos de aprendizaje

| Objetivo | Estado | Evidencia |
|---|---|---|
| 1. Diseño centrado en el usuario (research, prototipado, validación) | ✅ | Prototipo Figma de alta fidelidad; design system documentado (`DESIGN_SYSTEM.md`). |
| 2. Arquitecturas modernas (MVVM, capas, repositorios) | ✅ | MVVM + Clean Architecture en 3 capas (`ARCHITECTURE.md`). |
| 3. Metodología de desarrollo consistente | ✅ | Git con ramas + tablero + CI. |
| 4. Persistencia local + backend vía API REST | ✅ | Room + DataStore (local) + Supabase PostgREST (REST). Ver §6 RF / §arquitectónicos. |
| 5. Pruebas unitarias y de calidad con métricas | ✅ | **Pruebas ejecutadas OK** (33 unit + 2 instrumentados, §6) y **métricas dentro de umbral** (cold start 1018 ms, jank 5.32%, §5). Evidencia en `test_results/`. |
| 6. Documentación técnica y de usuario | ✅ | Carpeta `documentation/` completa. |

---

## 4. Requisitos Funcionales

### RF1 — Onboarding inicial ✅
`OnboardingScreen` se muestra en la primera instalación (gate por DataStore
`onboardingCompleted`). Presenta la propuesta de valor y el descuento de bienvenida.

### RF2 — ≥3 flujos de pantallas + CRUD completo del dominio principal ✅
- **Flujos:** (1) Autenticación (Splash → Onboarding → Login/Registro), (2) Pedido
  (detección de local → menú → carrito → pago → seguimiento → retiro), (3) Gestión
  BackOffice (lista → actualizar/cancelar → historial).
- **CRUD completo del dominio principal (`pedido`, tabla `orders`):**
  - **Create:** confirmar pedido (`PlaceOrderUseCase` → `OrderRemoteDataSource.createOrder`).
  - **Read:** seguimiento en vivo (`getOrdersByUser` + Supabase Realtime); lista BackOffice.
  - **Update:** avanzar estado (`UpdateOrderStatusUseCase`: pending → preparing → ready → picked_up).
  - **Delete (baja lógica):** **cancelar pedido** desde BackOffice (`BackOfficeUpdateOrderScreen`,
    botón con confirmación → `status = cancelled`, sale de la lista de activos).
  - Diagramas de secuencia del CRUD anexados en `H2/diagramas_secuenciales/`
    (4 PNGs + `.md` explicativo).

### RF3 — Autenticación email/contraseña y/o federada ✅
Supabase Auth con **email + contraseña** real (registro, login, persistencia de
sesión con refresh token, baja lógica de cuenta). Login federado con Google **simulado**
(`GoogleLoginScreen`). El requisito se cumple con email/password; Google real queda
como mejora futura (no bloqueante).

### RF4 — Modo offline ✅
Estrategia **network-first con fallback a caché Room** para locales y menú
(`LocalesRepositoryImpl`, `MenuRepositoryImpl`). El origen del dato viaja a la UI en
`CachedResult<T>`; cuando es caché se muestra `OfflineBanner`. Cubre **lectura**; la
cola de tareas offline para escritura queda fuera de alcance (justificado: sin
operaciones de escritura pendientes que encolar en el MVP).

### RF5 — Listado con card views ✅
Múltiples listados con cards: locales (`MisLocalesScreen`), menú (`MenuScreen`),
pedidos (`MisPedidosScreen`), notificaciones, métodos de pago.

### RF6 — Sensor / captura ✅
**Cámara** con **CameraX + ML Kit** para escaneo de QR de local (`ScanearQrScreen`);
el QR contiene el UUID del local, validado contra Supabase (`GetLocalByIdUseCase`).
Además se usa **GPS** (FusedLocationProvider) para detección de cercanía.

### RF7 — Accesibilidad (fuentes escalables, contentDescription, dark mode) ✅
- **Dark mode:** ✅ esquemas claro/oscuro completos (`Color.kt`).
- **Fuentes escalables:** ✅ todo el texto en `sp` (139 ocurrencias, 0 en `dp`).
- **contentDescription:** ✅ íconos significativos descritos; campos de formulario
  (Login, Registro, método de pago) con `contentDescription` asociado al label visible
  (semantics); íconos decorativos en `null` (criterio correcto).
- Detalle completo en `H2/nielsen_checklist.md`.

---

## 5. Requisitos No Funcionales

### RNF1 — Cold start < 2.5 s y scroll > 54 fps ✅
Medido con `scripts/measure-metrics.sh` (cold start con `am start -W`, jank/fps con
`dumpsys gfxinfo`). Evidencia en `H2/test_results/coldstart.log` y `jank.log`.
- **Cold start:** promedio **1018 ms** (min 941 / max 1204, 10 iteraciones con
  force-stop) → **muy por debajo** del objetivo de 2500 ms. ✅
- **Jank / fps:** **5.32%** de frames con jank (64 de 1204) → **bajo el umbral** de
  10% (⇔ >54 fps sostenido). ✅

### RNF2 — Buen manejo de errores de conectividad ✅
Modo offline (RF4) + mensajes de error mapeados a español + banners. Cola de tareas
offline (opcional) no implementada — justificado en RF4.

### RNF3 — API Level mínimo acorde al público ✅
`minSdk = 24` (Android 7.0), `targetSdk = 36`. Cubre >95% del parque Android
manteniendo APIs modernas.

---

## 6. Requisitos Arquitectónicos

| Requisito | Estado | Evidencia |
|---|---|---|
| 1. Lenguaje justificado | ✅ | **Kotlin** (nativo Android): mejor acceso a sensores/CameraX/Compose, interoperabilidad y rendimiento vs. RN para esta app. |
| 2. MVVM + Repository | ✅ | ViewModels con `StateFlow<UiState>` + repositorios con contrato en dominio (`ARCHITECTURE.md`). |
| 3. Vistas en Jetpack Compose | ✅ | 100% Compose + Material 3. |
| 4. Librerías para consumo de API (retrofit2/Gson **o similares**) | ✅ | **Ktor (OkHttp engine) + supabase-kt (PostgREST) + Kotlin Serialization** — equivalente válido a "retrofit2/Gson o similares". PostgREST es una API REST; el SDK es el cliente. |
| 5. Material 3, dark mode, **dynamic color** | ✅ / decisión intencional | Material 3 ✅ y dark mode ✅. **Dynamic color NO se incluye por decisión de diseño intencional**: la identidad de marca gastronómica requiere una **paleta fija** (ver `DESIGN_SYSTEM.md`). El código lo soporta (`QLessTheme(dynamicColor = false)`), pero se mantiene desactivado a propósito para no romper el branding. |

**Pruebas (objetivo 5 / RF5).**
- **Código fuente** de las pruebas:
  - Unit tests JVM en `app/src/test/`: `OrderViewModelTest`, `CartViewModelTest`,
    `HomeViewModelTest`, `MisLocalesViewModelTest`, `MenuViewModelTest`,
    `QrScanViewModelTest`, `LocationUseCasesTest` (repos fake + `MainDispatcherRule`).
  - Test instrumentado de composable en `app/src/androidTest/`: `ActiveCartCardTest`.
  - Detalle de la estrategia en `testing.md`.

**✅ Ejecutadas OK** (evidencia en `H2/test_results/`):
- **Unit tests JVM** (`./gradlew testDebugUnitTest`): **33 tests, 0 fallos, 0 ignorados,
  100% éxito** (0.565 s). Log: `test_results/unit-tests.log`; reporte HTML completo:
  `test_results/unit-html-report/index.html`.
- **Instrumentados** (`./gradlew connectedDebugAndroidTest`): **2 tests OK** en
  **emulador Pixel 9 (AVD)**, `BUILD SUCCESSFUL`. Log: `test_results/instrumented.log`.

---

## 7. Requisitos UI/UX y CX

| Requisito | Estado | Evidencia |
|---|---|---|
| Mapa de navegación | ✅ | `AppNavigation` (30 rutas); diagrama de arquitectura/estructura en `ARCHITECTURE.md`. |
| Design system básico (tipos, colores, componentes) | ✅ | `DESIGN_SYSTEM.md` (tokens, Lora + Plus Jakarta, componentes). |
| Prototipo navegable | ✅ | Prototipo Figma de alta fidelidad **entregado en H1**; app funcional en H2. |
| Material Design 3 | ✅ | Toda la UI. |
| Heurísticas de Nielsen (checklist) | ✅ | `H2/nielsen_checklist.md` con las 10 heurísticas + evidencia. |
| Buenas prácticas de accesibilidad | ✅ | Ver RF7 + `H2/nielsen_checklist.md`. |
| Wireframes alta fidelidad (Figma) | ✅ | **Entregados en H1.** |

---

## 8. Ciclo de desarrollo y colaboración

| Requisito | Estado | Evidencia |
|---|---|---|
| Repositorio GitHub/GitLab | ✅ | Repo Git con acceso de la cátedra. |
| Estrategia de ramas (trunk-based / GitFlow) | ✅ | Trabajo por ramas de feature (`ajustes`, `RamaPfaff-fixes-varios`, etc.) + merges a `main`. |
| Diagrama de alto nivel de arquitectura | ✅ | PNG anexado en `H2/diagrama_arquitectura.png` (+ diagrama de responsabilidad de capas en `H2/diagrama_responsabilidad.png`). Fuente Mermaid en `ARCHITECTURE.md`. |
| Diagramas de secuencia (≥2 del flujo principal) | ✅ | **4 diagramas** anexados como PNG en `H2/diagramas_secuenciales/` (`Client_flow_1/2.png`, `Backoffice_flow_1/2.png`) con su `.md` explicativo. Cubren el CRUD completo del pedido (Create / Read / Update / Delete). |

---

## 9. Artefacto final (RC) y reproducibilidad

| Requisito | Estado | Evidencia |
|---|---|---|
| APK RC firmado | ✅ | **APK RC generado** con `assembleRelease` (`app-release.apk`, `versionName 1.0-rc1`), firmado con clave de release propia (`CN=Qless`, verificado con `apksigner`, no la clave de debug). |
| Documentación completa | ✅ | Carpeta `documentation/` (técnica, arquitectura, design system, diagramas, pruebas) + anexos en `H2/`. |
| Builds reproducibles | ✅ | Gradle con version catalogs (`libs.versions.toml`); secretos y firma fuera del repo (`local.properties` / `keystore.properties`). |

---

## 10. Uso responsable de IA

| Requisito | Estado | Evidencia                                                                                                                                                                                                                                                          |
|---|---|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Declarar prompts relevantes, fragmentos generados y revisión humana | ✅ | Anexo **`H2/Uso responsable de IA en QLess.docx`**: 8 prompts reconstruidos (herramienta: Claude Code) con resultado de la IA, fragmentos generados y revisión humana de cada uno, sección de decisiones donde se mantuvo criterio propio y conclusión.            |
| Prohibido subir claves o datos de terceros | ✅🟡 | Claves actuales fuera del repo (`BuildConfig` / `local.properties`, no commiteadas); manejo de secretos documentado en el anexo de IA.                                                                                                                             |

---