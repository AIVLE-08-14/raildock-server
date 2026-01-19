#!/bin/bash
set -e

mkdir -p /opt/raildock
cd /opt/raildock

cp -f /opt/raildock/deploy/.env /opt/raildock/.env

docker --version
docker compose version || true