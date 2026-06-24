# QLess - Arquitectura de alto nivel

## Actores y Componentes Principales

* **Usuario (Cliente / Local):** Interactúa con la aplicación móvil.
* **Dispositivo Android:** Contenedor principal del sistema cliente.
    * **QLess Mobile App (Kotlin + Jetpack Compose):** La aplicación en sí, dividida en capas.
    * **Room Database:** Base de datos para persistencia local.
* **Backend:** Servidor remoto.
    * **API REST:** Interfaz de comunicación.
    * **PostgreSQL (Supabase):** Base de datos principal.

---

## Capas de la Aplicación (QLess Mobile App)

### 1. Presentation Layer (Capa de Presentación)
Gestiona la interfaz de usuario y la interacción directa con el usuario.
* **Pantallas Compose:** Recibe la interacción del usuario. 
    * Delega eventos de UI a los *ViewModels*.
    * Gestiona la navegación a través de *Navigation Compose*.
    * Inicia sesión comunicándose con *Google SSO* (Data Layer).
* **ViewModels:** Ejecutan acciones llamando a los *Casos de uso* (Domain Layer).
* **Navigation Compose:** Encargado de enrutar las vistas.

### 2. Domain Layer (Capa de Dominio)
Contiene la lógica de negocio principal.
* **Casos de Uso:** Orquestan la lógica.
    * Solicitan datos a los *Contratos de repositorio*.
    * Aplican reglas de negocio utilizando los *Modelos de dominio*.
    * Interactúan con los *Servicios del dispositivo* (GPS, Cámara, Sonido, Vibración).
* **Contratos de repositorio:** Interfaces que definen cómo se obtienen los datos (implementadas por la Data Layer).
* **Modelos de dominio:** Entidades puras de negocio.

### 3. Data Layer (Capa de Datos)
Gestiona el origen de los datos (local o remoto) y la autenticación.
* **Repositorios:** Implementan los contratos de la capa de dominio. Deciden si buscar datos locales o remotos.
    * Piden datos locales al *Local Data Source*.
    * Piden datos remotos al *Remote Data Source*.
* **Local Data Source:** Realiza lectura y escritura en la *Room Database* (incluye el
  centro de notificaciones, tabla `notifications`).
* **Remote Data Source:** Se comunica vía HTTP / JSON con la *API REST* del backend y,
  para el estado de pedidos en vivo, por **Supabase Realtime** (Postgres Changes) sobre
  un WebSocket.
* **Google SSO:** Gestiona la autenticación delegada comunicándose con la *API REST*.

### 4. Servicios del dispositivo
Hardware y funciones nativas accedidas por los casos de uso:
* **GPS Ubicación:** Para detectar local cercano.
* **Cámara Escaneo QR:** Para escanear códigos QR.
* **Sonido Avisos:** Para avisar cuando un pedido está listo.
* **Vibración Alertas:** Para alertar sobre cambios de estado.
* **Notificaciones (`SystemNotifier`):** Bandeja del sistema (`NotificationManager`) ante
  cambios de estado del pedido detectados por Realtime. Abstracción en `domain/`, impl en
  `data/`, inyectada por `AppModule`.

---

## Diagrama (Mermaid)

> Vista de alto nivel (slide). El flujo principal es horizontal: la UI invoca
> casos de uso de dominio, que se resuelven en datos locales/remotos. `AppModule`
> cablea todo (composition root manual).

