# Documento de Migración: Java 11 → Java 21 + Spring Boot 2.7.17 → 3.5.0

**Proyecto:** `Ejercicio5MyAPIGestor` (API REST de gestión de proyectos)
**Fecha:** 21 de octubre de 2026
**Alcance:** Actualización de plataforma Java/Spring + integración DevSecOps (JaCoCo, OWASP, SonarQube, Semgrep, Jenkins).

---

## 1. Resumen de cambios

| Componente | Antes | Después |
|---|---|---|
| Java | 11 | **21 LTS** |
| Spring Boot (parent) | 2.7.17 | **3.5.0** |
| Namespace JPA/validación | `javax.persistence.*` | **`jakarta.persistence.*`** (Jakarta EE 10) |
| Hibernate | 5.6 (integrado en SB 2.7) | **6.6** (integrado en SB 3.5) |
| Generación de IDs | `GenerationType.AUTO` | **`GenerationType.IDENTITY`** |
| Maven Wrapper | (versión anterior) | **Apache Maven 3.9.9** |
| maven-surefire-plugin | (implícito) | **Explícito en el pom** (hereda 3.5.3 del parent, compatible Java 21) |
| jacoco-maven-plugin | No existía | **0.8.12** (`prepare-agent` + `report`) |
| OWASP dependency-check | No existía | **12.1.0** (HTML + JSON, `failBuildOnCVSS=11`) |
| sonar-maven-plugin | No existía | **5.5.0.6356** |
| Pruebas | 1 test `@SpringBootTest` que exigía MariaDB | **35 tests autónomos** (H2 en memoria + Mockito) |

---

## 2. Cambios detallados en el `pom.xml`

### 2.1 Versión de plataforma
```xml
<!-- ANTES -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.17</version>
</parent>
<properties>
    <java.version>11</java.version>
</properties>

<!-- DESPUÉS -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.0</version>
</parent>
<properties>
    <java.version>21</java.version>
</properties>
```

### 2.2 Propiedades DevSecOps agregadas
```xml
<properties>
    <java.version>21</java.version>
    <jacoco.version>0.8.12</jacoco.version>
    <owasp.version>12.1.0</owasp.version>
    <sonar.version>5.5.0.6356</sonar.version>
    <sonar.coverage.jacoco.xmlReportPaths>${project.build.directory}/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
    <sonar.dependencyCheck.jsonReportPath>${project.build.directory}/dependency-check-report.json</sonar.dependencyCheck.jsonReportPath>
    <sonar.dependencyCheck.htmlReportPath>${project.build.directory}/dependency-check-report.html</sonar.dependencyCheck.htmlReportPath>
</properties>
```

### 2.3 Dependencia de pruebas H2 (autonomía de los tests)
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```
Los tests ya **no dependen de MariaDB encendida**: se ejecutan contra H2 en memoria (modo MySQL) y mocks de repositorios.

### 2.4 Plugins de build agregados
- **maven-surefire-plugin**: declarado explícitamente; hereda la versión 3.5.3 del parent de Spring Boot, compatible con la ejecución deJUnit 5 sobre Java 21.
- **jacoco-maven-plugin 0.8.12**: ejecuciones `prepare-agent` (instrumenta el JVM durante los tests) y `report` (fase `verify`), generando `target/site/jacoco/jacoco.xml` e `index.html` para SonarQube.
- **dependency-check-maven 12.1.0 (OWASP)**: genera reportes HTML y JSON de vulnerabilidades en dependencias; `failBuildOnCVSS=11` para no bloquear el pipeline por hallazgos moderados.
- **sonar-maven-plugin 5.5.0.6356**: permite enviar el análisis de calidad y cobertura a SonarQube con `./mvnw sonar:sonar`.

---

## 3. Cambios en el código fuente (Jakarta EE 10 / Hibernate 6)

### 3.1 Imports migrados (5 entidades)
Reemplazo mecánico en `src/main/java/com/mx/cbtis/gestor/modelo/`:
- `javax.persistence.*` → `jakarta.persistence.*`

Archivos afectados: `Asignaciones.java`, `Comentarios.java`, `Proyectos.java`, `Tareas.java`, `Usuario.java`.

> Nota: no existían imports `javax.validation.*` ni `javax.servlet.*` en el proyecto, por lo que solo fue necesaria la migración de persistencia.

### 3.2 Cambio de estrategia de generación de IDs
```java
// ANTES (Hibernate 5: producía auto_increment en MariaDB)
@GeneratedValue(strategy = GenerationType.AUTO)

