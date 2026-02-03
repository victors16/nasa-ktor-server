# --- ETAPA 1: BUILD ---
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Damos permisos de ejecución al wrapper (CRUCIAL)
RUN chmod +x gradlew

# Usamos ./gradlew en vez de 'gradle' para usar tu configuración exacta
# y limitamos la memoria explícitamente por si ignora el gradle.properties
RUN ./gradlew shadowJar --no-daemon -x test -Dorg.gradle.jvmargs="-Xmx384m"

# --- ETAPA 2: RUN ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar server.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "server.jar"]