#!/bin/bash

set -e

if [[ "${1#-}" != "$1" ]]; then
	set -- php-lint.sh "$@"
fi

exec "$@"
