#!/bin/bash
set -e
cd /opt/raildock

if [ ! -f .env ]; then
  echo ".env not found. Create /opt/raildock/.env first" >&2
  exit 1
fi

if [ ! -f deploy/image.env ]; then
  echo "deploy/image.env not found in bundle" >&2
  ls -al deploy || true
  exit 1
fi

# 기존 .env에서 ECR_URI/IMAGE_TAG 제거 후 새 값으로 덮어쓰기
grep -v '^ECR_URI=' .env | grep -v '^IMAGE_TAG=' > .env.tmp || true
cat .env.tmp deploy/image.env > .env
rm -f .env.tmp

echo "==== merged .env ===="
cat .env

docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
docker image prune -f || true