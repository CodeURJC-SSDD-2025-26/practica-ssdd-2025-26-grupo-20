@echo off
REM =========================================================
REM RODRI — Docker + OpenAPI + Documentación
REM =========================================================
REM Publica las imágenes en DockerHub.
REM Requisitos:
REM   - Haber ejecutado create_image.bat antes
REM   - Haber hecho login en DockerHub: docker login
REM
REM Uso: docker\publish_image.bat
REM =========================================================

REM TODO: docker push TU_USUARIO/app-service:latest
REM TODO: docker push TU_USUARIO/utility-service:latest