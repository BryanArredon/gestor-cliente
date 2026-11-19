# Contexto y Guía de Despliegue para Agente / Servidor (`admin-proyectos`)

> **Propósito de este documento:** Proveer el contexto completo de arquitectura, versionado de imágenes OCI, despliegue en infraestructura `~/infra` (Compose + Traefik + Cloudflare Tunnel + PostgreSQL) y observabilidad/gestión visual de contenedores para que otra IA o ingeniero ejecute el despliegue de punta a punta en el servidor.

---

## 1. Contexto de la Aplicación

El sistema **Administrador de Proyectos (`admin-proyectos`)** consta de dos microservicios Java 21 / Spring Boot 3.5.0 ya preparados para contenedorización y producción:

1. **Backend (`gestor-api` — carpeta `Ejercicio5MyAPIGestor`)**
   - **Rol:** API REST con Spring Data JPA + Actuator + CORS configurable.
   - **Puerto interno:** `8082`.
   - **Healthcheck:** `GET /actuator/health` (`wget -qO- http://127.0.0.1:8082/actuator/health >/dev/null`).
   - **Base de datos:** Compatible con **PostgreSQL** (`org.postgresql.Driver`, Hibernate `ddl-auto=update` crea las tablas automáticamente al arrancar).
   - **Exposición:** **Microservicio interno** (sin `[[spec.hosts]]` en `app.toml`). Traefik no lo expone al público; solo es alcanzable dentro de la red interna Docker (`edge`/`data`) como `http://gestor-api:8082`.
   - **CORS:** Controlado por la variable de entorno `CORS_ALLOWED_ORIGINS` (ejemplo: `https://gestor.bryandevs.com`).

2. **Frontend (`gestor-web` — carpeta `Ejercicio5Cliente`)**
   - **Rol:** Aplicación web SSR (Thymeleaf + Bootstrap + jQuery AJAX). Todas las llamadas AJAX del navegador usan rutas relativas al mismo origen (`/consultarProyectos`, `/obtenerTareas`, etc.) y el servidor actúa como BFF/proxy hacia el backend vía `RestTemplate` (sin problemas de CORS en el navegador).
   - **Puerto interno:** `8083`.
   - **Healthcheck:** `GET /actuator/health` (`wget -qO- http://127.0.0.1:8083/actuator/health >/dev/null`).
   - **Proxy / TLS:** Configurado con `server.forward-headers-strategy=framework` para respetar `X-Forwarded-Proto` y `X-Forwarded-Host` detrás de Cloudflare Tunnel + Traefik.
   - **Exposición:** Público vía Traefik + Cloudflare Tunnel en `gestor.bryandevs.com` (o el subdominio elegido), apuntando al backend por `API_BASE_URL=http://gestor-api:8082`.

---

## 2. Convención de Nombres, Tags y Versionado de Imágenes OCI

**Regla estricta:** Nunca usar `:latest`. Cada imagen debe llevar **versión semántica + hash corto del commit** (o etiqueta dual) para trazabilidad y rollback inmediato.

- **Registro:** Docker Hub (`bryanarrendon/adminbryandev`)
- **Imágenes ya compiladas para `linux/amd64` y subidas (listas para `docker pull` en el servidor):**
  - Backend: `bryanarrendon/adminbryandev:gestor-api-v1.0.0-sha-571e107` (alias `gestor-api-v1.0.0`)
  - Frontend: `bryanarrendon/adminbryandev:gestor-web-v1.0.0-sha-571e107` (alias `gestor-web-v1.0.0`)
- **Formato de tag para futuras versiones:**
  - `gestor-api-v<SEMVER>-sha-<commit_corto>`
  - `gestor-web-v<SEMVER>-sha-<commit_corto>`

### Build y Push de futuras versiones
```bash
VERSION="v1.0.1"
SHA="$(git rev-parse --short HEAD)"

# Backend (gestor-api)
docker buildx build --platform linux/amd64 \
  -t "bryanarrendon/adminbryandev:gestor-api-${VERSION}-sha-${SHA}" \
  -t "bryanarrendon/adminbryandev:gestor-api-${VERSION}" \
  --push ./Ejercicio5MyAPIGestor

# Frontend (gestor-web)
docker buildx build --platform linux/amd64 \
  -t "bryanarrendon/adminbryandev:gestor-web-${VERSION}-sha-${SHA}" \
  -t "bryanarrendon/adminbryandev:gestor-web-${VERSION}" \
  --push ./Ejercicio5Cliente
```

> **Control de versiones / Rollback:** Para actualizar, solo se incrementa `VERSION` (ej. `v1.0.1-sha-...`), se cambia el tag en `app.toml` y se ejecuta `make deploy`. Para rollback, se regresa al tag anterior en `app.toml` y se vuelve a ejecutar `make deploy`.

---

## 3. Tareas a Ejecutar en el Servidor (`~/infra`)

### 3.1 Base de datos en `platform-postgres`
Crear un rol y base de datos dedicados para `gestor-api` (no compartir credenciales con otras apps):
- Generar contraseña aleatoria y guardarla en `~/infra/secrets/gestor_db_password.txt` (permisos `600`).
- Ejecutar en `platform-postgres`:
  - `CREATE ROLE gestor LOGIN PASSWORD '<secret>';`
  - `CREATE DATABASE gestor OWNER gestor;`

