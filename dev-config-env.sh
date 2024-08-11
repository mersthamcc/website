#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

pushd "${DIR}/dev-config/cognito" > /dev/null || exit 1
echo "COGNITO_USER_POOL_ID=$(terraform output -json | jq -r '.cognito_user_pool_id.value')"
echo "COGNITO_ISSUER_URI=https://cognito-idp.eu-west-2.amazonaws.com/$(terraform output -json | jq -r '.cognito_user_pool_id.value')"
echo "COGNITO_UI_URI=https://$(terraform output -json | jq -r '.cognito_ui_uri.value').auth.eu-west-2.amazoncognito.com"

echo "COGNITO_CLIENT_ID=$(terraform output -json | jq -r '.website_client_id.value')"
echo "COGNITO_CLIENT_SECRET=$(terraform output -json | jq -r '.website_client_secret.value')"

echo "WEBSITE_CLIENT_CREDENTIALS_ID=$(terraform output -json | jq -r '.website_credentials_client_id.value')"
echo "WEBSITE_CLIENT_CREDENTIALS_SECRET=$(terraform output -json | jq -r '.website_credentials_client_secret.value')"

echo "JUPYTER_CLIENT_ID=$(terraform output -json | jq -r '.jupyter_client_id.value')"
echo "JUPYTER_CLIENT_SECRET=$(terraform output -json | jq -r '.jupyter_client_secret.value')"
