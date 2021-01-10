#!/usr/bin/env bash

set -eu

function down() {
  docker-compose down
}

trap down EXIT

echo "Building Docker services..."
docker-compose --env-file ../.env build --no-cache

echo "Starting Postgres..."
docker-compose --env-file ../.env up -d postgres

echo -n "Waitng for Postgres to start ."
until docker-compose exec postgres pg_isready; do
    echo -n "."
done
echo " done!"

echo "Running migrations..."
docker-compose --env-file ../.env run flyway

echo "Running introspection"
docker-compose run prisma-introspect

echo "Running Build..."
npm run build