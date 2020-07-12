#!/usr/bin/env bash
docker swarm join-token worker > /dev/null 2>&1
LOCAL_MODE=$?

if [[ ${LOCAL_MODE} == "1" ]]; then
  docker container ls -q | \
    xargs docker inspect | \
    jq -r ".[].Config.Labels.\"tls-cert-host-check\" | select (.!=null)" | \
    xargs -n 1  -I fqdn bash -c "~/bin/cert-detail.sh fqdn | curl -H \"Content-Type: application/json\" -XPOST \"http://elasticsearch:9200/cert-checks/_doc/\" -d @-"
else
  docker service ls -q | \
  xargs docker service inspect | \
  jq -r ".[].Spec.Labels.\"tls-cert-host-check\" | select (.!=null)" | \
  xargs -n 1  -I fqdn bash -c "~/bin/cert-detail.sh fqdn | curl -u mcc-logger:${MCC_LOGGER_PASSWORD} -H \"Content-Type: application/json\" -XPOST \"http://elasticsearch:9200/cert-checks/_doc/\" -d @-"
fi
