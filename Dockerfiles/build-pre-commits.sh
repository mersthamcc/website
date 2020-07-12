#!/usr/bin/env bash

set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

pushd ${DIR}/php-linter
docker build --force-rm --pull -t php-linter:local .
popd