#!/bin/bash

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"

SERVICES=(
  "user-service"
  "notification-service"
  "email-worker"
)
