#!/bin/bash
# =========================================================
# RODRI
# =========================================================
# Versión Linux/Mac de publish_image.bat
# =========================================================

echo "Subiendo app-service a DockerHub..."
docker push rodrigodefrutos/app-service:latest

echo "Subiendo utility-service a DockerHub..."
docker push rodrigodefrutos/utility-service:latest

echo "========================================================="
echo "¡Imágenes publicadas con éxito en DockerHub!"
echo "========================================================="