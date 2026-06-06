# Documento de diseño: persistencia de datos y arquitectura

**Proyecto:** QLess (beeper virtual para pedidos en restaurantes)
**Materia:** Desarrollo de Aplicaciones I
**Stack:** Kotlin + Jetpack Compose · MVVM + Repository · Room · Retrofit/Gson · Supabase (PostgREST + Auth + Realtime) · DataStore · FCM

---

## 1. Principio rector

La UI no sabe de dónde salen los datos. La UI pide datos y emite eventos. Cada dato vive en **un solo lugar como fuente de verdad** y se copia hacia otras capas solo como caché explícita. Duplicar la fuente de verdad genera inconsistencias imposibles de debuggear, así que el criterio para ubicar cualquier dato es una sola pregunta: *¿quién es el dueño autoritativo de este dato?*

Tres destinos de fuente de verdad:

- **Postgres (Supabase):** datos compartidos entre dispositivos o con integridad crítica.
- **DataStore:** preferencias y flags propios de *este* dispositivo.
- **Estado de Compose (`remember`):** estado efímero de pantalla.

Room no es una cuarta fuente de verdad. Room es la **caché local observable** de lo que vive en Postgres, y es lo que habilita el modo offline.

---

## 2. Tabla de decisión de datos

| Dato | Fuente de verdad | Caché local | Detalle |
|---|---|---|---|
| **Sesión y credenciales** | | | |
| Email + contraseña (hash) | Supabase Auth (`auth.users`) | No | Fuera de nuestro alcance. Nunca guardamos contraseñas, ni siquiera hasheadas. |
| JWT de sesión | Supabase Auth SDK | DataStore (cifrado por el SDK) | Lo gestiona el SDK. Lo inyectamos en cada request vía interceptor de OkHttp. |
| **Perfil de usuario** | | | |
| `id`, `nombre`, `email`, `rol` | Postgres (`perfiles`) | Room | `rol` define cliente vs staff. Lo lee el backend para autorización (RLS), no la app. |
| `primer_uso_disponible` (flag descuento) | **Postgres (`perfiles`)** | No cachear como verdad | Integridad crítica. Si vive en el dispositivo, se resetea con la reinstalación y el descuento es infinito. |
| **Catálogo** | | | |
| Locales (`nombre`, dirección, lat/lng) | Postgres (`locales`) | Room | Verdad en servidor, lectura offline del listado. |
| Productos / menú | Postgres (`productos`) | Room | El catálogo abre al instante sin red desde Room. |
| **Pedidos** | | | |
| Pedido + items | Postgres (`pedidos`, `items_pedido`) | Room (solo los propios) | Cliente y staff comparten el dato. Cacheamos los pedidos del usuario para su historial offline, no los de terceros. |
| Estado del pedido | **Postgres** (enum `estado_pedido`) | Room (reflejo) | Lo actualiza el staff, lo lee el cliente. Llega en tiempo real vía Supabase Realtime. |
| **Pago (simulado)** | | | |
| `metodo_pago` = `'simulado'` | Postgres (`pedidos`) | Room | No hay datos de tarjeta. No existe ningún input de tarjeta en la app. |
| `estado_pago` = `'confirmado'` | Postgres (`pedidos`) | Room | Forma de un sistema real sin procesar pagos reales. |
| Número de tarjeta / CVV / vencimiento | **En ningún lado** | No | PCI-DSS. Fuera de scope. Ni siquiera lo pedimos. |
| **Notificaciones** | | | |
| Token FCM | Postgres (`dispositivos`) | No | El backend decide a qué dispositivo mandar el push "pedido listo". Un usuario puede tener varios dispositivos. |
| **Preferencias del dispositivo** | | | |
| Tema oscuro (toggle) | DataStore | No | Preferencia de este teléfono. Si quisiéramos que siga al usuario entre dispositivos, iría a Postgres. |
| Onboarding visto | DataStore | No | Flag de instalación inicial, controla el flujo de onboarding. |
| Local favorito / último usado | DataStore | No | Decisión local, no parte del dominio compartido. |
| **Estado efímero de UI** | | | |
| Texto en inputs, item seleccionado, scroll | `remember` / `rememberSaveable` en Compose | No | Se pierde al cerrar la pantalla. No persiste en ningún lado. |

