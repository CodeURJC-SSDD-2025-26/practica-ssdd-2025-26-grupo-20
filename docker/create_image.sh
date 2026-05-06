#!/bin/bash
# =========================================================
# RODRI
# =========================================================
# Versión Linux/Mac de create_image.bat
# Mismo contenido, misma lógica.
# =========================================================

echo "Construyendo la imagen de app-service..."
docker build -f docker/app-service.Dockerfile -t TU_USUARIO/app-service:latest .

echo "Construyendo la imagen de utility-service..."
docker build -f docker/utility-service.Dockerfile -t TU_USUARIO/utility-service:latest .

echo "========================================================="
echo "¡Imágenes construidas con éxito!"
echo "Puedes comprobarlas ejecutando: docker images"
echo "========================================================="