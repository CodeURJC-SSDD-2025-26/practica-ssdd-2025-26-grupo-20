#!/bin/bash
# =========================================================
# RODRI
# =========================================================
# Versión Linux/Mac de publish_docker-compose.bat
# =========================================================

echo "Generando artefacto OCI con el docker-compose.yml..."

cat <<EOF > temp-compose.Dockerfile
FROM scratch
COPY docker-compose.yml /
EOF

docker build -f temp-compose.Dockerfile -t rodrigodefrutos/docker-compose:latest .

echo "Subiendo artefacto a DockerHub..."
docker push rodrigodefrutos/docker-compose:latest

rm temp-compose.Dockerfile

echo "========================================================="
echo "¡docker-compose.yml publicado con éxito en DockerHub!"
echo "Para descargarlo y usarlo en cualquier equipo:"
echo "  docker pull rodrigodefrutos/docker-compose:latest"
echo "========================================================="