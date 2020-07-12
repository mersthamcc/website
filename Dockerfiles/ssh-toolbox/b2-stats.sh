#!/usr/bin/env bash

set -e
b2 authorize-account
b2 list-buckets | \
  awk '{print $3}' | \
  xargs -n 1 -I {} \
  bash -c "b2 get-bucket --showSize {} | jq -rc --arg timestamp \"\$(date -Iseconds)\" '{\"@timestamp\": \$timestamp} + . ' | curl -u mcc-logger:${MCC_LOGGER_PASSWORD} -H \"Content-Type: application/json\" -XPOST \"http://elasticsearch:9200/b2-stats/_doc/\" -d @-"
