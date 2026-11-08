# GUÍA DE REQUESTS — Ejercicio5MyAPIGestor (para capturas)

- **Base URL:** `http://localhost:8082`
- **Header obligatorio en POST/DELETE con body:** `Content-Type: application/json`
- **Servidor:** Spring Boot en `localhost:8082` · **BD:** MariaDB `dbgestor`

> **IMPORTANTE (reproducibilidad):** en mi ejecución los `id` 203–229 fueron creados durante las pruebas y luego **borrados** para dejar la BD intacta. Si ves un `id` ya no existente, primero ejecuta los `add...` (sección Preparación) y consulta el listado (`/buscarUsuarios`, `/buscarProyectos`, etc.) para obtener los ids nuevos, o usa los registros reales que ya existen:
> - Usuario: **131**, **172**, **196**
> - Proyecto: **197**
> - Tarea: **199**
> - Comentario: **201**, **202**
> - Asignación: (ninguna, créala con `/addAsignacion`)

---

## 0. Preparación (datos base)

#### SETUP · crear usuario base
- **Método:** `POST`
- **URL:** `http://localhost:8082/addUsuario`
- **Body:** `{"nombre": "QA Tester", "correo_electronico": "qa_tester@test.com", "contrasena": "qa123", "rol": "Tester", "imagen_usuario": "#000000"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addUsuario" -H "Content-Type: application/json" -d '{"nombre": "QA Tester", "correo_electronico": "qa_tester@test.com", "contrasena": "qa123", "rol": "Tester", "imagen_usuario": "#000000"}'
```

#### SETUP · crear proyecto base
- **Método:** `POST`
- **URL:** `http://localhost:8082/addProyecto`
- **Body:** `{"nombre": "QA Proyecto", "descripcion": "Proyecto de pruebas", "fecha_inicio": "2026-01-01", "fecha_finalizacion": "2026-02-01", "estado_proyecto": "En proceso", "fondo": "#fff"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addProyecto" -H "Content-Type: application/json" -d '{"nombre": "QA Proyecto", "descripcion": "Proyecto de pruebas", "fecha_inicio": "2026-01-01", "fecha_finalizacion": "2026-02-01", "estado_proyecto": "En proceso", "fondo": "#fff"}'
```

#### SETUP · crear tarea base
- **Método:** `POST`
- **URL:** `http://localhost:8082/addTareas`
- **Body:** `{"nombre": "QA Tarea", "descripcion": "tarea de prueba", "fecha_vencimiento": "2026-01-15", "lista": "todo", "estado": "nuevo", "id_proyecto": 204}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addTareas" -H "Content-Type: application/json" -d '{"nombre": "QA Tarea", "descripcion": "tarea de prueba", "fecha_vencimiento": "2026-01-15", "lista": "todo", "estado": "nuevo", "id_proyecto": 204}'
```

#### SETUP · crear comentario base
- **Método:** `POST`
- **URL:** `http://localhost:8082/addComentario`
- **Body:** `{"id_usuario": "QA Tester", "id_tarea": 205, "texto_comentario": "comentario de prueba", "fecha_hora": "2026-01-02 10:00:00"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addComentario" -H "Content-Type: application/json" -d '{"id_usuario": "QA Tester", "id_tarea": 205, "texto_comentario": "comentario de prueba", "fecha_hora": "2026-01-02 10:00:00"}'
```

#### SETUP · crear asignacion base
- **Método:** `POST`
- **URL:** `http://localhost:8082/addAsignacion`
- **Body:** `{"id_usuario": 203, "id_tarea": 205, "fecha_asignacion": "2026-01-03"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addAsignacion" -H "Content-Type: application/json" -d '{"id_usuario": 203, "id_tarea": 205, "fecha_asignacion": "2026-01-03"}'
```

---

## 1. Pruebas Unitarias (camino feliz) — UT-01 a UT-28

#### UT-01 · getUsuarioLogin
- **Método:** `POST`
- **URL:** `http://localhost:8082/getUsuarioLogin`
- **Body:** `{"correo_electronico": "qa_tester@test.com", "contrasena": "qa123"}`
- **Respuesta observada:** `HTTP 200` → `{"id_usuario":203,"nombre":"QA Tester","correo_electronico":"qa_tester@test.com","contrasena":"qa123","rol":"Tester","imagen_usuario":"#000000"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/getUsuarioLogin" -H "Content-Type: application/json" -d '{"correo_electronico": "qa_tester@test.com", "contrasena": "qa123"}'
```

