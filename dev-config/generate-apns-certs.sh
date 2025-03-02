#!/usr/bin/env bash

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

mkdir -p "${DIR}/keys"

openssl req -x509 \
  -newkey rsa:4096 \
  -keyout "${DIR}/keys/server.key" \
  -out "${DIR}/keys/server.pem" \
  -sha256 \
  -days 3650 \
  -nodes \
  -subj "/C=GB/O=Mock/CN=localhost"

echo "Add the following to your .env file"
echo "MOCK_APNS_SERVER_KEY=${DIR}/keys/server.key"
echo "MOCK_APNS_SERVER_CERTIFICATE=${DIR}/keys/server.pem"
