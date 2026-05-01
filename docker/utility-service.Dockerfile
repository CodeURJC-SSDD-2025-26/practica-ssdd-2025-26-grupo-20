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
# TODO: igual que app-service.Dockerfile

# -- Stage 2: ejecutar --
# TODO: igual que app-service.Dockerfile pero puerto 8080
#
# Variables de entorno que acepta este servicio:
#   MAIL_USERNAME
#   MAIL_PASSWORD