#### UT-02 · buscarUsuario
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarUsuario?id=203`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_usuario":203,"nombre":"QA Tester","correo_electronico":"qa_tester@test.com","contrasena":"qa123","rol":"Tester","imagen_usuario":"#000000"}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarUsuario?id=203"
```

#### UT-03 · addUsuario
- **Método:** `POST`
- **URL:** `http://localhost:8082/addUsuario`
- **Body:** `{"nombre": "Nuevo QA", "correo_electronico": "nuevo_qa@test.com", "contrasena": "pwd", "rol": "Estudiante", "imagen_usuario": "#123456"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addUsuario" -H "Content-Type: application/json" -d '{"nombre": "Nuevo QA", "correo_electronico": "nuevo_qa@test.com", "contrasena": "pwd", "rol": "Estudiante", "imagen_usuario": "#123456"}'
```

#### UT-04 · updateUsuario
- **Método:** `POST`
- **URL:** `http://localhost:8082/updateUsuario`
- **Body:** `{"id_usuario": 208, "nombre": "Nuevo QA Editado", "correo_electronico": "nuevo_qa@test.com", "contrasena": "pwd", "rol": "Docente", "imagen_usuario": "#654321"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/updateUsuario" -H "Content-Type: application/json" -d '{"id_usuario": 208, "nombre": "Nuevo QA Editado", "correo_electronico": "nuevo_qa@test.com", "contrasena": "pwd", "rol": "Docente", "imagen_usuario": "#654321"}'
```

#### UT-05 · deleteUsuario
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteUsuario`
- **Body:** `{"id_usuario": 208}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteUsuario" -H "Content-Type: application/json" -d '{"id_usuario": 208}'
```

#### UT-06 · buscarUsuarios
- **Método:** `POST`
- **URL:** `http://localhost:8082/buscarUsuarios`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 200` → `[{"id_usuario":131,"nombre":"Ana Paola Ramírez Vázquez","correo_electronico":"ana22533@cbtis75.edu.mx","contrasena":"0722","rol":"Estudiante","imagen_usuario":"#70FAB7"},{"id_usuario":172,"nombre":"Liliana Gómez Martínez","correo_electronico":"liliana22512@cbtis75.edu.mx","contra`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/buscarUsuarios"
```

#### UT-07 · buscarProyecto
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarProyecto?id_proyecto=204`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_proyecto":204,"nombre":"QA Proyecto","descripcion":"Proyecto de pruebas","fecha_inicio":"2026-01-01","fecha_finalizacion":"2026-02-01","estado_proyecto":"En proceso","fondo":"#fff"}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarProyecto?id_proyecto=204"
```

#### UT-08 · addProyecto
- **Método:** `POST`
- **URL:** `http://localhost:8082/addProyecto`
- **Body:** `{"nombre": "QA Proyecto 2", "descripcion": "desc", "fecha_inicio": "2026-03-01", "fecha_finalizacion": "2026-03-31", "estado_proyecto": "Nuevo", "fondo": "#abc"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addProyecto" -H "Content-Type: application/json" -d '{"nombre": "QA Proyecto 2", "descripcion": "desc", "fecha_inicio": "2026-03-01", "fecha_finalizacion": "2026-03-31", "estado_proyecto": "Nuevo", "fondo": "#abc"}'
```

#### UT-09 · updateProyecto
- **Método:** `POST`
- **URL:** `http://localhost:8082/updateProyecto`
- **Body:** `{"id_proyecto": 209, "nombre": "QA Proyecto 2 edit", "descripcion": "desc2", "fecha_inicio": "2026-03-02", "fecha_finalizacion": "2026-04-01", "estado_proyecto": "En proceso", "fondo": "#def"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/updateProyecto" -H "Content-Type: application/json" -d '{"id_proyecto": 209, "nombre": "QA Proyecto 2 edit", "descripcion": "desc2", "fecha_inicio": "2026-03-02", "fecha_finalizacion": "2026-04-01", "estado_proyecto": "En proceso", "fondo": "#def"}'
```