---

## 3. Decisión sobre el pago

El pago se **simula**. No se captura ningún dato de tarjeta, no existe ningún campo de tarjeta en ningún input ni en ninguna tabla. El usuario confirma el pedido en una pantalla de checkout y el pedido se crea con `metodo_pago = 'simulado'` y `estado_pago = 'confirmado'`.

Los campos `metodo_pago` y `estado_pago` se incluyen igual en el modelo para que el esquema tenga la forma de un sistema de pagos real. Esto deja la puerta abierta a integrar un proveedor (Mercado Pago en sandbox) en H2 si sobra tiempo, sin rediseñar el modelo. En ese escenario futuro, el proveedor tokeniza la tarjeta y nosotros guardaríamos como mucho `payment_id`, `estado_pago`, `ultimos_4` y `marca`, nunca el número completo.

---

## 4. Capas de la arquitectura

El patrón es MVVM + Repository con separación en capas. De arriba hacia abajo:

**Presentation (UI).** Pantallas en Jetpack Compose que observan un `UiState` expuesto por el ViewModel. El `UiState` se modela como `sealed interface` con estados `Loading`, `Success(data)` y `Error(msg)`. La UI no llama a repositorios ni hace lógica de negocio: renderiza el estado y dispara eventos hacia el ViewModel.

**ViewModel.** Expone el `UiState` mediante `StateFlow`. Traduce eventos de UI en llamadas a UseCases. No conoce Room ni Retrofit. Las llamadas de carga inicial van en `init { }` o disparadas por `LaunchedEffect`, nunca escritas directo en el body de un composable (eso se ejecuta en cada recomposición y genera loops y requests duplicados).

**Domain.** UseCases (`ObtenerPedidosUseCase`, `CrearPedidoUseCase`, `ActualizarEstadoUseCase`, `VerificarDescuentoUseCase`), modelos de dominio puros y contratos de repositorio (interfaces). Esta capa no depende de Android ni de ninguna librería de datos. Acá viven las reglas del dominio, como "el descuento se aplica solo si `primer_uso_disponible` es true".

**Data.** Implementación de los repositorios, que coordinan dos fuentes:
- **LocalDataSource:** Room (Entity, DAO, Database). Fuente de verdad local y caché observable.
- **RemoteDataSource:** Retrofit contra PostgREST de Supabase, más Supabase Auth para login y Realtime para el estado del pedido.

El repositorio decide cuándo leer de local, cuándo pegarle al remoto y cómo reconciliar. La UI nunca toca un DataSource directamente.

```
UI (Compose)  ←observa StateFlow←  ViewModel  →usa→  UseCases  →contrato→  Repository
                                                                              ├─ LocalDataSource (Room)
                                                                              └─ RemoteDataSource (Retrofit/Supabase)
```

---

## 5. Flujo de comunicación entre capas (Offline First)

La regla de oro para los datos compartidos: **la UI siempre lee de Room, el Repository sincroniza Room contra Supabase en background.** Room es la Single Source of Truth local.

**Lectura (ej. catálogo o historial de pedidos):**

```
1. La UI observa repository.observarPedidos() → Flow<List<Pedido>> desde Room.
   → Muestra de inmediato lo que haya en local, con o sin red.

2. Al entrar a la pantalla o en pull-to-refresh:
   repository.sincronizar()
     → RemoteDataSource pega a PostgREST (Retrofit)
     → si OK: actualiza Room (LocalDataSource)
     → Room emite el cambio por Flow → la UI recompone sola.

3. Si no hay red: el paso 2 falla en silencio, la UI sigue mostrando
   lo de Room. El error de conectividad se maneja sin romper la pantalla.
```

**Escritura (ej. crear pedido):**

```
1. La UI dispara el evento → ViewModel → CrearPedidoUseCase → Repository.
2. El Repository hace POST a PostgREST.
3. Con la respuesta OK, inserta el pedido en Room.
4. Room emite → la UI ve su pedido nuevo en el historial.
```

**Estado del pedido en tiempo real (el corazón del beeper):**

