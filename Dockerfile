# ==========================================
# ETAPA 1: Construcción (Build stage)
# ==========================================
# Usamos Java 25 y Maven 3.9 para la compilación
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# 1. Cachear dependencias: Copiamos pom.xml primero
COPY pom.xml .

# 2. Descargar dependencias (optimiza la caché de capas de Docker)
RUN mvn dependency:go-offline -B

# 3. Compilar el proyecto con el perfil de producción
COPY src ./src
RUN mvn clean package -DskipTests -Pprod

# ==========================================
# ETAPA 2: Ejecución (Runtime stage)
# ==========================================
# Usamos el JRE 25 ligero para reducir el tamaño de la imagen y la superficie de ataque
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# SEGURIDAD: Usuario no-root siguiendo el principio de menor privilegio
RUN addgroup -S pricesgroup && adduser -S pricesuser -G pricesgroup
USER pricesuser:pricesgroup

# Copiamos el JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# - MaxRAMPercentage: Optimizado para contenedores en K8s.
# - Virtual Threads: Java 25 gestiona nativamente la escalabilidad.
# - ZGC: Usamos Generational ZGC (estándar en Java 25) para latencias ultra-bajas (<1ms).
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:+UseZGC", \
            "-XX:+ZGenerational", \
            "-Dspring.profiles.active=prod", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "app.jar"]

# Exponemos el puerto estándar del microservicio
EXPOSE 8080