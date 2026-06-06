# Supabase Setup

Todos los pasos se ejecutan desde **Supabase → SQL Editor → New query**.

---

## 1. Crear tabla `perfiles` + RLS + Trigger

Pegá y ejecutá todo junto:

```sql
-- Tabla
create table public.perfiles (
  id     uuid references auth.users(id) on delete cascade primary key,
  nombre text not null,
  email  text not null,
  rol    text not null default 'USER'
           check (rol in ('USER', 'BACK_OFFICE'))
);

-- RLS
alter table public.perfiles enable row level security;

create policy "perfil_select_own"
  on public.perfiles for select
  using (auth.uid() = id);

-- Trigger: crea el perfil automáticamente en cada signup
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer set search_path = ''
as $$
begin
  insert into public.perfiles (id, nombre, email, rol)
  values (
    new.id,
    coalesce(new.raw_user_meta_data->>'name', ''),
    new.email,
    coalesce(new.raw_user_meta_data->>'role', 'USER')
  );
  return new;
end;
$$;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();
```

Si todo sale bien, Supabase devuelve `Success. No rows returned`.

---

## 2. Insertar usuario BackOffice

Solo si el usuario BackOffice ya existía en Auth **antes** de crear la tabla (el trigger no se ejecutó retroactivamente):

```sql
insert into public.perfiles (id, nombre, email, rol)
select id, 'Back Office', email, 'BACK_OFFICE'
from auth.users
where email = 'backoffice@gmail.com';
```

Si el usuario BackOffice todavía no existe, crealo desde **Authentication → Users → Add user** con:
- Email: `backoffice@gmail.com`
- Password: el que elijas
- Raw User Meta Data: `{ "name": "Back Office", "role": "BACK_OFFICE" }`

El trigger se encarga de insertar en `perfiles` automáticamente.

---

## 3. Deshabilitar confirmación de correo (desarrollo)

Para que los usuarios puedan registrarse y entrar sin confirmar el correo:

**Authentication → Configuration → Email → "Confirm email" → OFF**

Cuando se quiera habilitar para producción, ver las instrucciones en `documentation/DISENO_DATOS_Y_ARQUITECTURA.md` § 7.

---

## 4. Verificar

```sql
select * from public.perfiles;
```
