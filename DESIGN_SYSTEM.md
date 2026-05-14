# QLess — Design System

Referencia rápida del sistema de diseño implementado en `com.qless.ui.theme`.

---

## Estructura de archivos

```
app/src/main/java/com/qless/ui/theme/
├── Color.kt       → tokens de color y esquemas M3
├── Type.kt        → familias tipográficas y roles M3
└── Theme.kt       → QLessTheme(), KraftSurface, QLessExtras

app/src/main/res/drawable/
├── ic_qless_pimenton.xml   → isotipo color primario
├── ic_qless_espresso.xml   → isotipo oscuro
├── ic_qless_blanco.xml     → isotipo blanco (top bar, dark mode)
├── ic_launcher_background.xml
├── ic_launcher_foreground.xml
└── ic_launcher_full.xml    → ícono autónomo (fondo + isotipo)
```

---

## Theme

```kotlin
// Uso estándar — respeta el modo del sistema
QLessTheme { ... }

// Forzar modo
QLessTheme(darkTheme = true) { ... }

// Ajustar intensidad de la textura kraft (0f = sin textura)
QLessTheme(kraftAlpha = 0.06f) { ... }
```

> `dynamicColor` está desactivado intencionalmente. La paleta gastronómica requiere colores fijos.

---

## Colores

### Acceso desde composables

```kotlin
// Siempre via MaterialTheme — nunca hardcodear hex
MaterialTheme.colorScheme.primary          // Pimentón #C44B1B
MaterialTheme.colorScheme.onPrimary        // Blanco
MaterialTheme.colorScheme.surface          // Crema cálida #FFFBF5
MaterialTheme.colorScheme.surfaceVariant   // Mantequilla #FFF8EE
MaterialTheme.colorScheme.onSurface        // Espresso #2C1A0E
MaterialTheme.colorScheme.onSurfaceVariant // Madera #7A5C3E
MaterialTheme.colorScheme.error            // Borgoña #8C2A2A
```

### Estados del pedido — `QLessStatusColors`

Estos colores viven fuera del esquema M3. Usarlos directamente en badges y chips de tracking.

```kotlin
QLessStatusColors.disponible           // #1A7A4A — Albahaca
QLessStatusColors.disponibleSurface    // #D6F5E3 — fondo del badge
QLessStatusColors.enPreparacion        // #D4870E — Azafrán
QLessStatusColors.enPreparacionSurface // #FFF3D6
QLessStatusColors.enCamino             // #1D6FA8 — Arándano
QLessStatusColors.enCaminoSurface      // #D6ECFA
QLessStatusColors.agotadoError         // #8C2A2A — Borgoña
QLessStatusColors.agotadoSurface       // #FAD6D6
```

**Patrón de uso para un badge de estado:**

```kotlin
Box(
    modifier = Modifier
        .clip(RoundedCornerShape(99.dp))
        .background(QLessStatusColors.enPreparacionSurface)
        .padding(horizontal = 10.dp, vertical = 4.dp)
) {
    Text(
        text = "En preparación",
        color = QLessStatusColors.enPreparacion,
        style = MaterialTheme.typography.labelMedium,
    )
}
```

### Paleta de referencia rápida

| Token | Nombre | Hex | Rol M3 |
|---|---|---|---|
| `Pimentón` | Rojo teja | `#C44B1B` | `primary` |
| `Tomate` | Naranja | `#E8673A` | `inversePrimary` |
| `Melocotón` | Salmón claro | `#FFEDE0` | `primaryContainer` |
| `CremaCálida` | Blanco cálido | `#FFFBF5` | `background` / `surface` |
| `Mantequilla` | Crema | `#FFF8EE` | `surfaceVariant` |
| `Espresso` | Marrón oscuro | `#2C1A0E` | `onBackground` / `onSurface` |
| `Madera` | Marrón medio | `#7A5C3E` | `secondary` / `onSurfaceVariant` |

---

## Tipografía

Dos familias. Archivos TTF en `res/font/`.

| Familia | Archivo | Uso |
|---|---|---|
| **Lora** | `lora_semibold.ttf`, `lora_bold.ttf` | Display, Headline |
| **Plus Jakarta Sans** | `plus_jakarta_sans_*.ttf` | Title, Body, Label |

### Roles y cuándo usar cada uno

```kotlin
// Lora — headings visibles, nombre del local, secciones de menú
MaterialTheme.typography.headlineLarge   // 32sp Bold   — título de pantalla
MaterialTheme.typography.headlineMedium  // 28sp Bold   — nombre del local
MaterialTheme.typography.headlineSmall   // 24sp SemiBold — sección del menú, top bar

// Plus Jakarta Sans — toda la UI funcional
MaterialTheme.typography.titleLarge      // 22sp SemiBold — TopAppBar
MaterialTheme.typography.titleMedium     // 16sp SemiBold — nombre de ítem
MaterialTheme.typography.titleSmall      // 14sp Medium   — subtítulos de card
MaterialTheme.typography.bodyMedium      // 14sp Regular  — descripción de ítem
MaterialTheme.typography.bodySmall       // 12sp Regular  — metadata, timestamps
MaterialTheme.typography.labelLarge      // 14sp SemiBold — texto de botones
MaterialTheme.typography.labelMedium     // 12sp Medium   — chips, badges
MaterialTheme.typography.labelSmall      // 11sp Medium   — section headers uppercase
```

---

## Textura kraft — `KraftSurface`

Drop-in replacement de `Box` que aplica un patrón de puntos sutil sobre cualquier superficie. Evoca papel kraft sin ser literal.

```kotlin
// Uso básico — toma kraftAlpha del tema automáticamente
KraftSurface(
    modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .background(MaterialTheme.colorScheme.primaryContainer)
) {
    // contenido — header del local, splash, onboarding
}

// Override puntual de intensidad
KraftSurface(kraftAlpha = 0.08f) { ... }
```

> En dark mode, `kraftAlpha` se reduce automáticamente de `0.04f` a `0.02f` para no comprometer el contraste.

---

## Extras del tema — `QLessTheme.extras`

Valores custom que no viven en `MaterialTheme` pero son parte del design system.

```kotlin
val extras = QLessTheme.extras

extras.kraftAlpha      // Float — intensidad de la textura
extras.kraftColor      // Color — color base de los puntos
extras.menuCardRadius  // Float (16f) — radio de esquinas para cards de menú
extras.chipRadius      // Float (99f) — radio de chips (pill)
```

---

## Isotipo en composables

```kotlin
// Top bar y superficies con fondo primario
Icon(
    painter = painterResource(R.drawable.ic_qless_blanco),
    contentDescription = null,
    tint = Color.Unspecified,
    modifier = Modifier.size(36.dp),
)

// Sobre fondos claros (splash, onboarding)
Icon(
    painter = painterResource(R.drawable.ic_qless_pimenton),
    contentDescription = null,
    tint = Color.Unspecified,
)

// Sobre fondos crema (about, perfil)
Icon(
    painter = painterResource(R.drawable.ic_qless_espresso),
    contentDescription = null,
    tint = Color.Unspecified,
)
```

> Siempre `tint = Color.Unspecified` para que el VectorDrawable respete sus colores propios.
