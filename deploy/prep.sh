#!/bin/bash
set -e

mkdir -p /opt/raildock
cd /opt/raildock

if [ ! -f .env ]; then
  echo "/opt/raildock/.env not found. Create it first (MYSQL_PASSWORD etc)." >&2
  exit 1
fi

docker --version
docker compose version