```mermaid
flowchart LR
    User(["👤 Cliente / BackOffice"])

    subgraph PRES["PRESENTATION · ui + navigation"]
        UI["Pantallas Compose"]
        VM["ViewModels<br/>StateFlow / eventos"]
        NAV["AppNavigation"]
    end

    subgraph DOM["DOMAIN · Kotlin puro"]
        UC["Casos de uso"]
        CON["Contratos<br/>(repos + device)"]
        MOD["Modelos"]
    end

    subgraph DAT["DATA"]
        REPO["Repositorios<br/>network-first + caché"]
        ROOM[("QLessDatabase<br/>Room")]
        STORE[("DataStore")]
        SUPA["SupabaseClient<br/>Auth · Postgrest · Realtime"]
        DEV["Providers de dispositivo<br/>GPS · QR · Notificaciones"]
    end

    subgraph EXT["BACKEND / SO"]
        SB[("Supabase<br/>PostgreSQL + RLS")]
        OS["Sensores Android<br/>+ NotificationManager"]
    end

    DI["AppModule · composition root"]

    User --> UI
    UI <--> VM
    UI --> NAV
    VM --> UC
    UC --> CON
    UC -.-> MOD
    REPO -. implementa .-> CON
    DEV -. implementa .-> CON
    REPO --> ROOM & STORE & SUPA
    DEV --> OS
    SUPA --> SB
    DI -. cablea .-> VM & REPO & UC

    classDef pres fill:#FFEDE0,stroke:#C44B1B,stroke-width:2px,color:#2C1A0E
    classDef dom fill:#FFF3D6,stroke:#D4870E,stroke-width:2px,color:#2C1A0E
    classDef dat fill:#D6ECFA,stroke:#1D6FA8,stroke-width:2px,color:#2C1A0E
    classDef ext fill:#FFF8EE,stroke:#7A5C3E,stroke-width:2px,color:#2C1A0E
    classDef di fill:#C44B1B,stroke:#2C1A0E,stroke-width:2px,color:#FFFBF5
    classDef actor fill:#1A7A4A,stroke:#2C1A0E,stroke-width:2px,color:#FFFBF5

    class UI,VM,NAV pres
    class UC,CON,MOD dom
    class REPO,ROOM,STORE,SUPA,DEV dat
    class SB,OS ext
    class DI di
    class User actor

    style PRES fill:#FFFBF5,stroke:#C44B1B,stroke-width:1px,color:#C44B1B
    style DOM fill:#FFFBF5,stroke:#D4870E,stroke-width:1px,color:#D4870E
    style DAT fill:#FFFBF5,stroke:#1D6FA8,stroke-width:1px,color:#1D6FA8
    style EXT fill:#FFFBF5,stroke:#7A5C3E,stroke-width:1px,color:#7A5C3E
```

---

## Estructura y responsabilidad de capas

Este diagrama no muestra el flujo de datos sino **qué carpeta hace qué, de qué
depende y de qué tiene prohibido depender**. La regla de oro es la flecha de
dependencia: siempre apunta hacia `domain/`, que no depende de nadie.

```mermaid
flowchart TB
    classDef pres fill:#FFEDE0,stroke:#C44B1B,stroke-width:2px,color:#2C1A0E
    classDef dom fill:#FFF3D6,stroke:#D4870E,stroke-width:2px,color:#2C1A0E
    classDef dat fill:#D6ECFA,stroke:#1D6FA8,stroke-width:2px,color:#2C1A0E
    classDef root fill:#C44B1B,stroke:#2C1A0E,stroke-width:2px,color:#FFFBF5

    subgraph P["⬛ PRESENTATION · ui/ + navigation/"]
        direction LR
        P1["RESPONSABILIDAD\nRenderizar estado y capturar eventos.\nCero lógica de negocio, cero acceso a datos."]
        P2["ui/screens · Composables\nui/components · reutilizables\nui/theme · Material 3 / tokens"]
        P3["ui/viewmodel · expone StateFlow<UiState>\norquesta use cases, mapea a UI, eventos one-shot"]
        P4["navigation/AppNavigation\nrutas, scope de VMs, gates de carrito/pedido"]
    end

    subgraph D["⬛ DOMAIN · domain/ — Kotlin PURO"]
        direction LR
        D1["RESPONSABILIDAD\nReglas de negocio. No conoce Android, Room,\nSupabase ni Compose. Es el centro estable."]
        D2["model/ · entidades de negocio + CachedResult<T>"]
        D3["repository/ · CONTRATOS (interfaces)\nlocation/ notification/ session/ · contratos device"]
        D4["usecase/ · una acción de negocio c/u\n(PlaceOrder valida carrito, RankLocalsByDistance…)"]
    end

    subgraph DA["⬛ DATA · data/"]
        direction LR
        DA1["RESPONSABILIDAD\nIMPLEMENTAR los contratos de domain.\nDecidir red vs caché, mapear DTO/Entity ↔ modelo."]
        DA2["repository/ · …Impl (network-first + fallback)\nremote/ · DataSources + dto + SupabaseClient\nlocal/ · Room (DAO+Entity) · DataStore\nproviders · Fused / SystemNotifier / Session"]
    end

    R["⬛ di/AppModule · COMPOSITION ROOT (object manual)\nÚnico lugar que conoce las 3 capas: cablea impl → contrato → use case"]

    P -->|"depende de ▶"| D
    DA -->|"implementa ▶"| D
    R -.->|"construye y entrega"| P
    R -.->|"construye"| DA

    class P,P1,P2,P3,P4 pres
    class D,D1,D2,D3,D4 dom
    class DA,DA1,DA2 dat
    class R root

    NOTE["REGLA DE DEPENDENCIA: las flechas sólidas apuntan a DOMAIN.\ndomain/ NO importa Android/Room/Supabase/Compose.\nUI nunca toca data/ directo; siempre vía use case → contrato."]
    class NOTE ext

    classDef ext fill:#FFF8EE,stroke:#7A5C3E,stroke-width:2px,color:#2C1A0E

    style P fill:#FFFBF5,stroke:#C44B1B,stroke-width:1px,color:#C44B1B
    style D fill:#FFFBF5,stroke:#D4870E,stroke-width:1px,color:#D4870E
    style DA fill:#FFFBF5,stroke:#1D6FA8,stroke-width:1px,color:#1D6FA8
```

