# 🚀 E-Commerce Prices Microservice

Este microservicio gestiona la consulta de tarifas de productos con una arquitectura de alta disponibilidad, diseñada bajo el estándar de **Arquitectura Hexagonal** y preparada para entornos de **concurrencia masiva** y resiliencia extrema.

---

## 🛠️ Stack Tecnológico (Update 2026)

*   **Core:** Java 25 (LTS) + Spring Boot 4.0.1.
*   **Concurrencia:** Java Virtual Threads habilitados nativamente.
*   **Arquitectura:** Hexagonal (Ports & Adapters) con modelos de dominio inmutables (**Java Records**).
*   **Persistencia:** PostgreSQL 16 (Prod) / H2 2.4 (Local/Test).
*   **Migraciones:** Flyway para el control de versiones de base de datos y consistencia de esquemas.
*   **Seguridad:** Spring Security + JWT (Stateless) + OpenAPI 3.1.
*   **Resiliencia:** Resilience4j (Circuit Breaker, Semaphore Bulkhead, Time Limiter).
*   **Observabilidad:** Micrometer + Prometheus + Actuator con soporte para métricas de hilos virtuales.
*   **Contenedores:** Docker (Multi-stage builds).

---

### 1. Concurrencia de Próxima Generación
*   **Virtual Threads (Loom):** El servidor utiliza hilos virtuales de Java 25, permitiendo que cada petición se procese de forma síncrona y legible sin bloquear hilos del sistema operativo.
*   **Always-Valid Domain:** Uso de **Java Records** con constructores compactos para asegurar que las reglas de negocio se validen en el momento de la creación del objeto.
*   **Standard Error Handling:** Implementación del estándar **RFC 9457 (Problem Details)** para respuestas de error ricas y consistentes.

### 2. Resiliencia y Protección de Infraestructura
Optimizado con **Resilience4j** para proteger los recursos físicos limitados:
*   **Circuit Breaker:** Detecta inestabilidad en la persistencia y abre el circuito para proteger el sistema.
*   **Bulkhead (Semaphore): el aislamiento se realiza mediante semáforos lógicos. Esto limita el acceso concurrente al pool de conexiones de la base de datos, evitando la saturación de **HikariCP**.
*   **Time Limiter:** Límite estricto de **2 segundos** por consulta.
*   **Graceful Shutdown:** Configurado para finalizar transacciones activas (15s) en entornos de orquestación (K8s).

### 3. Seguridad y API Design
*   **OAuth2 Resource Server:** Validación de tokens JWT. Los tests de integración utilizan `.with(jwt())` para simular contextos de seguridad.
*   **OpenAPI 3.1:** Documentación autogenerada y enriquecida con esquemas detallados y ejemplos reales.

### 4. Observabilidad y Monitoreo
*   **Métricas de Negocio:** Contador personalizado para monitorizar la demanda real de consultas.
*   **Health Checks:** Probes de `liveness` y `readiness` para auto-reparación en K8s.
*   **Prometheus Scraping:** Exposición de métricas técnicas (JVM, Latencia P99, estado del Circuit Breaker).

---

## 🚦 Ejecución en Local (H2)

Entorno ligero para desarrollo rápido. Usa base de datos en memoria.

1.  **Compilar y Testear:**
    ```bash
    ./mvnw clean test
    ```
2.  **Arrancar Aplicación:**
    ```bash
    ./mvnw spring-boot:run "-Dspring-boot.run.profiles=local"
    ```
3.  **Probar API (Postman):**
    *   **URL:** `GET http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1`
    *   **Auth:** Bearer Token (JWT firmado con clave de desarrollo).

---

## 🚦 Ejecución en Producción (PostgreSQL)

Simulación del entorno empresarial real con persistencia persistente.

### 1. Levantar Infraestructura (Docker)
```bash
docker run -d --name postgres-db \
  -p 5432:5432 \
  -e POSTGRES_DB=prices_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:15-alpine
   ```
### Pasos
1.  **Compilar y Testear:**
    ```bash
    ./mvnw clean test
    ```
2.  **Arrancar Aplicación:**
    ```bash
    ./mvnw spring-boot:run "-Dspring-boot.run.profiles=prod"

---

## 📈 Monitorización
Acceso al endpoint de métricas (Requiere Auth):
`http://localhost:8080/actuator/prometheus`

---

### 🚢 Despliegue (CI/CD)
El proyecto incluye un pipeline de **GitHub Actions** que automatiza:
1.  Validación de tests unitarios y de integración.
2.  Construcción de imagen Docker (Multi-stage).
3.  Publicación en **GitHub Container Registry**.