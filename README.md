# 🏷️ Ecommerce Price Service

Servicio REST construido con **Spring Boot 3** y **Arquitectura Hexagonal** para la gestión de tarifas de precios con lógica de desambiguación por prioridad.

## 🚀 Tecnologías y Herramientas
- **Java 17** / **Spring Boot 4.0.1**
- **Arquitectura Hexagonal** (Puertos y Adaptadores)
- **H2 Database** (Base de datos en memoria)
- **Spring Data JPA** & **Hibernate**
- **JUnit 5** & **MockMvc** (Tests de integración)
- **OpenAPI 3 / Swagger** (Documentación de API)
- **Lombok** (Solo en la capa de infraestructura)

## 🏗️ Arquitectura
El proyecto sigue los principios de la Arquitectura Hexagonal:
- **Domain**: Modelos de negocio inmutables y lógica pura sin dependencias externas.
- **Application**: Casos de uso y definición de puertos de entrada/salida.
- **Infrastructure**: Implementaciones técnicas (Adaptadores REST, JPA, Mappers y Configuración).

## 📊 Base de Datos
Se utiliza una base de datos H2 llamada `prices_db`.
- El esquema se define en `schema.sql`.
- Los datos de prueba se cargan desde `data.sql`.
- Acceso a consola: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    - **JDBC URL**: `jdbc:h2:mem:prices_db`

## 🧪 Ejecución de Tests
El proyecto incluye una suite de tests de integración que validan los 5 escenarios solicitados en la prueba técnica:
1. Petición a las 10:00 del día 14 (Producto 35455, Brand 1).
2. Petición a las 16:00 del día 14 (Validación de prioridad mayor).
3. Petición a las 21:00 del día 14.
4. Petición a las 10:00 del día 15.
5. Petición a las 21:00 del día 16.

Para ejecutar los tests:
```bash
./mvnw test