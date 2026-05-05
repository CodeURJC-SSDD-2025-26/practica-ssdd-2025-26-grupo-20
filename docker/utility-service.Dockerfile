# =========================================================
# RODRI
# =========================================================
# Dockerfile multi-stage para utility-service.
# Igual que app-service.Dockerfile pero más sencillo:
# este servicio no tiene base de datos ni SSL.
#
# Puerto: 8080 (HTTP) — ver utility-service/application.properties
# artifactId del proyecto: utilityservice (ver pom.xml)
# Clase principal: es.urjc.utilityservice.UtilityserviceApplication
# =========================================================

# -- Stage 1: compilar --
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# NOTA: Al ejecutar el script create_image.bat desde la raíz (.), 
# indicamos que coja los archivos dentro de la carpeta utility-service/
COPY utility-service/pom.xml .
RUN mvn dependency:go-offline

COPY utility-service/src -/src
RUN mvn package -DskipTests

# -- Stage 2: ejecutar --
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# Variables de entorno que acepta este servicio:
#   MAIL_USERNAME
#   MAIL_PASSWORD