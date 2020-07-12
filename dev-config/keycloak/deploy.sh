#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

function initialise_terraform() {
  tfenv install
  tfenv use $(cat .terraform-version)

  echo "Initialise Terraform"
  terraform init
}

set -e

MODE=apply
while [ $# -gt 0 ]; do
  case "$1" in
    -v|--validate)
      MODE=validate
      ;;
    -p|--plan)
      MODE=plan
      ;;
    -e|--environment-variables)
      MODE=environment
      ;;
  esac
  shift
done

case "${MODE}" in
  validate)
    initialise_terraform
    echo "Validating Terraform"
    terraform validate
    ;;
  plan)
    initialise_terraform
    echo "Planning Terraform"
    terraform plan
    ;;
  apply)
    initialise_terraform
    echo "Applying Terraform"
    terraform apply -auto-approve
    ;;
esac
