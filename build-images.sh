#!/usr/bin/env bash

set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

pushd ${DIR}

if [[ -z ${1} ]]; then
    echo "Executing docker-compose build because file changed ${1}"
fi

echo "Building Main Image..."
docker build --force-rm --pull --no-cache --rm --quiet .

echo "Building dev and additional images..."
docker-compose build --force-rm --pull --parallel --no-cache --quiet
