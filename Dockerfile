# Usamos una imagen con JDK 17 y Maven para construir el proyecto
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# 1. Copiamos el archivo de configuración de Maven
COPY pom.xml .

# 2. Descargamos las dependencias (se guardan en la caché de Docker)
RUN mvn dependency:go-offline -B

# 3. Copiamos el código fuente y compilamos el archivo JAR
COPY src ./src
RUN mvn clean package -DskipTests -Pprod

# ==========================================
# ETAPA 2: Ejecución (Runtime stage)
# ==========================================
# Usamos una imagen ligera de JRE (solo ejecución) para producción
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# SEGURIDAD: Creamos un usuario de sistema para no ejecutar como root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiamos el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Configuración de rendimiento de la JVM para 2026
# - MaxRAMPercentage: Adapta el uso de RAM al límite asignado por Docker/K8s
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
            "-Dspring.profiles.active=prod", \
            "-jar", "app.jar"]

# Exponemos el puerto del microservicio
EXPOSE 8080