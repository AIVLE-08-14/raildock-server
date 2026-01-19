#!/bin/bash
set -e
cd /opt/raildock

cat .env

docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
docker image prune -f || true