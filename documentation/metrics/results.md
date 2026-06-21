# QLess — Métricas no funcionales (RF5)

Requisitos de la consigna: **cold start < 2.5 s** y **scroll > 54 fps** (jank < 10%)
en Pixel 9 Pro. Medición con `scripts/measure-metrics.sh` (vía `adb`, sobre la
build **release** instalada en el dispositivo).

## Cómo reproducir

```bash
# 1. Compilar e instalar la release en el dispositivo
./gradlew installRelease        # o assembleRelease + adb install -r

# 2. Cold start (10 arranques en frío, force-stop entre cada uno)
./scripts/measure-metrics.sh coldstart 10

# 3. Jank / fps de scroll (reset → scrollear ~10 s → leer stats)
./scripts/measure-metrics.sh jank
```

## Resultados

> Completar tras correr en el Pixel 9 Pro. Adjuntar capturas/salida de consola
> como evidencia para la defensa.

### Cold start

| Dispositivo   | Build   | Fecha | Iteraciones | Promedio | Min | Max | ¿< 2.5 s? |
|---------------|---------|-------|-------------|----------|-----|-----|-----------|
| Pixel 9 Pro   | release |       | 10          |          |     |     |           |

### Scroll (jank / fps)

Pantalla medida: **Mis Locales** (lista con scroll).

| Dispositivo   | Build   | Fecha | Total frames | Janky % | p90 (ms) | p95 (ms) | ¿jank < 10%? |
|---------------|---------|-------|--------------|---------|----------|----------|--------------|
| Pixel 9 Pro   | release |       |              |         |          |          |              |

## Notas

- `TotalTime` de `am start -W` mide hasta el primer frame de la Activity; es la
  métrica estándar de cold start.
- `dumpsys gfxinfo <pkg>` reporta "Janky frames" como % de frames que excedieron
  el presupuesto de 16.6 ms (60 Hz). Jank < 10% equivale a > 54 fps sostenido.
- Para un pipeline reproducible/CI conviene migrar a un módulo **Macrobenchmark**
  (`StartupTimingMetric` + `FrameTimingMetric`); se difirió por no poder cablear
  ni verificar un módulo Gradle nuevo sin entorno de build. El método `adb` da la
  misma evidencia para H2.
