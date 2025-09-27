#!/bin/bash
set -e
if [[ -n ${DEBUG_PORT} ]]; then
    set -- "$@" "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT}"
fi

if [[ -n ${AGENT_DOWNLOAD_URL} ]]; then
  curl --request GET -sL \
       --url "${AGENT_DOWNLOAD_URL}"\
       --output '/app/agent.jar'
  export JAVA_TOOL_OPTIONS="-javaagent:/app/agent.jar -${JAVA_TOOL_OPTIONS}"
fi

echo "Starting ${COMPONENT} service ..."
exec java -jar "${COMPONENT}.jar" "$@"