#### UT-10 · deleteProyecto
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteProyecto`
- **Body:** `{"id_proyecto": 209}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteProyecto" -H "Content-Type: application/json" -d '{"id_proyecto": 209}'
```

#### UT-11 · buscarProyectos
- **Método:** `POST`
- **URL:** `http://localhost:8082/buscarProyectos`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 200` → `[{"id_proyecto":197,"nombre":"My API Rest","descripcion":"Desarrollaran un Api Rest, que satisfaga un sistema de información con al menos 5 tablas.","fecha_inicio":"2024-05-31","fecha_finalizacion":"2024-06-29","estado_proyecto":"En proceso","fondo":"/Images/animacion1.gif"},{"id`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/buscarProyectos"
```

#### UT-12 · getTarea
- **Método:** `POST`
- **URL:** `http://localhost:8082/getTarea`
- **Body:** `{"id_proyecto": 204}`
- **Respuesta observada:** `HTTP 200` → `[{"id_tarea":205,"nombre":"QA Tarea","descripcion":"tarea de prueba","fecha_vencimiento":"2026-01-15","lista":"todo","estado":"nuevo","id_proyecto":204}]`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/getTarea" -H "Content-Type: application/json" -d '{"id_proyecto": 204}'
```

#### UT-13 · buscarTarea
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarTarea?id_tarea=205`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_tarea":205,"nombre":"QA Tarea","descripcion":"tarea de prueba","fecha_vencimiento":"2026-01-15","lista":"todo","estado":"nuevo","id_proyecto":204}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarTarea?id_tarea=205"
```

#### UT-14 · addTareas
- **Método:** `POST`
- **URL:** `http://localhost:8082/addTareas`
- **Body:** `{"nombre": "QA Tarea 2", "descripcion": "t2", "fecha_vencimiento": "2026-02-20", "lista": "doing", "estado": "nuevo", "id_proyecto": 204}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addTareas" -H "Content-Type: application/json" -d '{"nombre": "QA Tarea 2", "descripcion": "t2", "fecha_vencimiento": "2026-02-20", "lista": "doing", "estado": "nuevo", "id_proyecto": 204}'
```

#### UT-15 · updateTareas
- **Método:** `POST`
- **URL:** `http://localhost:8082/updateTareas`
- **Body:** `{"id_tarea": 210, "nombre": "QA Tarea 2 edit", "descripcion": "t2b", "fecha_vencimiento": "2026-02-21", "lista": "done", "estado": "terminado", "id_proyecto": 204}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/updateTareas" -H "Content-Type: application/json" -d '{"id_tarea": 210, "nombre": "QA Tarea 2 edit", "descripcion": "t2b", "fecha_vencimiento": "2026-02-21", "lista": "done", "estado": "terminado", "id_proyecto": 204}'
```

#### UT-16 · deleteTareas
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteTareas`
- **Body:** `{"id_tarea": 210}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteTareas" -H "Content-Type: application/json" -d '{"id_tarea": 210}'
```

#### UT-17 · buscarTareas
- **Método:** `POST`
- **URL:** `http://localhost:8082/buscarTareas`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 200` → `[{"id_tarea":199,"nombre":"Base de datos","descripcion":"Realizar la estructura de las tablas","fecha_vencimiento":"2024-06-13","lista":"terminado","estado":"danger","id_proyecto":197},{"id_tarea":205,"nombre":"QA Tarea","descripcion":"tarea de prueba","fecha_vencimiento":"2026-0`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/buscarTareas"
```

#### UT-18 · getComentario
- **Método:** `POST`
- **URL:** `http://localhost:8082/getComentario`
- **Body:** `{"id_tarea": 205}`
- **Respuesta observada:** `HTTP 200` → `[{"id_comentario":206,"id_usuario":"QA Tester","id_tarea":205,"texto_comentario":"comentario de prueba","fecha_hora":"2026-01-02 10:00:00"}]`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/getComentario" -H "Content-Type: application/json" -d '{"id_tarea": 205}'
```

#### UT-19 · buscarComentario
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarComentario?id=206`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_comentario":206,"id_usuario":"QA Tester","id_tarea":205,"texto_comentario":"comentario de prueba","fecha_hora":"2026-01-02 10:00:00"}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarComentario?id=206"
```

#### UT-20 · addComentario
- **Método:** `POST`
- **URL:** `http://localhost:8082/addComentario`
- **Body:** `{"id_usuario": "QA Tester", "id_tarea": 205, "texto_comentario": "nuevo comentario", "fecha_hora": "2026-01-05 09:30:00"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addComentario" -H "Content-Type: application/json" -d '{"id_usuario": "QA Tester", "id_tarea": 205, "texto_comentario": "nuevo comentario", "fecha_hora": "2026-01-05 09:30:00"}'
```

