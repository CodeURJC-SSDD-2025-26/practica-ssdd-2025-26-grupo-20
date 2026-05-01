@echo off
REM =========================================================
REM RODRI — Docker + OpenAPI + Documentación
REM =========================================================
REM Publica el docker-compose.yml en DockerHub como OCI Artifact.
REM Esto permite que cualquiera ejecute la aplicación completa
REM con un solo comando sin necesitar el código fuente.
REM
REM Requisitos:
REM   - Docker instalado
REM   - Haber hecho login: docker login
REM
REM El comando para publicarlo es:
REM   docker compose push  (si usas el registry de compose)
REM O usando ORAS (OCI Registry As Storage):
REM   oras push TU_USUARIO/docker-compose:latest docker-compose.yml
REM
REM El comando para que cualquiera lo descargue y ejecute sería:
REM   docker compose -f oci://TU_USUARIO/docker-compose:latest up
REM =========================================================

REM TODO: implementar la publicación del docker-compose como OCI Artifact