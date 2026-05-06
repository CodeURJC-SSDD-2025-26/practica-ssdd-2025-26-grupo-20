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

echo Construyendo la imagen de app-service...
docker build -f docker/app-service.Dockerfile -t rodrigodefrutos/app-service:latest .

echo Construyendo la imagen de db-service...
docker build -f docker/utility-service.Dockerfile -t rodrigodefrutos/utility-service:latest .

echo =========================================================
echo ¡Imagenes construidas con exito!
echo Puedes comprobarlas ejecutando: docker images
echo =========================================================
pause