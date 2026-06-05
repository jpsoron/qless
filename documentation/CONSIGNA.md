# UADE: Facultad de Ingeniería y Ciencias Exactas

**Depto. Tecnología Informática**
**Fecha:** April 22, 2026
**Materia:** Desarrollo de Aplicaciones I: Aplicaciones Móviles
**Detalle:** Trabajo Práctico Grupal Integrador (Jueves Turno Noche. Campus Montserrat. Clases: 14526, 14528. Curso: 539402)

---

## Encuadre General

El presente trabajo práctico propone el desarrollo desde el diseño hasta la puesta en producción de una aplicación para dispositivos móviles con persistencia en un backend, debiendo para esto aplicar buenas prácticas de arquitectura, diseño, pruebas, integración y documentación. La consigna es de tipo abierta, es decir, los equipos de trabajo deberán elegir el dominio real, justificar adecuadamente el problema que se pretende resolver y diseñar la solución. El trabajo contará con 4 entregas disponibles en el cronograma, dos de las cuales resultarán de carácter obligatorio.

1. **H1 Obligatorio:** Figma, flujo de pantallas, repositorio inicializado, tablero de seguimiento, al menos 2 casos de uso, y APK Demo (puede ser mockeado). Diagrama inicial de arquitectura a alto nivel con descripción de las tecnologías elegidas.
2. **H2 Obligatorio:** Feature set completo, pruebas, métricas, apk RC, documentación final y defensa.

En términos de gestión del proyecto se deberán tener en cuenta los siguientes criterios:
1. Equipo de 3 estudiantes con roles (rotativos o fijos): Product Owner, Tech Lead, UX/UI, Backend Lead, QA/DevOps u otros que el equipo considere.
2. Seguimiento de backlog e historias de usuario con acceso por parte de la cátedra.

---

## Objetivos de Aprendizaje

A continuación se detallan los objetivos de aprendizaje:
1. Aplicar el concepto de diseño centrado en el usuario, esto es realizar una breve investigación, prototipado y validación.
2. Realizar de forma satisfactoria la implementación de arquitecturas modernas tales como MVVM, capas y repositorios.
3. Elegir y aplicar una metodología de desarrollo de software consistente que acompañe durante todo el ciclo de vida del producto.
4. Diseñar e integrar en la aplicación persistencia tanto a nivel local como en un servidor de backend utilizando para ello API Rest/GraphQL.
5. Diseñar e implementar pruebas unitarias y de calidad con métricas.
6. Elaborar documentación técnica y de usuario de calidad.
7. Presentar y defender técnicamente el producto solución.

---

## Requisitos funcionales

Se deberá implementar mínimamente los siguientes requisitos funcionales:
1. La aplicación deberá contar con un flujo de onboarding inicial al momento de la instalación inicial.
2. Al menos 3 flujos de pantallas diferentes, teniendo en consideración, debiendo tener al menos un CRUD completo referente al dominio principal.
3. La aplicación deberá contar autenticación mediante email/contraseña y/o federada mediante Cloud Services.
4. Deberá contar un modo offline en el caso de que el usuario no cuente con conectividad a internet. Tener presente una funcionalidad mínima.
5. Deberá contar con al menos un listado compuesto por cards view.
6. Uso de al menos un sensor y/o dispositivo de captura (audio o cámara).
7. Los tamaños de fuente deberán ser escalables, contar con content Description y tema oscuro. Opcionalmente se podrá implementar internacionalización.

---

## Requisitos no funcionales

1. El cold start deberá ser menor a 2.5 segundos en un dispositivo Google Pixel 9 Pro con 4GB de RAM y 2 Cores. Scroll fluido (mayor a 54 fps).
2. Buen manejo de errores de conectividad. Opcional implementar cola de tareas offline.
3. Evaluar de forma correcta el mínimo API Level de acuerdo con el público objetivo.

---

## Requisitos arquitectónicos

1. Lenguaje: La aplicación podrá ser desarrollada en Android Kotlin o React Native. En cualquier caso se deberá justificar su elección.
2. Patrón MVVM + Repository.
3. Vistas en Jetpack Compose.
4. Uso de librerías como retrofit2/Gson o similares para consumo de API / servicios de backend.
5. Accesibilidad: temas: Material 3, dark mode, dynamic color.

---

## Requisitos UI/UX y CX

Se deberá entregar el mapa de navegación, design system básico (tipos, colores, componentes) y prototipo navegable.
1. Aplicar Material Design 3 y heurísticas de Nielsen (evidenciar en un checklist).
2. Implementar buenas prácticas de accesibilidad.
3. Wireframes: prototipo de alta fidelidad en Figma.

---

## Ciclo de Desarrollo y colaboración

Se deberá entregar el mapa de navegación, design system básico (tipos, colores, componentes) y prototipo navegable.
1. Repositorio en GitHub/GitLab (público o privado con acceso de la cátedra).
2. Estrategia de ramas: trunk-based o GitFlow. Se deberá evidenciar trabajo en grupo.
3. Diagrama de alto nivel de la arquitectura de la solución.
4. Diagramas a presentar:
   * De Secuencia, al menos dos del flujo principal.

---

## Lineamientos para presentación final

1. Demo en vivo (10 min) + QA (5 min). Se realizarán preguntas grupales e individuales.
2. Pitch: problema, usuarios, métricas, arquitectura, decisiones clave y aprendizajes.
3. Benchmark corto frente a 1 app similar (pros/contras).
4. Entrega de RC (Release Candidate) + documentación completa.
5. Puntaje 60/100 y haber cumplido todos los hitos H1-H2.
6. Repos accesibles, builds reproducibles, demo funcional.

### 0.1 Uso responsable de IA
* Se permite apoyo con IA (copilots, LLMs) declarando: prompts relevantes, fragmentos generados y revisión humana realizada.
* Prohibido subir claves o datos de terceros.