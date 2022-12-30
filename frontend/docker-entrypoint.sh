#!/bin/bash
set -e
DEBUG_OPTIONS=""
if [[ -n ${DEBUG_PORT} ]]; then
    DEBUG_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT}"
fi

echo "Starting GraphQL service ..."
java "${DEBUG_OPTIONS}" -jar graphql.jar