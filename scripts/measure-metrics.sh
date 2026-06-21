#!/usr/bin/env bash
#
# QLess — medición de métricas no funcionales (RF5).
#
# Mide en un dispositivo real conectado por adb:
#   1. Cold start (objetivo de la consigna: < 2.5 s en Pixel 9 Pro)
#   2. Jank / fps de scroll (objetivo: > 54 fps  ⇔  jank < 10%)
#
# Requisitos:
#   - APK instalado en el dispositivo (release o debug; idealmente release).
#   - Un solo dispositivo conectado (`adb devices`).
#
# Uso:
#   ./scripts/measure-metrics.sh coldstart [iteraciones]
#   ./scripts/measure-metrics.sh jank
#
# La evidencia se vuelca a documentation/metrics/results.md (completar a mano
# con el modelo de dispositivo y la fecha).

set -euo pipefail

PKG="com.qless"
ACTIVITY="${PKG}/.MainActivity"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

# Resuelve el binario `adb`: PATH → $ADB → ANDROID_HOME/SDK_ROOT →
# local.properties (sdk.dir) → ubicaciones por defecto. Evita depender del PATH.
ADB=""
resolve_adb() {
  if command -v adb >/dev/null 2>&1; then ADB="adb"; return; fi

  local candidates=()
  [ -n "${ANDROID_HOME:-}" ]     && candidates+=("$ANDROID_HOME/platform-tools/adb")
  [ -n "${ANDROID_SDK_ROOT:-}" ] && candidates+=("$ANDROID_SDK_ROOT/platform-tools/adb")
  if [ -f "$ROOT_DIR/local.properties" ]; then
    local sdkdir
    sdkdir=$(grep -E '^sdk\.dir=' "$ROOT_DIR/local.properties" | cut -d= -f2- | tr -d '\r')
    [ -n "$sdkdir" ] && candidates+=("$sdkdir/platform-tools/adb")
  fi
  candidates+=(
    "$HOME/Library/Android/sdk/platform-tools/adb"
    "$HOME/Android/Sdk/platform-tools/adb"
  )

  local c
  for c in "${candidates[@]}"; do
    if [ -n "$c" ] && [ -x "$c" ]; then ADB="$c"; return; fi
  done

  echo "✗ No se encontró 'adb'. Agregá platform-tools al PATH o exportá ANDROID_HOME." >&2
  exit 1
}

require_device() {
  resolve_adb
  local n
  n=$("$ADB" devices | grep -cw "device" || true)
  if [ "$n" -eq 0 ]; then
    echo "✗ No hay dispositivos conectados ($ADB devices)." >&2
    exit 1
  fi
}

coldstart() {
  local iterations="${1:-10}"
  require_device
  echo "▶ Cold start — ${iterations} iteraciones de ${ACTIVITY}"
  echo "  (se hace force-stop antes de cada arranque para garantizar cold start)"
  local total=0 min=999999 max=0
  for i in $(seq 1 "$iterations"); do
    "$ADB" shell am force-stop "$PKG"
    sleep 1
    # TotalTime = tiempo hasta el primer frame de la Activity (ms).
    local t
    t=$("$ADB" shell am start -W -n "$ACTIVITY" \
          | awk -F': ' '/TotalTime/{print $2}' | tr -d '\r')
    [ -z "$t" ] && { echo "  iter $i: sin TotalTime (¿activity correcta?)"; continue; }
    echo "  iter $i: ${t} ms"
    total=$((total + t))
    [ "$t" -lt "$min" ] && min=$t
    [ "$t" -gt "$max" ] && max=$t
  done
  local avg=$((total / iterations))
  echo "──────────────────────────────"
  echo "  promedio: ${avg} ms · min: ${min} ms · max: ${max} ms"
  echo "  objetivo: < 2500 ms"
}

jank() {
  require_device
  echo "▶ Jank / fps — scroll en la pantalla activa"
  echo "  1) Abrí la pantalla a medir (ej. lista de locales) en el dispositivo."
  "$ADB" shell dumpsys gfxinfo "$PKG" reset >/dev/null
  echo "  2) Reseteado. Hacé scroll fluido ~10 s ahora..."
  read -r -p "  3) Cuando termines, Enter para leer las estadísticas." _
  "$ADB" shell dumpsys gfxinfo "$PKG" \
    | grep -E "Total frames rendered|Janky frames|50th|90th|95th|99th"
  echo "──────────────────────────────"
  echo "  objetivo: Janky frames < 10%  (⇔ >54 fps sostenido)"
}

case "${1:-}" in
  coldstart) coldstart "${2:-10}" ;;
  jank)      jank ;;
  *) echo "uso: $0 {coldstart [iteraciones] | jank}" >&2; exit 2 ;;
esac
