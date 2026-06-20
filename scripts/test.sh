#!/bin/bash

set -e

source "$(dirname "$0")/common.sh"

for service in "${SERVICES[@]}"
do
  echo "testing $service..."

  cd "$ROOT_DIR/services/$service"

  ./mvnw test
done
