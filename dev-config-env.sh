#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

pushd "${DIR}/dev-config/cognito" > /dev/null || exit 1
echo "COGNITO_USER_POOL_ID=$(terraform output -json | jq -r '.cognito_user_pool_id.value')"
echo "COGNITO_ISSUER_URI=https://cognito-idp.eu-west-2.amazonaws.com/$(terraform output -json | jq -r '.cognito_user_pool_id.value')"
echo "COGNITO_UI_URI=https://$(terraform output -json | jq -r '.cognito_ui_uri.value').auth.eu-west-2.amazoncognito.com"

echo "COGNITO_CLIENT_ID=$(terraform output -json | jq -r '.website_client_id.value')"
echo "COGNITO_CLIENT_SECRET=$(terraform output -json | jq -r '.website_client_secret.value')"

echo "JUPYTER_CLIENT_ID=$(terraform output -json | jq -r '.jupyter_client_id.value')"
echo "JUPYTER_CLIENT_SECRET=$(terraform output -json | jq -r '.jupyter_client_secret.value')"

echo "TRANSACTION_QUEUE_URL=$(terraform output -json | jq -r '.transactions_queue_url.value')"
echo "TRANSACTION_RESPONSE_QUEUE_URL=$(terraform output -json | jq -r '.transaction_responses_queue_url.value')"

echo "SAFEGUARDING_QUEUE_URL=$(terraform output -json | jq -r '.safeguarding_queue_url.value')"
