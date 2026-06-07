# Supabase — Tabla `locales`

Todos los pasos se ejecutan desde **Supabase → SQL Editor → New query**.

---

## 1. Crear tabla + RLS

```sql
create table public.locales (
  id              uuid default gen_random_uuid() primary key,
  nombre          text not null,
  emoji           text not null default '🍽️',
  categoria       text not null,
  barrio          text not null,
  direccion       text not null,
  rating          numeric(2,1) not null default 0.0,
  tiempo_entrega  text,           -- ej: "15-25 min", null si está cerrado
  abierto         boolean not null default true,
  tiene_promo     boolean not null default false,
  destacado       boolean not null default false,
  created_at      timestamptz default now()
);

-- RLS: solo usuarios autenticados pueden leer
alter table public.locales enable row level security;

create policy "locales_select_authenticated"
  on public.locales for select
  to authenticated
  using (true);
```

---

## 2. Seed — locales de prueba

```sql
insert into public.locales (nombre, emoji, categoria, barrio, direccion, rating, tiempo_entrega, abierto, tiene_promo, destacado)
values
  ('Big Pons',    '🍔', 'Hamburguesas & Snacks',       'San Isidro', 'Av. del Libertador 1420, San Isidro', 4.8, '15-25 min', true,  true,  true),
  ('Sushi Nori',  '🍱', 'Japonesa · Rolls & Pokés',    'Palermo',    'Thames 1850, Palermo',                4.9, '20-30 min', true,  false, false),
  ('Pizza Mía',   '🍕', 'Italiana · Pastas y Pizzas',  'Recoleta',   'Av. Callao 890, Recoleta',            4.7, '20-35 min', true,  false, false),
  ('Green Bowl',  '🥗', 'Saludable · Bowls',           'Núñez',      'Av. Cabildo 2100, Núñez',             4.6, null,        false, false, false);
```

---

## 3. Verificar

```sql
select * from public.locales order by destacado desc, rating desc;
```

---

## Estructura de la tabla

| Columna | Tipo | Descripción |
|---|---|---|
| `id` | uuid | PK auto-generada |
| `nombre` | text | Nombre del local |
| `emoji` | text | Emoji representativo |
| `categoria` | text | Tipo de comida |
| `barrio` | text | Barrio / zona |
| `direccion` | text | Dirección completa |
| `rating` | numeric(2,1) | Puntaje 0.0–5.0 |
| `tiempo_entrega` | text | Rango estimado, ej: "15-25 min". `null` si cerrado |
| `abierto` | boolean | Estado actual del local |
| `tiene_promo` | boolean | Si tiene promoción activa |
| `destacado` | boolean | Si aparece primero en el listado |
