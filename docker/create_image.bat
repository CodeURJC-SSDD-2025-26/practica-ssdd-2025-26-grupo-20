@echo off
REM =========================================================
REM RODRI — Docker + OpenAPI + Documentación
REM =========================================================
REM Script para construir las imágenes Docker de los dos servicios.
REM Requisito: tener Docker instalado y arrancado.
REM NO publica nada en DockerHub (eso lo hace publish_image.bat)
REM
REM Uso: ejecutar desde la RAÍZ del proyecto:
REM   docker\create_image.bat
REM =========================================================

REM TODO: construir la imagen de app-service con:
REM   docker build -f docker/app-service.Dockerfile -t TU_USUARIO/app-service:latest .
REM   (el punto final es importante — indica el contexto de build)

REM TODO: construir la imagen de utility-service con:
REM   docker build -f docker/utility-service.Dockerfile -t TU_USUARIO/utility-service:latest .

REM TODO: mostrar mensaje de éxito al terminar