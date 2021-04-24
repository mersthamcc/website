#!/usr/bin/env bash

set -eu

function wait_for_docker_services() {
  RUNNING=0
  LOOP_COUNT=0
  echo -n "Waiting for service(s) to become healthy ($*) ."
  until [[ ${RUNNING} == $# || ${LOOP_COUNT} == 100 ]]; do
    RUNNING=$(docker-compose ps -q "$@" | xargs docker inspect | jq -rc '[ .[] | select(.State.Health.Status == "healthy")] | length')
    LOOP_COUNT=$((LOOP_COUNT + 1))
    echo -n "."
  done
  if [[ ${LOOP_COUNT} == 100 ]]; then
    echo "FAILED"
    return 1
  fi
  echo " done!"
  return 0
}

function start_docker_services() {
  docker-compose up --build -d --no-deps --quiet-pull "$@"
}

function stop_docker_services() {
  docker-compose down --rmi local --remove-orphans
}

echo "Shutting down environment..."
docker-compose down --volumes --rmi local --remove-orphans

echo "Removing Terraform state..."
rm -f dev-config/keycloak/terraform.*

echo "Initialising new environment..."
start_docker_services loki postgres
wait_for_docker_services loki postgres

docker-compose build flyway keycloak-terraform
docker-compose run flyway

start_docker_services keycloak
wait_for_docker_services keycloak

docker-compose run --rm keycloak-terraform

stop_docker_services keycloak postgres loki
