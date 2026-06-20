#!/bin/bash

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

echo "$ROOT_DIR"

tail -f "$ROOT_DIR"/logs/*.log
