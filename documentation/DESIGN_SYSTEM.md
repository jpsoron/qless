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

### Tokens primitivos

Definidos en `Color.kt`. No referenciarlos directamente desde composables — usar siempre `MaterialTheme.colorScheme.*`.

| Token | Hex | Descripción |
|---|---|---|
| `CremaCálida` | `#FFFBF5` | Fondo principal modo claro |
| `Mantequilla` | `#FFF8EE` | Superficie variante modo claro |
| `Espresso` | `#2C1A0E` | Texto principal modo claro |
| `Madera` | `#7A5C3E` | Texto secundario modo claro |
| `Pimentón` | `#C44B1B` | Color primario (fijo, ej. botón login) |
| `Tomate` | `#E8673A` | Variante primaria |
| `Melocotón` | `#FFEDE0` | Contenedor primario modo claro |
| `PimentónDark` | `#FF7A45` | Primario en dark mode |
| `EspressoDark` | `#1A0F07` | Fondo principal dark mode |
| `MaderaOscura` | `#3D2B1A` | Superficie variante dark mode |

### Acceso desde composables

```kotlin
// Siempre via MaterialTheme — nunca hardcodear hex
// (excepto cuando el color debe ser fijo en ambos temas, ej. Pimentón en login)
MaterialTheme.colorScheme.primary          // Claro: #C44B1B · Oscuro: #FF7A45
MaterialTheme.colorScheme.onPrimary        // Claro: Blanco  · Oscuro: #1A0F07
MaterialTheme.colorScheme.primaryContainer // Claro: #FFEDE0 · Oscuro: #C44B1B
MaterialTheme.colorScheme.background       // Claro: #FFFBF5 · Oscuro: #1A0F07
MaterialTheme.colorScheme.surface          // Claro: #FFFBF5 · Oscuro: #1A0F07
MaterialTheme.colorScheme.surfaceVariant   // Claro: #FFF8EE · Oscuro: #3D2B1A
MaterialTheme.colorScheme.onSurface        // Claro: #2C1A0E · Oscuro: #EFDDD0
MaterialTheme.colorScheme.onSurfaceVariant // Claro: #7A5C3E · Oscuro: #D4A98A
MaterialTheme.colorScheme.error            // Claro: #8C2A2A · Oscuro: #FFB4AB
```

### Esquema claro — `LightColorScheme`

| Rol M3 | Token | Hex |
|---|---|---|
| `primary` | Pimentón | `#C44B1B` |
| `onPrimary` | — | `#FFFFFF` |
| `primaryContainer` | Melocotón | `#FFEDE0` |
| `onPrimaryContainer` | Espresso | `#2C1A0E` |
| `secondary` | Madera | `#7A5C3E` |
| `onSecondary` | — | `#FFFFFF` |
| `secondaryContainer` | Mantequilla | `#FFF8EE` |
| `onSecondaryContainer` | Espresso | `#2C1A0E` |
| `tertiary` | Azafrán | `#D4870E` |
| `tertiaryContainer` | AzafránClaro | `#FFF3D6` |
| `error` | Borgoña | `#8C2A2A` |
| `errorContainer` | BorgoñaClaro | `#FAD6D6` |
| `background` | CremaCálida | `#FFFBF5` |
| `surface` | CremaCálida | `#FFFBF5` |
| `surfaceVariant` | Mantequilla | `#FFF8EE` |
| `onSurface` | Espresso | `#2C1A0E` |
| `onSurfaceVariant` | Madera | `#7A5C3E` |
| `outline` | Madera | `#7A5C3E` |
| `outlineVariant` | Melocotón | `#FFEDE0` |
| `inverseSurface` | Espresso | `#2C1A0E` |
| `inverseOnSurface` | CremaCálida | `#FFFBF5` |
| `inversePrimary` | Tomate | `#E8673A` |

### Esquema oscuro — `DarkColorScheme`

