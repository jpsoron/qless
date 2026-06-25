# QLess 🍔

**QLess** es una app Android de pedidos anticipados en locales gastronómicos: un
*beeper virtual*. El cliente arma su pedido desde el celular, lo confirma y la app
le avisa cuando está listo para retirar, evitando filas en caja.

Kotlin · Jetpack Compose · Material 3 · MVVM + Clean Architecture · Supabase.

---

> ## Entrega H2
> **Toda la justificación y evidencia de la Entrega H2 está en
> [`/documentation/H2`](documentation/H2).**
> Empezar por
> [`Justificaciones_Entrega_H2.md`](documentation/H2/Justificaciones_Entrega_H2.md):
> mapea cada requisito de la consigna contra su evidencia (tests, métricas,
> diagramas, uso de IA).

---

## Guía rápida

**Dos roles en la app:**

- **Cliente** — detecta el local (GPS o QR), arma el carrito desde el menú, confirma
  el pedido (pago en efectivo al retirar) y sigue el estado en tiempo real hasta el retiro.
- **BackOffice (operador del local)** — ve los pedidos entrantes, avanza su estado
  (recibido → en preparación → listo → retirado) y puede cancelar pedidos. El rol se
  determina por el perfil del usuario (`USER` / `BACK_OFFICE`).

**Dónde está cada cosa:**

- **Documentación:** [`/documentation`](documentation) — técnica, arquitectura,
  design system, diagramas. Entrega H2 en [`/documentation/H2`](documentation/H2).
- **Tests:** `app/src/test/` (unitarios JVM) y `app/src/androidTest/` (instrumentados).
  Evidencia de ejecución en [`/documentation/H2/test_results`](documentation/H2/test_results).
  Cómo correrlos: [`documentation/testing.md`](documentation/testing.md).

---

## Build

Requiere Android Studio + JDK 17. Antes de compilar, crear `local.properties` con
las claves (ver `local.properties.example`).

```bash
./gradlew assembleDebug        # build de desarrollo
```

> Firmar el APK release: ver [`documentation/APK_signing.md`](documentation/APK_signing.md).
> Las claves y el keystore no se commitean.

---

## Tests y métricas

```bash
./gradlew testDebugUnitTest          # 33 unitarios (JVM, sin dispositivo)
./gradlew connectedDebugAndroidTest  # instrumentados (necesita emulador/teléfono)
```

Métricas no funcionales (cold start + fps) por adb, con la app instalada:

```bash
./gradlew installRelease && ./scripts/measure-metrics.sh coldstart 10
./scripts/measure-metrics.sh jank
```

Resultados: **33/33 unit OK · cold start ~1018 ms · jank 5,32%**. Guía completa en
[`documentation/testing.md`](documentation/testing.md); evidencia en
[`/documentation/H2/test_results`](documentation/H2/test_results).
