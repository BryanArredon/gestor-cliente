# Documento de Migración: Java 17 → Java 21 + Spring Boot 3.2.5 → 3.5.0

**Proyecto:** `Ejercicio5Cliente` (Frontend Thymeleaf del Gestor de Proyectos)
**Fecha:** 21 de octubre de 2026
**Alcance:** Actualización de plataforma Java/Spring + integración DevSecOps (JaCoCo, OWASP, SonarQube, Semgrep, Jenkins).

> Microservicio backend asociado: `Ejercicio5MyAPIGestor` (migrado previamente a Java 21 + Spring Boot 3.5.0).

---

## 1. Resumen de cambios

| Componente | Antes | Después |
|---|---|---|
| Java | 17 | **21 LTS** |
| Spring Boot (parent) | 3.2.5 | **3.5.0** |
| Maven Wrapper | (versión anterior) | **Apache Maven 3.9.9** |
| URL del backend | Hardcodeada `http://localhost:8081` (19 lugares) | **Parametrizada** con `@Value("${api.base-url:...}")` desde variable de entorno `API_BASE_URL` |
| Puerto del frontend | (default 8080) | **Parametrizado** con `SERVER_PORT` (default 8083) |
| maven-surefire-plugin | (implícito) | **Explícito en el pom** (hereda 3.5.3 del parent, compatible Java 21) |
| jacoco-maven-plugin | No existía | **0.8.12** (`prepare-agent` + `report`) |
| OWASP dependency-check | No existía | **12.1.0** (HTML + JSON, `failBuildOnCVSS=11`) |
| sonar-maven-plugin | No existía | **5.5.0.6356** |
| Pruebas | 1 test `contextLoads` | **8 tests** (contextLoads + 7 pruebas de vistas del controlador) |

**Nota sobre Jakarta EE:** al ser un frontend Thymeleaf **sin JPA ni servlet API directa**, el proyecto no contenía imports `javax.*`, por lo que la migración de namespace Jakarta EE no fue necesaria.

---

## 2. Cambios detallados en el `pom.xml`

### 2.1 Versión de plataforma
```xml
<!-- ANTES -->
<parent>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
</parent>
<properties>
    <java.version>17</java.version>
</properties>

<!-- DESPUÉS -->
<parent>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.0</version>
</parent>
<properties>
    <java.version>21</java.version>
</properties>
```

### 2.2 Propiedades DevSecOps agregadas
```xml
<jacoco.version>0.8.12</jacoco.version>
<owasp.version>12.1.0</owasp.version>
<sonar.version>5.5.0.6356</sonar.version>
<sonar.coverage.jacoco.xmlReportPaths>${project.build.directory}/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
<sonar.dependencyCheck.jsonReportPath>${project.build.directory}/dependency-check-report.json</sonar.dependencyCheck.jsonReportPath>
<sonar.dependencyCheck.htmlReportPath>${project.build.directory}/dependency-check-report.html</sonar.dependencyCheck.htmlReportPath>
```

### 2.3 Plugins de build agregados
- **maven-surefire-plugin**: versión 3.5.3 heredada del parent, compatible con Java 21 y JUnit 5.
- **jacoco-maven-plugin 0.8.12**: `prepare-agent` + `report` (fase `verify`).
- **dependency-check-maven 12.1.0 (OWASP)**: reportes HTML/JSON de vulnerabilidades en dependencias.
- **sonar-maven-plugin 5.5.0.6356**: análisis de calidad y cobertura hacia SonarQube.

---

## 3. Cambios en el código fuente

### 3.1 `GestorController.java` — URL del backend parametrizada
```java
// ANTES: URL hardcodeada en 19 puntos del controlador
String urlservicebd = "http://localhost:8081/addUsuario";

// DESPUÉS: única fuente de verdad configurable por entorno
@Value("${api.base-url:http://localhost:8082}")
private String apiBaseUrl;
...
String urlservicebd = apiBaseUrl + "/addUsuario";
```
**Por qué:** elimina la duplicación de la URL y permite apuntar a distintos entornos (local, Docker, producción) sin recompilar, usando la variable `API_BASE_URL`.

### 3.2 `application.properties`
```properties
spring.application.name=Ejercicio5Cliente
server.port=${SERVER_PORT:8083}
api.base-url=${API_BASE_URL:http://localhost:8082}
```
El frontend corre por defecto en el puerto **8083** y consumirá al backend en **8082** (puerto configurado en el `.env` de `Ejercicio5MyAPIGestor`).

### 3.3 Gestión de credenciales/entorno con `.env`
- `.env` (local, **ignorado por Git**): valores reales.
- `.env.example` (subido): plantilla para el equipo.
- `.gitignore`: excluye `.env`, `target/`, reportes e IDE.

---

## 4. Cambios en pruebas (`src/test`)

| Test | Qué valida |
|---|---|
| `Ejercicio5ClienteApplicationTests` (existente) | Smoke test: el contexto Spring carga sin errores |
| `GestorControllerTest` (nuevo, `@WebMvcTest`) | Las 7 vistas que no requieren backend: `/`, `/tutorial`, `/manual`, `/login`, `/register`, `/inicio`, `/tarea` — verifica status 200, nombre de vista y atributos del modelo |

Las pruebas de los endpoints que consumen el backend (`RestTemplate` → `localhost:8082`) quedan cubiertas por las pruebas de integración del backend `Ejercicio5MyAPIGestor`; las unitarias del cliente son autónomas y no requieren servicios externos.

---

## 5. Herramientas DevSecOps integradas

Iguales a las del backend (mismo estándar del curso):
- **`Jenkinsfile`**: pipeline declarativo con `jdk 'JDK-21'` y `maven 'Maven-3.9.9'`; etapas: Checkout → Build+Tests+JaCoCo → OWASP → SonarQube → Semgrep → Package.
- **`Dockerfile.jenkins`**: base `jenkins/jenkins:lts-jdk21` con Maven 3.9.9 y Semgrep.
- **`docker-compose.yml`**: Jenkins + SonarQube en la red `devsecops-net`.

---

## 6. Evidencia de compilación y pruebas

```
$ ./mvnw clean package
...
[INFO] BUILD SUCCESS
```

Reportes generados en CI/verify:
- `target/site/jacoco/jacoco.xml` / `index.html` — cobertura.
- `target/dependency-check-report.html` / `.json` — vulnerabilidades.

Los archivos `evidencia/pom-original.xml` y `evidencia/pom-actualizado.xml` contienen el estado previo (SB 3.2.5 / Java 17) y posterior (SB 3.5.0 / Java 21) del `pom.xml`.
