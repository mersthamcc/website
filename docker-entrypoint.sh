#!/bin/bash

set -eu

DEBUG_OPTIONS=""
if [[ ! -z ${DEBUG_PORT} ]]; then
    DEBUG_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT}"
fi

echo "Starting Frontend service ..."
java ${DEBUG_OPTIONS} -jar frontend.jar