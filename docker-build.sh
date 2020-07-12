#!/usr/bin/env bash

set -e
echo Running Script ${BASH_SOURCE[0]}...

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
while getopts ":pt:" opt; do
  case ${opt} in
    t )
      TAG=$OPTARG
      ;;
    p )
      PUSH=1
      ;;
    \? )
      echo "Invalid option: $OPTARG" 1>&2
      exit 1
      ;;
    : )
      echo "Invalid option: $OPTARG requires an argument" 1>&2
      exit 1
      ;;
  esac
done
shift $((OPTIND -1))

if [[ "${TAG}" = "" ]]; then
    TAG=latest
fi

echo Building Directory ${DIR}...

docker build --tag mersthamcc/website:${TAG} ${DIR}

if [[ "${PUSH}" = "1" ]]; then
    echo Pushing image mersthamcc/website:${TAG} to Dockerhub...
    docker push mersthamcc/website:${TAG}
fi
