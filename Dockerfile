# --- ETAPA 1: COMPILACIÓN ---
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Creamos el JAR
RUN gradle shadowJar --no-daemon -x test

# --- ETAPA 2: EJECUCIÓN ---
FROM eclipse-temurin:21-jre-alpine

# CORRECCIÓN AQUÍ:
# En vez de 'mkdir', usamos WORKDIR.
# Esto crea la carpeta /app y nos mueve dentro de ella automáticamente.
WORKDIR /app

# Copiamos el JAR dentro de la carpeta actual (/app)
COPY --from=build /home/gradle/src/build/libs/*.jar server.jar

# Configuración de puerto
ENV PORT=8080
EXPOSE 8080

# Arrancamos
ENTRYPOINT ["java", "-jar", "server.jar"]