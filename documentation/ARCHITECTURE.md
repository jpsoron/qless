# QLess - Arquitectura de alto nivel

## Actores y Componentes Principales

* **Usuario (Cliente / Local):** Interactúa con la aplicación móvil.
* **Dispositivo Android:** Contenedor principal del sistema cliente.
    * **QLess Mobile App (Kotlin + Jetpack Compose):** La aplicación en sí, dividida en capas.
    * **Room Database:** Base de datos para persistencia local.
* **Backend:** Servidor remoto.
    * **API REST:** Interfaz de comunicación.
    * **MySQL Database:** Base de datos principal.

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

```mermaid
flowchart LR
    User["Usuario\nCliente / Local"]

    subgraph Android["Dispositivo Android"]
        
        subgraph App["QLess Mobile App\nKotlin + Jetpack Compose"]
            
            subgraph Presentation["Presentation Layer"]
                UI["Pantallas Compose"]
                VM["ViewModels"]
                Nav["Navigation Compose"]
            end
            
            subgraph Domain["Domain Layer"]
                UC["Casos de uso"]
                Contracts["Contratos de repositorio"]
                Models["Modelos de dominio"]
            end
            
            subgraph Data["Data Layer"]
                Repo["Repositorios"]
                SSO["Google SSO"]
                LocalDS["Local Data Source"]
                RemoteDS["Remote Data Source"]
            end
            
            subgraph DeviceServices["Servicios del dispositivo"]
                GPS["GPS\nUbicación"]
                Cam["Cámara\nEscaneo QR"]
                Sound["Sonido\nAvisos"]
                Vib["Vibración\nAlertas"]
            end
            
        end
        Room[("Room Database\nPersistencia local")]
    end

    subgraph BackendSys["Backend"]
        API["API REST"]
        DB[("MySQL Database")]
    end

    %% Relaciones Usuario y Presentación
    User -- "interactúa con la app" --> UI
    UI -- "eventos de UI" --> VM
    UI -- "navega" --> Nav
    UI -- "inicio de sesión" --> SSO
    VM -- "ejecuta acciones" --> UC

    %% Relaciones Dominio
    UC -- "solicita datos" --> Contracts
    UC -- "aplica reglas del dominio" --> Models
    Repo -.-> Contracts

    %% Relaciones Datos
    Repo -- "datos locales" --> LocalDS
    Repo -- "datos remotos" --> RemoteDS
    LocalDS -- "lectura / escritura" --> Room
    RemoteDS -- "HTTP / JSON" --> API
    SSO -- "autenticación" --> API

    %% Relaciones Servicios Dispositivo
    UC -- "detectar local cercano" --> GPS
    UC -- "escanear QR" --> Cam
    UC -- "avisar pedido listo" --> Sound
    UC -- "alertar estado" --> Vib

    %% Relaciones Backend
    API -- "lectura / escritura" --> DB