# QLess — Diagramas de secuencia

Cuatro diagramas del flujo principal (el **pedido**, dominio principal), dos del
lado **cliente** y dos del lado **BackOffice**. Entre los cuatro cubren el CRUD
completo del pedido:

| Operación | Diagrama |
|---|---|
| **Create** | Cliente 1 — Confirmar pedido (checkout) |
| **Read** (en vivo) | Cliente 2 — Seguimiento en tiempo real + notificación |
| **Update** | BackOffice 1 — Avanzar el estado del pedido |
| **Delete** (cancelación lógica) | BackOffice 2 — Cancelar pedido |

> Los nombres de participantes son las clases reales del código (MVVM + capas:
> Screen → ViewModel → UseCase → Repository → RemoteDataSource → Supabase).
> El "Delete" es una **baja lógica**: el pedido pasa a `status = cancelled` y sale
> de la lista de activos (`getActiveOrdersForLocal` filtra `neq cancelled`).

---

## Cliente 1 — Confirmar pedido (Create)

El cliente paga (MVP: efectivo al retirar) y se crea el pedido real en Supabase.

---

## Cliente 2 — Seguimiento en tiempo real + notificación (Read en vivo)

El estado del pedido llega por **Supabase Realtime**. El mismo flujo
actualiza el seguimiento y dispara la notificación de bandeja.

---

## BackOffice 1 — Avanzar el estado del pedido (Update)

El operador mueve el pedido un paso (`pending → preparing → ready → picked_up`).
El cambio se persiste y Realtime lo propaga al cliente (Cliente 2).

---

## BackOffice 2 — Cancelar pedido (Delete / baja lógica)

El operador cancela un pedido activo; con confirmación, el
pedido pasa a `cancelled`, sale de la lista de activos y el cliente es notificado.

---

