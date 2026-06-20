#!/bin/bash

source "$(dirname "$0")/common.sh"

for service in "${SERVICES[@]}"
do
  PID_FILE="$LOG_DIR/$service.pid"

  if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")

    if kill -0 "$PID" 2>/dev/null; then
      echo "Stopping $service ($PID)"
      kill "$PID"
    fi

    rm -f "$PID_FILE"
  fi
done

echo "stopping infrastructure..."
cd "$ROOT_DIR/infrastructure"
docker compose down