```
1. El staff cambia el estado desde el backoffice → PATCH a PostgREST.
2. Supabase Realtime empuja el cambio por websocket a la app del cliente.
3. El RemoteDataSource recibe el evento y actualiza Room.
4. Room emite → la UI muestra "Listo para retirar" sin que el cliente refresque.
5. En paralelo, el backend dispara un push FCM al token del dispositivo
   para avisar aunque la app esté cerrada.
```

Este último flujo es lo que hace que la app sea un beeper y no una pantalla que hay que estar refrescando a mano.

---

## 6. Mapeo entre capas (DTO → Entity → Domain)

Cada capa tiene su propia representación del dato y no se filtra hacia las otras. Esto evita que un cambio en el JSON del backend rompa la UI.

- **DTO:** lo que devuelve PostgREST, anotado para Gson. Vive en la capa de datos.
- **Entity:** lo que persiste Room (`@Entity`). Vive en la capa de datos.
- **Domain:** el modelo limpio que consumen UseCases y ViewModel.

Los mappers (`dto.toEntity()`, `entity.toDomain()`) son funciones de extensión en la capa de datos. El ViewModel y la UI solo ven modelos de dominio.

---

## 7. Autenticación y autorización

La autenticación va por Supabase Auth, con email/contraseña y federada con Google. El SDK gestiona el JWT y nosotros lo inyectamos en cada request de Retrofit con un `Interceptor` de OkHttp que agrega los headers `Authorization: Bearer <jwt>` y `apikey`.

La **autorización vive en el backend, no en la app.** Cliente y staff comparten backend pero ven cosas distintas: el cliente solo sus propios pedidos, el staff los pedidos de su local. Esto se resuelve con **Row Level Security (RLS)** en Postgres, con policies atadas al `rol` del perfil y al `auth.uid()` de la sesión. Dejar la autorización del lado del cliente es el error donde se cae la mayoría de los equipos: si el filtro de "solo mis pedidos" está solo en la app, cualquiera que arme el request a mano ve los pedidos de todos.

### Confirmación de correo (decisión de entrega)

**Estado actual:** la confirmación de correo está deshabilitada para facilitar el desarrollo y las demos.

Esto implica dos cosas que van de la mano y deben cambiarse juntas al habilitar la confirmación:

1. **Dashboard de Supabase:** `Authentication → Configuration → Email → "Confirm email"` debe estar en **OFF** (actualmente) o **ON** (producción).

2. **Código (`AuthRemoteDataSource.kt`):** la constante `AUTO_SIGNIN_AFTER_REGISTER` controla si la app hace sign-in automático inmediatamente después del registro (bypasea el flujo de confirmación). Cuando se habilite la confirmación, setear a `false` y agregar una pantalla de "revisá tu correo".

Para habilitar la confirmación en una entrega futura:
- Prender el toggle en el dashboard de Supabase
- Setear `AUTO_SIGNIN_AFTER_REGISTER = false` en `AuthRemoteDataSource`
- Manejar el estado "pendiente de confirmación" en `AuthViewModel` y mostrar feedback al usuario

---

## 8. Antipatrones que evitamos (checklist de defensa)

- **Estado duplicado:** el mismo dato como verdad en Room, en la API y en el ViewModel a la vez. Lo evitamos con Single Source of Truth por dato.
- **Side effects sin control:** llamar a `viewModel.cargarDatos()` en el body de un composable. Lo evitamos con `LaunchedEffect` e `init { }`.
- **NavController inyectado en todos lados:** lo evitamos pasando callbacks específicos (`onBack`, `onLoginOk`, `onPedidoClick`).
- **SQL injection:** lo evitamos usando queries parametrizadas de Room (`@Query` con `:param`), nunca concatenando entrada de usuario.
- **Datos sensibles en el cliente:** contraseñas, flag de descuento y cualquier dato de tarjeta no viven en el dispositivo.

---

## 9. Resumen en una frase

La UI lee de Room. Room es caché de Postgres para todo lo compartido. DataStore guarda lo que solo le importa a este teléfono. El estado efímero vive en Compose. Lo sensible (contraseñas, tarjetas) no lo tocamos: lo maneja Supabase o, llegado el caso, un proveedor de pago externo.
