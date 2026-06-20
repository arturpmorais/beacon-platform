#!/bin/bash

set -e

source "$(dirname "$0")/common.sh"

mkdir -p "$LOG_DIR"

echo "starting infrastructure..."
cd "$ROOT_DIR/infrastructure"
docker compose up -d

for service in "${SERVICES[@]}"
do
  echo "starting $service..."

  cd "$ROOT_DIR/services/$service"

  ./mvnw spring-boot:run \
    > "$LOG_DIR/$service.log" 2>&1 &

  echo $! > "$LOG_DIR/$service.pid"
done

echo "all services started!"