### 3.2 Alta de las aplicaciones en `~/infra/apps/`
Existen plantillas base en el repositorio bajo `infra-templates/` que siguen el contrato `app.toml` del renderer (`~/infra/scripts/render.py`):

1. **`gestor-api` (`~/infra/apps/gestor-api/`)**
   - Imagen: `ghcr.io/bryane/gestor-api:<TAG>`
   - `container_port = 8082`
   - `depends_on = ["postgres"]`
   - Sin `[[spec.hosts]]` (servicio interno en redes `edge`/`data`).
   - Variables de entorno:
     - `SERVER_PORT=8082`
     - `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/gestor`
     - `SPRING_DATASOURCE_USERNAME=gestor`
     - `SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver`
     - `CORS_ALLOWED_ORIGINS=https://gestor.bryandevs.com`
   - Secreto (`mode = "env"`, guardado en `~/infra/apps/gestor-api/env` con permisos `600`):
     - `SPRING_DATASOURCE_PASSWORD`

2. **`gestor-web` (`~/infra/apps/gestor-web/`)**
   - Imagen: `ghcr.io/bryane/gestor-web:<TAG>`
   - `container_port = 8083`
   - Variables de entorno:
     - `SERVER_PORT=8083`
     - `API_BASE_URL=http://gestor-api:8082`
   - Host público en Traefik:
     - `[[spec.hosts]] name = "gestor.bryandevs.com" path = "/"`

### 3.3 Render, Validación y Despliegue
Ejecutar en `~/infra`:
1. `make render APP=gestor-api` y validar con `APP=gestor-api scripts/compose.sh config -q`.
2. `make deploy APP=gestor-api` y confirmar estado `healthy` con `make ps` y `make logs APP=gestor-api`.
3. `make render APP=gestor-web` y validar con `APP=gestor-web scripts/compose.sh config -q`.
4. `make deploy APP=gestor-web`.
5. Probar Traefik localmente antes del túnel:
   - `curl -i -H 'Host: gestor.bryandevs.com' http://127.0.0.1:18080/actuator/health` (debe devolver `200 OK`).
6. Si aún no existe wildcard `*.bryandevs.com` en Cloudflare Tunnel (`~/.cloudflared/config.yml`), registrar el hostname `gestor.bryandevs.com` apuntando a `http://localhost:18080`, crear ruta DNS con `cloudflared tunnel route dns` y reiniciar `systemctl --user restart cloudflared`.

---

## 4. Gestión Visual y Observabilidad de Contenedores en el Servidor

**Importante — No duplicar servicios:** La infraestructura actual del servidor ya cuenta con **Prometheus (`19090`)**, **Grafana (`13000`)** y **Traefik Dashboard (`127.0.0.1:18081`)** mediante `make observability`. No se debe levantar otro Prometheus ni otro Grafana por aplicación.

Para cubrir la gestión visual de contenedores (similar a un stack con Dozzle) sin usar SSH desde la Mac:

1. **Dozzle (`amir20/dozzle`) — Visor de logs y consumo en tiempo real:**
   - Integrarlo en el compose de observabilidad/plataforma montando `/var/run/docker.sock:/var/run/docker.sock:ro`.
2. **Portainer CE (`portainer/portainer-ce:lts`) — Gestión visual completa (start/stop/restart/logs/terminal):**
   - Montar `/var/run/docker.sock` y un volumen persistente `portainer_data:/data`.
3. **Formas de acceso desde la Mac (elegir una según preferencia de seguridad):**
   - **Opción A (Solo Tailscale — más simple y privada):** Publicar únicamente en la IP de Tailscale (`100.91.46.42:8888` para Dozzle, `100.91.46.42:9443` para Portainer) o usar Tailscale MagicDNS desde el navegador de la Mac. No se expone nada a internet.
   - **Opción B (Con dominio propio vía Traefik + Cloudflare Tunnel, ej. `dozzle.bryandevs.com` / `portainer.bryandevs.com`):**
     - Enrutar el subdominio en Traefik (`127.0.0.1:18080`) hacia el contenedor correspondiente en la red `edge` y asegurarse de que Cloudflare Tunnel apunte ese hostname (o `*.bryandevs.com`) a `http://localhost:18080`.
     - **Requisito crítico de seguridad:** Como estos contenedores montan `docker.sock`, proteger ambos subdominios con **Cloudflare Access** (Zero Trust, OTP por correo/GitHub) antes de exponerlos en el túnel, y habilitar autenticación en Portainer/Dozzle.
4. **Métricas de las apps Spring Boot hacia el Prometheus existente (Opcional):**
   - Si se desea graficar JVM, memoria y peticiones HTTP de `gestor-api` y `gestor-web` en el Grafana existente (`13000`), agregar `io.micrometer:micrometer-registry-prometheus` al `pom.xml`, exponer `prometheus` en `management.endpoints.web.exposure.include` y añadir los targets `gestor-api:8082` y `gestor-web:8083` (`/actuator/prometheus`) en la configuración del Prometheus de `~/infra/config/`.

---

## 5. Notas de Seguridad para Producción Pública

- El módulo original de autenticación de `admin-proyectos` compara contraseñas en texto plano contra la tabla `usuario` y no incluye filtro de sesión obligatorio en todas las rutas del frontend.
- Si el dominio `gestor.bryandevs.com` va a quedar abierto al público general, se recomienda activar **Cloudflare Access** (Zero Trust) delante del subdominio o implementar **Spring Security** con hash BCrypt antes de abrir registros.

