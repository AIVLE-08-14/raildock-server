#!/bin/bash
set -e
cd /opt/raildock
docker compose -f docker-compose.prod.yml down || true