| Rol M3 | Token | Hex |
|---|---|---|
| `primary` | PimentónDark | `#FF7A45` |
| `onPrimary` | EspressoDark | `#1A0F07` |
| `primaryContainer` | Pimentón | `#C44B1B` |
| `onPrimaryContainer` | Melocotón | `#FFEDE0` |
| `secondary` | — | `#D4A98A` |
| `onSecondary` | MaderaOscura | `#3D2B1A` |
| `secondaryContainer` | MaderaOscura | `#3D2B1A` |
| `onSecondaryContainer` | — | `#EFD5BC` |
| `tertiary` | — | `#FFCC70` |
| `tertiaryContainer` | — | `#543200` |
| `error` | — | `#FFB4AB` |
| `errorContainer` | — | `#93000A` |
| `background` | EspressoDark | `#1A0F07` |
| `surface` | EspressoDark | `#1A0F07` |
| `surfaceVariant` | MaderaOscura | `#3D2B1A` |
| `onSurface` | — | `#EFDDD0` |
| `onSurfaceVariant` | — | `#D4A98A` |
| `outline` | — | `#9E7A5A` |
| `outlineVariant` | MaderaOscura | `#3D2B1A` |
| `inverseSurface` | — | `#EFDDD0` |
| `inverseOnSurface` | Espresso | `#2C1A0E` |
| `inversePrimary` | Pimentón | `#C44B1B` |

### Estados del pedido — `QLessStatusColors`

Estos colores viven fuera del esquema M3. Usarlos directamente en badges y chips de tracking.

```kotlin
// Modo claro — superficies claras
QLessStatusColors.disponible           // #1A7A4A — Albahaca
QLessStatusColors.disponibleSurface    // #D6F5E3 — fondo del badge
QLessStatusColors.enPreparacion        // #D4870E — Azafrán
QLessStatusColors.enPreparacionSurface // #FFF3D6
QLessStatusColors.enCamino             // #1D6FA8 — Arándano
QLessStatusColors.enCaminoSurface      // #D6ECFA
QLessStatusColors.agotadoError         // #8C2A2A — Borgoña
QLessStatusColors.agotadoSurface       // #FAD6D6

// Modo oscuro — superficies oscuras
QLessStatusColors.disponibleDark           // #6DD9A0 — verde claro
QLessStatusColors.disponibleSurfaceDark    // #0D3D25 — verde muy oscuro
QLessStatusColors.enPreparacionDark        // #FFB74D — ámbar claro
QLessStatusColors.enPreparacionSurfaceDark // #3A2400 — ámbar muy oscuro
QLessStatusColors.enCaminoDark             // #64B5F6 — azul claro
QLessStatusColors.enCaminoSurfaceDark      // #0D3352 — azul muy oscuro
QLessStatusColors.agotadoErrorDark         // #EF9A9A — rojo claro
QLessStatusColors.agotadoSurfaceDark       // #3D0F0F — rojo muy oscuro
```

> ⚠️ Los tokens `*Dark` de `QLessStatusColors` están definidos en `Color.kt` como referencia para implementación futura. Hoy los badges de estado usan las variantes claras en ambos temas.

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

| Token | Nombre | Hex | Modo claro | Modo oscuro |
|---|---|---|---|---|
| `Pimentón` | Rojo teja | `#C44B1B` | `primary` | `primaryContainer` / botón login fijo |
| `PimentónDark` | Naranja intenso | `#FF7A45` | — | `primary` |
| `Tomate` | Naranja | `#E8673A` | `inversePrimary` | — |
| `Melocotón` | Salmón claro | `#FFEDE0` | `primaryContainer` | `onPrimaryContainer` |
| `CremaCálida` | Blanco cálido | `#FFFBF5` | `background` / `surface` | `inverseSurface` |
| `Mantequilla` | Crema | `#FFF8EE` | `surfaceVariant` | — |
| `Espresso` | Marrón oscuro | `#2C1A0E` | `onBackground` / `onSurface` | `onPrimary` |
| `EspressoDark` | Negro cálido | `#1A0F07` | — | `background` / `surface` |
| `Madera` | Marrón medio | `#7A5C3E` | `secondary` / `onSurfaceVariant` | — |
| `MaderaOscura` | Marrón oscuro | `#3D2B1A` | — | `surfaceVariant` |

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
