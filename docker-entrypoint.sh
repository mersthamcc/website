#!/bin/bash
set -e
if [[ -n ${DEBUG_PORT} ]]; then
    set -- "$@" "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT}"
fi

echo "Starting ${COMPONENT} service ..."
exec java -jar "${COMPONENT}.jar" "$@"