#### UT-21 · updateComentario
- **Método:** `POST`
- **URL:** `http://localhost:8082/updateComentario`
- **Body:** `{"id_comentario": 211, "id_usuario": "QA Tester", "id_tarea": 205, "texto_comentario": "editado", "fecha_hora": "2026-01-05 09:35:00"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/updateComentario" -H "Content-Type: application/json" -d '{"id_comentario": 211, "id_usuario": "QA Tester", "id_tarea": 205, "texto_comentario": "editado", "fecha_hora": "2026-01-05 09:35:00"}'
```

#### UT-22 · deleteComentario
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteComentario`
- **Body:** `   `
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteComentario" -H "Content-Type: application/json" -d '{"id_comentario": 211}'
```

#### UT-23 · buscarComentarios
- **Método:** `POST`
- **URL:** `http://localhost:8082/buscarComentarios`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 200` → `[{"id_comentario":201,"id_usuario":"Perla Joceline Martínez Meza","id_tarea":199,"texto_comentario":"Subirlo YA!!!","fecha_hora":"2024-06-26 13:48:00"},{"id_comentario":202,"id_usuario":"Liliana Gómez Martínez","id_tarea":199,"texto_comentario":"hola","fecha_hora":"2024-06-26 13:`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/buscarComentarios"
```

#### UT-24 · buscarAsignacion
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarAsignacion?id=207`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_asignacion":207,"id_usuario":203,"id_tarea":205,"fecha_asignacion":"2026-01-03"}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarAsignacion?id=207"
```

#### UT-25 · addAsignacion
- **Método:** `POST`
- **URL:** `http://localhost:8082/addAsignacion`
- **Body:** `{"id_usuario": 203, "id_tarea": 205, "fecha_asignacion": "2026-01-06"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addAsignacion" -H "Content-Type: application/json" -d '{"id_usuario": 203, "id_tarea": 205, "fecha_asignacion": "2026-01-06"}'
```

#### UT-26 · updateAsignacion
- **Método:** `POST`
- **URL:** `http://localhost:8082/updateAsignacion`
- **Body:** `{"id_asignacion": 212, "id_usuario": 203, "id_tarea": 205, "fecha_asignacion": "2026-01-07"}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/updateAsignacion" -H "Content-Type: application/json" -d '{"id_asignacion": 212, "id_usuario": 203, "id_tarea": 205, "fecha_asignacion": "2026-01-07"}'
```

#### UT-27 · deleteAsignacion
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteAsignacion`
- **Body:** `{"id_asignacion": 212}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteAsignacion" -H "Content-Type: application/json" -d '{"id_asignacion": 212}'
```

#### UT-28 · buscarAsignaciones
- **Método:** `POST`
- **URL:** `http://localhost:8082/buscarAsignaciones`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 200` → `[{"id_asignacion":207,"id_usuario":203,"id_tarea":205,"fecha_asignacion":"2026-01-03"}]`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/buscarAsignaciones"
```

---

## 2. Pruebas de Integración (casos borde) — IT-01 a IT-59

### 2.Nulos Tipo: Nulos

#### IT-01 · addUsuario nulls
- **Método:** `POST`
- **URL:** `http://localhost:8082/addUsuario`
- **Body:** `{"nombre": null, "correo_electronico": null, "contrasena": null, "rol": null, "imagen_usuario": null}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addUsuario" -H "Content-Type: application/json" -d '{"nombre": null, "correo_electronico": null, "contrasena": null, "rol": null, "imagen_usuario": null}'
```

#### IT-02 · addProyecto nulls
- **Método:** `POST`
- **URL:** `http://localhost:8082/addProyecto`
- **Body:** `{"nombre": null, "descripcion": null, "fecha_inicio": null, "fecha_finalizacion": null, "estado_proyecto": null, "fondo": null}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addProyecto" -H "Content-Type: application/json" -d '{"nombre": null, "descripcion": null, "fecha_inicio": null, "fecha_finalizacion": null, "estado_proyecto": null, "fondo": null}'
```

#### IT-03 · addTareas nulls
- **Método:** `POST`
- **URL:** `http://localhost:8082/addTareas`
- **Body:** `{"nombre": null, "descripcion": null, "fecha_vencimiento": null, "lista": null, "estado": null, "id_proyecto": null}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addTareas" -H "Content-Type: application/json" -d '{"nombre": null, "descripcion": null, "fecha_vencimiento": null, "lista": null, "estado": null, "id_proyecto": null}'
```

#### IT-04 · addComentario nulls
- **Método:** `POST`
- **URL:** `http://localhost:8082/addComentario`
- **Body:** `{"id_usuario": null, "id_tarea": null, "texto_comentario": null, "fecha_hora": null}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addComentario" -H "Content-Type: application/json" -d '{"id_usuario": null, "id_tarea": null, "texto_comentario": null, "fecha_hora": null}'
```

#### IT-05 · addAsignacion nulls
- **Método:** `POST`
- **URL:** `http://localhost:8082/addAsignacion`
- **Body:** `{"id_usuario": null, "id_tarea": null, "fecha_asignacion": null}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addAsignacion" -H "Content-Type: application/json" -d '{"id_usuario": null, "id_tarea": null, "fecha_asignacion": null}'
```

#### IT-06 · addUsuario vacio
- **Método:** `POST`
- **URL:** `http://localhost:8082/addUsuario`
- **Body:** `{}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addUsuario" -H "Content-Type: application/json" -d '{}'
```

#### IT-07 · addProyecto vacio
- **Método:** `POST`
- **URL:** `http://localhost:8082/addProyecto`
- **Body:** `{}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addProyecto" -H "Content-Type: application/json" -d '{}'
```

#### IT-08 · addTareas vacio
- **Método:** `POST`
- **URL:** `http://localhost:8082/addTareas`
- **Body:** `{}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addTareas" -H "Content-Type: application/json" -d '{}'
```

#### IT-09 · addComentario vacio
- **Método:** `POST`
- **URL:** `http://localhost:8082/addComentario`
- **Body:** `{}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addComentario" -H "Content-Type: application/json" -d '{}'
```

#### IT-10 · addAsignacion vacio
- **Método:** `POST`
- **URL:** `http://localhost:8082/addAsignacion`
- **Body:** `{}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addAsignacion" -H "Content-Type: application/json" -d '{}'
```

#### IT-11 · addUsuario sin body
- **Método:** `POST`
- **URL:** `http://localhost:8082/addUsuario`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.792+00:00","status":400,"error":"Bad Request","path":"/addUsuario"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addUsuario"
```

#### IT-12 · addProyecto sin body
- **Método:** `POST`
- **URL:** `http://localhost:8082/addProyecto`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.798+00:00","status":400,"error":"Bad Request","path":"/addProyecto"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addProyecto"
```

#### IT-13 · addTareas sin body
- **Método:** `POST`
- **URL:** `http://localhost:8082/addTareas`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.800+00:00","status":400,"error":"Bad Request","path":"/addTareas"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addTareas"
```

#### IT-14 · addComentario sin body
- **Método:** `POST`
- **URL:** `http://localhost:8082/addComentario`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.802+00:00","status":400,"error":"Bad Request","path":"/addComentario"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addComentario"
```

#### IT-15 · addAsignacion sin body
- **Método:** `POST`
- **URL:** `http://localhost:8082/addAsignacion`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.805+00:00","status":400,"error":"Bad Request","path":"/addAsignacion"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addAsignacion"
```

#### IT-16 · deleteUsuario sin body
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteUsuario`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.808+00:00","status":400,"error":"Bad Request","path":"/deleteUsuario"}`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteUsuario"
```

#### IT-17 · deleteProyecto sin body
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteProyecto`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.811+00:00","status":400,"error":"Bad Request","path":"/deleteProyecto"}`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteProyecto"
```

#### IT-18 · deleteTareas sin body
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteTareas`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.812+00:00","status":400,"error":"Bad Request","path":"/deleteTareas"}`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteTareas"
```

#### IT-19 · deleteComentario sin body
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteComentario`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.814+00:00","status":400,"error":"Bad Request","path":"/deleteComentario"}`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteComentario"
```

#### IT-20 · deleteAsignacion sin body
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteAsignacion`
- **Body:** _sin cuerpo (vacío)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.815+00:00","status":400,"error":"Bad Request","path":"/deleteAsignacion"}`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteAsignacion"
```

#### IT-21 · updateUsuario vacio
- **Método:** `POST`
- **URL:** `http://localhost:8082/updateUsuario`
- **Body:** `{}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/updateUsuario" -H "Content-Type: application/json" -d '{}'
```

#### IT-22 · updateProyecto vacio
- **Método:** `POST`
- **URL:** `http://localhost:8082/updateProyecto`
- **Body:** `{}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/updateProyecto" -H "Content-Type: application/json" -d '{}'
```

#### IT-23 · updateTareas vacio
- **Método:** `POST`
- **URL:** `http://localhost:8082/updateTareas`
- **Body:** `{}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/updateTareas" -H "Content-Type: application/json" -d '{}'
```

#### IT-24 · updateComentario vacio
- **Método:** `POST`
- **URL:** `http://localhost:8082/updateComentario`
- **Body:** `{}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/updateComentario" -H "Content-Type: application/json" -d '{}'
```

#### IT-25 · updateAsignacion vacio
- **Método:** `POST`
- **URL:** `http://localhost:8082/updateAsignacion`
- **Body:** `{}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/updateAsignacion" -H "Content-Type: application/json" -d '{}'
```

#### IT-26 · deleteUsuario id null
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteUsuario`
- **Body:** `{"id_usuario": null}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteUsuario" -H "Content-Type: application/json" -d '{"id_usuario": null}'
```

#### IT-27 · deleteProyecto id null
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteProyecto`
- **Body:** `{"id_proyecto": null}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteProyecto" -H "Content-Type: application/json" -d '{"id_proyecto": null}'
```

#### IT-28 · deleteTareas id null
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteTareas`
- **Body:** `{"id_tarea": null}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteTareas" -H "Content-Type: application/json" -d '{"id_tarea": null}'
```

#### IT-29 · deleteComentario id null
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteComentario`
- **Body:** `{"id_comentario": null}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteComentario" -H "Content-Type: application/json" -d '{"id_comentario": null}'
```

#### IT-30 · deleteAsignacion id null
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteAsignacion`
- **Body:** `{"id_asignacion": null}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteAsignacion" -H "Content-Type: application/json" -d '{"id_asignacion": null}'
```

#### IT-31 · getUsuarioLogin nulls
- **Método:** `POST`
- **URL:** `http://localhost:8082/getUsuarioLogin`
- **Body:** `{"correo_electronico": null, "contrasena": null}`
- **Respuesta observada:** `HTTP 200` → `{"id_usuario":null,"nombre":"000","correo_electronico":null,"contrasena":null,"rol":null,"imagen_usuario":null}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/getUsuarioLogin" -H "Content-Type: application/json" -d '{"correo_electronico": null, "contrasena": null}'
```

#### IT-32 · getTarea id null
- **Método:** `POST`
- **URL:** `http://localhost:8082/getTarea`
- **Body:** `{"id_proyecto": null}`
- **Respuesta observada:** `HTTP 200` → `[]`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/getTarea" -H "Content-Type: application/json" -d '{"id_proyecto": null}'
```

#### IT-33 · getComentario id null
- **Método:** `POST`
- **URL:** `http://localhost:8082/getComentario`
- **Body:** `{"id_tarea": null}`
- **Respuesta observada:** `HTTP 200` → `[]`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/getComentario" -H "Content-Type: application/json" -d '{"id_tarea": null}'
```

### 2.String a Int Tipo: String a Int

#### IT-34 · buscarUsuario id=abc
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarUsuario?id=abc`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.866+00:00","status":400,"error":"Bad Request","path":"/buscarUsuario"}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarUsuario?id=abc"
```

#### IT-35 · buscarProyecto id=texto
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarProyecto?id_proyecto=texto`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.867+00:00","status":400,"error":"Bad Request","path":"/buscarProyecto"}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarProyecto?id_proyecto=texto"
```

#### IT-36 · buscarTarea id=hola
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarTarea?id_tarea=hola`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.869+00:00","status":400,"error":"Bad Request","path":"/buscarTarea"}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarTarea?id_tarea=hola"
```

#### IT-37 · buscarComentario id=abc
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarComentario?id=abc`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.872+00:00","status":400,"error":"Bad Request","path":"/buscarComentario"}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarComentario?id=abc"
```

#### IT-38 · buscarAsignacion id=abc
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarAsignacion?id=abc`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.874+00:00","status":400,"error":"Bad Request","path":"/buscarAsignacion"}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarAsignacion?id=abc"
```

#### IT-39 · getTarea id_proyecto=abc
- **Método:** `POST`
- **URL:** `http://localhost:8082/getTarea`
- **Body:** `{"id_proyecto": "abc"}`
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.878+00:00","status":400,"error":"Bad Request","path":"/getTarea"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/getTarea" -H "Content-Type: application/json" -d '{"id_proyecto": "abc"}'
```

#### IT-40 · getComentario id_tarea=abc
- **Método:** `POST`
- **URL:** `http://localhost:8082/getComentario`
- **Body:** `{"id_tarea": "abc"}`
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.880+00:00","status":400,"error":"Bad Request","path":"/getComentario"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/getComentario" -H "Content-Type: application/json" -d '{"id_tarea": "abc"}'
```

#### IT-41 · deleteProyecto id=abc
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteProyecto`
- **Body:** `{"id_proyecto": "abc"}`
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.881+00:00","status":400,"error":"Bad Request","path":"/deleteProyecto"}`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteProyecto" -H "Content-Type: application/json" -d '{"id_proyecto": "abc"}'
```

#### IT-42 · deleteUsuario id=abc
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteUsuario`
- **Body:** `{"id_usuario": "abc"}`
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.882+00:00","status":400,"error":"Bad Request","path":"/deleteUsuario"}`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteUsuario" -H "Content-Type: application/json" -d '{"id_usuario": "abc"}'
```

#### IT-43 · addTareas id_proyecto=abc
- **Método:** `POST`
- **URL:** `http://localhost:8082/addTareas`
- **Body:** `{"nombre": "t", "descripcion": "d", "fecha_vencimiento": "2026-01-01", "lista": "l", "estado": "e", "id_proyecto": "abc"}`
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.884+00:00","status":400,"error":"Bad Request","path":"/addTareas"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addTareas" -H "Content-Type: application/json" -d '{"nombre": "t", "descripcion": "d", "fecha_vencimiento": "2026-01-01", "lista": "l", "estado": "e", "id_proyecto": "abc"}'
```

#### IT-44 · addAsignacion id_usuario=abc
- **Método:** `POST`
- **URL:** `http://localhost:8082/addAsignacion`
- **Body:** `{"id_usuario": "abc", "id_tarea": 205, "fecha_asignacion": "2026-01-01"}`
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.885+00:00","status":400,"error":"Bad Request","path":"/addAsignacion"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addAsignacion" -H "Content-Type: application/json" -d '{"id_usuario": "abc", "id_tarea": 205, "fecha_asignacion": "2026-01-01"}'
```

#### IT-45 · addComentario id_tarea=abc
- **Método:** `POST`
- **URL:** `http://localhost:8082/addComentario`
- **Body:** `{"id_usuario": "x", "id_tarea": "abc", "texto_comentario": "c", "fecha_hora": "2026-01-01 00:00:00"}`
- **Respuesta observada:** `HTTP 400` → `{"timestamp":"2026-10-13T04:36:03.886+00:00","status":400,"error":"Bad Request","path":"/addComentario"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addComentario" -H "Content-Type: application/json" -d '{"id_usuario": "x", "id_tarea": "abc", "texto_comentario": "c", "fecha_hora": "2026-01-01 00:00:00"}'
```

### 2.Numero a String Tipo: Numero a String

#### IT-46 · addUsuario nombre=12345
- **Método:** `POST`
- **URL:** `http://localhost:8082/addUsuario`
- **Body:** `{"nombre": 12345, "correo_electronico": 99999, "contrasena": 123, "rol": 1, "imagen_usuario": 2}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addUsuario" -H "Content-Type: application/json" -d '{"nombre": 12345, "correo_electronico": 99999, "contrasena": 123, "rol": 1, "imagen_usuario": 2}'
```

#### IT-47 · addProyecto nombre=12345
- **Método:** `POST`
- **URL:** `http://localhost:8082/addProyecto`
- **Body:** `{"nombre": 12345, "descripcion": 999, "fecha_inicio": "2026-01-01", "fecha_finalizacion": "2026-01-02", "estado_proyecto": 1, "fondo": 2}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addProyecto" -H "Content-Type: application/json" -d '{"nombre": 12345, "descripcion": 999, "fecha_inicio": "2026-01-01", "fecha_finalizacion": "2026-01-02", "estado_proyecto": 1, "fondo": 2}'
```

### 2.Fecha Inválida Tipo: Fecha Inválida

#### IT-48 · addProyecto fecha invalida
- **Método:** `POST`
- **URL:** `http://localhost:8082/addProyecto`
- **Body:** `{"nombre": "FechaBad", "descripcion": "d", "fecha_inicio": "texto_invalido", "fecha_finalizacion": "2026-01-01", "estado_proyecto": "Estado", "fondo": "#fff"}`
- **Respuesta observada:** `HTTP 500` → `{"timestamp":"2026-10-13T04:36:03.922+00:00","status":500,"error":"Internal Server Error","path":"/addProyecto"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addProyecto" -H "Content-Type: application/json" -d '{"nombre": "FechaBad", "descripcion": "d", "fecha_inicio": "texto_invalido", "fecha_finalizacion": "2026-01-01", "estado_proyecto": "Estado", "fondo": "#fff"}'
```

#### IT-49 · addTareas fecha invalida
- **Método:** `POST`
- **URL:** `http://localhost:8082/addTareas`
- **Body:** `{"nombre": "t", "descripcion": "d", "fecha_vencimiento": "texto_invalido", "lista": "l", "estado": "e", "id_proyecto": 204}`
- **Respuesta observada:** `HTTP 500` → `{"timestamp":"2026-10-13T04:36:03.931+00:00","status":500,"error":"Internal Server Error","path":"/addTareas"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addTareas" -H "Content-Type: application/json" -d '{"nombre": "t", "descripcion": "d", "fecha_vencimiento": "texto_invalido", "lista": "l", "estado": "e", "id_proyecto": 204}'
```

#### IT-50 · addComentario fecha invalida
- **Método:** `POST`
- **URL:** `http://localhost:8082/addComentario`
- **Body:** `{"id_usuario": "x", "id_tarea": 205, "texto_comentario": "c", "fecha_hora": "texto_invalido"}`
- **Respuesta observada:** `HTTP 500` → `{"timestamp":"2026-10-13T04:36:03.938+00:00","status":500,"error":"Internal Server Error","path":"/addComentario"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addComentario" -H "Content-Type: application/json" -d '{"id_usuario": "x", "id_tarea": 205, "texto_comentario": "c", "fecha_hora": "texto_invalido"}'
```

#### IT-51 · addAsignacion fecha invalida
- **Método:** `POST`
- **URL:** `http://localhost:8082/addAsignacion`
- **Body:** `{"id_usuario": 203, "id_tarea": 205, "fecha_asignacion": "texto_invalido"}`
- **Respuesta observada:** `HTTP 500` → `{"timestamp":"2026-10-13T04:36:03.944+00:00","status":500,"error":"Internal Server Error","path":"/addAsignacion"}`
- **cURL:**
```bash
curl -X POST "http://localhost:8082/addAsignacion" -H "Content-Type: application/json" -d '{"id_usuario": 203, "id_tarea": 205, "fecha_asignacion": "texto_invalido"}'
```

### 2.ID Inexistente Tipo: ID Inexistente

#### IT-52 · buscarUsuario id=-1
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarUsuario?id=-1`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_usuario":null,"nombre":null,"correo_electronico":null,"contrasena":null,"rol":null,"imagen_usuario":null}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarUsuario?id=-1"
```

#### IT-53 · buscarUsuario id=999999
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarUsuario?id=999999`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_usuario":null,"nombre":null,"correo_electronico":null,"contrasena":null,"rol":null,"imagen_usuario":null}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarUsuario?id=999999"
```

#### IT-54 · buscarProyecto id=999999
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarProyecto?id_proyecto=999999`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_proyecto":null,"nombre":null,"descripcion":null,"fecha_inicio":null,"fecha_finalizacion":null,"estado_proyecto":null,"fondo":null}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarProyecto?id_proyecto=999999"
```

#### IT-55 · buscarTarea id=-1
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarTarea?id_tarea=-1`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_tarea":null,"nombre":null,"descripcion":null,"fecha_vencimiento":null,"lista":null,"estado":null,"id_proyecto":null}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarTarea?id_tarea=-1"
```

#### IT-56 · buscarComentario id=999999
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarComentario?id=999999`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_comentario":null,"id_usuario":null,"id_tarea":null,"texto_comentario":null,"fecha_hora":null}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarComentario?id=999999"
```

#### IT-57 · buscarAsignacion id=-1
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarAsignacion?id=-1`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_asignacion":null,"id_usuario":null,"id_tarea":null,"fecha_asignacion":null}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarAsignacion?id=-1"
```

#### IT-58 · buscarAsignacion id=999999
- **Método:** `GET`
- **URL:** `http://localhost:8082/buscarAsignacion?id=999999`
- **Body:** _no aplica (query param)_
- **Respuesta observada:** `HTTP 200` → `{"id_asignacion":null,"id_usuario":null,"id_tarea":null,"fecha_asignacion":null}`
- **cURL:**
```bash
curl "http://localhost:8082/buscarAsignacion?id=999999"
```

#### IT-59 · deleteUsuario id=999999
- **Método:** `DELETE`
- **URL:** `http://localhost:8082/deleteUsuario`
- **Body:** `{"id_usuario": 999999}`
- **Respuesta observada:** `HTTP 200` → `1`
- **cURL:**
```bash
curl -X DELETE "http://localhost:8082/deleteUsuario" -H "Content-Type: application/json" -d '{"id_usuario": 999999}'
```
