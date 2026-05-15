#!/usr/bin/env bash
set -euo pipefail

IMAGE_NAME="isms"
CONTAINER_NAME="isms-app"
HOST_PORT=9090
CONTAINER_PORT=9090

echo "🐳  Building Docker image: $IMAGE_NAME ..."
docker build -t "$IMAGE_NAME" .

echo ""
echo "🛑  Stopping & removing any existing container named '$CONTAINER_NAME' ..."
docker rm -f "$CONTAINER_NAME" 2>/dev/null || true

echo ""
echo "🚀  Starting container '$CONTAINER_NAME' on port $HOST_PORT ..."
docker run -d \
  --name "$CONTAINER_NAME" \
  -p "$HOST_PORT:$CONTAINER_PORT" \
  -e PORT="$CONTAINER_PORT" \
  "$IMAGE_NAME"

echo ""
echo "✅  ISMS is running at http://localhost:$HOST_PORT"
echo "    Logs: docker logs -f $CONTAINER_NAME"
echo "    Stop: docker stop $CONTAINER_NAME"
