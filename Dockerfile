# 1. ETAPA DE CONSTRUCCIÓN
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Creamos el JAR (saltando tests para ir rápido, ya que los pasamos antes)
RUN gradle shadowJar --no-daemon -x test

# 2. ETAPA DE EJECUCIÓN
FROM eclipse-temurin:21-jre-alpine
mkdir /app
# COPIAMOS el JAR generado. El asterisco *.jar coge cualquiera que haya.
COPY --from=build /home/gradle/src/build/libs/*.jar /app/server.jar

# Configuración vital para Render
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/server.jar"]