**Una frase por capa:**

- **`presentation`** depende de `domain` y de nada más hacia abajo; el ViewModel
  es el único que orquesta use cases y traduce a `UiState`. Que la UI no importe
  `data/` es lo que mantiene la regla.
- **`domain`** es el núcleo puro: define *qué* se hace (use cases) y *qué
  contrato* necesita (interfaces de repo/device), sin saber *cómo*.
- **`data`** es la única que sabe *cómo* (Supabase, Room, DataStore, Play
  Services) e **implementa** los contratos; la flecha invertida (data→domain) es
  la inversión de dependencias.
- **`di/AppModule`** es el único punto que ve las tres capas y las cose; por eso
  vive aparte y no "pertenece" a ninguna.

> Desviación honesta: hoy la flecha `AppModule → presentation` es *pull* (los VMs
> piden a `AppModule`), no *push* por constructor. Es el punto service-locator
> pendiente de migrar a Hilt (ver `.claude/pendientes.md` C3).

---

## Mapa de persistencia

Qué dato vive en cada mecanismo de almacenamiento.

```mermaid
flowchart TB
    subgraph SUPA["☁️ Supabase · PostgreSQL (remoto, fuente de verdad)"]
        direction LR
        S1["perfiles<br/>nombre · email · rol · favoritos<br/>descuento_1ra · activo"]
        S2["locales · menu_items"]
        S3["orders · order_items"]
    end

    subgraph AUTH["🔑 Supabase Auth (remoto)"]
        A1["credenciales email/password<br/>sesión + tokens (access / refresh)"]
    end

    subgraph ROOM["💾 Room · QLessDatabase (local)"]
        direction LR
        R1["carrito<br/>(cart_items, localId por ítem)"]
        R2["métodos de pago"]
        R3["caché offline<br/>locales · menu_items"]
        R4["notificaciones<br/>(por userId)"]
        R5["user (caché perfil, sin uso)"]
    end

    subgraph DS["⚙️ DataStore Preferences (local)"]
        direction LR
        D1["qless_session<br/>JSON de sesión persistida"]
        D2["qless_settings<br/>tema oscuro · onboarding"]
        D3["preferencias de notificaciones<br/>estado · listo · sonido/vibración"]
    end

    classDef supa fill:#D6ECFA,stroke:#1D6FA8,stroke-width:2px,color:#2C1A0E
    classDef auth fill:#FFEDE0,stroke:#C44B1B,stroke-width:2px,color:#2C1A0E
    classDef room fill:#D6F5E3,stroke:#1A7A4A,stroke-width:2px,color:#2C1A0E
    classDef ds fill:#FFF3D6,stroke:#D4870E,stroke-width:2px,color:#2C1A0E

    class S1,S2,S3 supa
    class A1 auth
    class R1,R2,R3,R4,R5 room
    class D1,D2,D3 ds

    style SUPA fill:#FFFBF5,stroke:#1D6FA8,stroke-width:1px,color:#1D6FA8
    style AUTH fill:#FFFBF5,stroke:#C44B1B,stroke-width:1px,color:#C44B1B
    style ROOM fill:#FFFBF5,stroke:#1A7A4A,stroke-width:1px,color:#1A7A4A
    style DS fill:#FFFBF5,stroke:#D4870E,stroke-width:1px,color:#D4870E
```

> Room cachea `locales`/`menu_items` para el modo offline (RF4); Supabase sigue
> siendo la fuente de verdad. Los pedidos **no** se cachean en Room: se leen en
> vivo por Realtime y solo viven en Supabase.
