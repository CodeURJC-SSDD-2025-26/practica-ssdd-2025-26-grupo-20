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

echo Subiendo app-service a DockerHub...
docker push rodrigodefrutos/app-service:latest

echo Subiendo utility-service a DockerHub...
docker push rodrigodefrutos/utility-service:latest

echo =========================================================
echo ¡Imagenes publicadas con exito en DockerHub!
echo =========================================================
pause