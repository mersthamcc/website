#!/usr/bin/env bash

set -eu

echo "Shutting down environment..."
docker-compose down --volumes --rmi local --remove-orphans

echo "Removing Terraform state..."
rm -f dev-config/keycloak/terraform.*
