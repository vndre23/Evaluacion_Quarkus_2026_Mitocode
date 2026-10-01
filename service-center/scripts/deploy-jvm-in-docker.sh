#!/bin/bash

# Mover el contexto de ejecución a la raíz del proyecto dinámicamente
cd "$(dirname "$0")/.."

# 1. Variables de configuración
APP_NAME="service-center-jvm"
IMAGE_NAME="mitocode/service-center-jvm"
TAG="latest"
DOCKERFILE="docker/Dockerfile.jvm"
NETWORK_NAME="mi-red-quarkus"

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'


# 0. Detener y eliminar contenedor previo si existe
echo -e "${GREEN}0. Deteniendo y eliminando contenedor previo...${NC}"

docker stop $APP_NAME 2>/dev/null || true
docker rm $APP_NAME 2>/dev/null || true


# 1. Construir la nueva imagen
echo -e "${GREEN}1. Construyendo imagen Docker...${NC}"

docker build -f $DOCKERFILE -t $IMAGE_NAME:$TAG .

if [ $? -ne 0 ]; then
    echo -e "${RED}Error al construir la imagen. Abortando.${NC}"
    exit 1
fi


# 2. Ejecutar el contenedor
echo -e "${GREEN}2. Ejecutando nuevo contenedor...${NC}"

docker run -d \
  --name $APP_NAME \
  --network $NETWORK_NAME \
  -p 8080:8080 \
  $IMAGE_NAME:$TAG


# 3. Mostrar logs para verificar arranque
echo -e "${GREEN}3. Logs del contenedor (Ctrl+C para salir):${NC}"

docker logs -f $APP_NAME