// DESPUÉS (Hibernate 6: AUTO produciría secuencias; se fija IDENTITY
// para conservar el auto_increment del esquema MariaDB existente)
@GeneratedValue(strategy = GenerationType.IDENTITY)
```
**Por qué:** con Hibernate 6, `AUTO` en bases de datos que soportan secuencias (MariaDB 10.3+) genera una tabla `hibernate_sequence` en lugar de usar `AUTO_INCREMENT`, rompiendo la compatibilidad con el esquema existente. `IDENTITY` preserva el comportamiento original.

### 3.3 `application.properties`
No se requirieron cambios de migración: no existe propiedad de dialecto (p. ej. `MariaDB103Dialect`), por lo que **Hibernate 6 detecta automáticamente** el dialecto MariaDB desde el driver JDBC. Se conservan la URL, credenciales y `ddl-auto=update` originales.

---

## 4. Cambios en pruebas (`src/test`)

| Antes | Después |
|---|---|
| `Ejercicio5MyApiGestorApplicationTests` (`@SpringBootTest`) que fallaba sin MariaDB encendida | Se conserva como smoke test, pero ejecutándose sobre **H2 en memoria** (`src/test/resources/application.properties`) |
| Sin pruebas de controladores | **5 suites `@WebMvcTest`** con `@MockitoBean` (reemplaza al deprecado `@MockBean` en Spring Boot 3.4+): `UsuarioControllerTest`, `ProyectosControllerTest`, `TareasControllerTest`, `ComentariosControllerTest`, `AsignacionesControllerTest` |

**Resultado:** `./mvnw clean test` → **35 tests, 0 fallos (BUILD SUCCESS)**, sin necesidad de base de datos externa.

**Cobertura JaCoCo (líneas): 92.6% global** (251/271 líneas); los 5 controladores quedan cubiertos al 100%.

---

## 5. Herramientas DevSecOps integradas

### 5.1 Jenkins (`Jenkinsfile`)
Pipeline declarativo con etapas: Checkout → Build+Tests+Cobertura (JaCoCo) → OWASP SCA → SonarQube → Semgrep SAST → Package. **Configurado para usar JDK 21** y Maven 3.9.9 (wrapper), garantizando la compilación en Java 21.

### 5.2 Contenedores (`Dockerfile.jenkins` + `docker-compose.yml`)
- `Dockerfile.jenkins`: imagen base `jenkins/jenkins:lts-jdk21` con Maven y Semgrep instalados.
- `docker-compose.yml`: levanta **Jenkins (JDK 21)** y **SonarQube** en la misma red Docker (`devsecops-net`).

### 5.3 Semgrep (SAST)
```bash
semgrep scan --config="p/java" --config="p/spring" --json -o semgrep-report.json .
```

---

## 6. Evidencia de compilación y pruebas

```
$ ./mvnw clean package
...
[INFO] Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Reportes generados:
- `target/site/jacoco/jacoco.xml` / `target/site/jacoco/index.html` — cobertura de código.
- `target/dependency-check-report.html` / `.json` — vulnerabilidades de dependencias (en CI).

Los archivos `evidencia/pom-original.xml` y `evidencia/pom-actualizado.xml` contienen el estado previo y posterior del `pom.xml`.
