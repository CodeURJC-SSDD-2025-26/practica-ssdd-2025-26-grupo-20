# =========================================================
# RODRI
# =========================================================
# Dockerfile multi-stage para app-service.
# "Multi-stage" significa que usamos dos imágenes:
#   1. Una imagen con Maven para COMPILAR el proyecto
#   2. Una imagen solo con Java para EJECUTAR el jar
# Así el resultado final no tiene Maven ni el código fuente,
# solo el jar. Esto cumple rúbrica #28 (no asumir JDK/Maven
# instalado en la máquina).
#
# IMPORTANTE: el keystore.p12 está dentro del jar porque está
# en src/main/resources, así que no necesitas copiarlo aparte.
#
# Puerto: 8443 (HTTPS) — ver application.properties
# =========================================================

# -- Stage 1: compilar --
# TODO: usa la imagen maven:3.9-eclipse-temurin-21 como base
# TODO: establece /app como directorio de trabajo (WORKDIR)
# TODO: copia primero solo el pom.xml y descarga dependencias
#       (esto aprovecha la caché de Docker)
# TODO: copia el resto del código fuente
# TODO: ejecuta mvn clean package -DskipTests para generar el jar

# -- Stage 2: ejecutar --
# TODO: usa la imagen eclipse-temurin:21-jre como base
# TODO: establece /app como directorio de trabajo
# TODO: copia el jar generado en el stage 1
#       (está en /app/target/*.jar)
# TODO: expón el puerto 8443
# TODO: define el ENTRYPOINT para ejecutar el jar:
#       java -jar app.jar
#
# Variables de entorno que acepta este servicio
# (se configuran desde docker-compose.yml):
#   SPRING_DATASOURCE_URL
#   SPRING_DATASOURCE_USERNAME
#   SPRING_DATASOURCE_PASSWORD
#   UTILITY_SERVICE_URL