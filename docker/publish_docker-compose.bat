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
REM =========================================================

echo Generando artefacto OCI con el docker-compose.yml...

echo FROM scratch > temp-compose.Dockerfile
echo COPY docker-compose.yml / >> temp-compose.Dockerfile

docker build -f temp-compose.Dockerfile -t rodrigodefrutos/docker-compose:latest .

echo Subiendo artefacto a DockerHub...
docker push rodrigodefrutos/docker-compose:latest

del temp-compose.Dockerfile

echo =========================================================
echo ¡docker-compose.yml publicado con exito en DockerHub!
echo Para descargarlo y usarlo en cualquier equipo:
echo   docker pull rodrigodefrutos/docker-compose:latest
echo =========================================================
pause