#!/usr/bin/env bash

set -e

echo "export B2_APPLICATION_KEY=${B2_APPLICATION_KEY}" > /etc/profile.d/mcc-env.sh
echo "export B2_APPLICATION_KEY_ID=${B2_APPLICATION_KEY_ID}" >> /etc/profile.d/mcc-env.sh
echo "export MCC_BACKUP_PASSPHRASE=${MCC_BACKUP_PASSPHRASE}" >> /etc/profile.d/mcc-env.sh
echo "export MCC_LOGGER_PASSWORD=${MCC_LOGGER_PASSWORD}" >> /etc/profile.d/mcc-env.sh
echo "export MYSQL_ROOT_PASSWORD_FILE=${MYSQL_ROOT_PASSWORD_FILE}" >> /etc/profile.d/mcc-env.sh
echo "export MYSQL_PASSWORD=$(cat ${MYSQL_ROOT_PASSWORD_FILE})" >> /etc/profile.d/mcc-env.sh
echo "export MYSQL_HOST=${MYSQL_HOST}" >> /etc/profile.d/mcc-env.sh
echo "export MYSQL_USER=root" >> /etc/profile.d/mcc-env.sh
echo "export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}" >> /etc/profile.d/mcc-env.sh
echo "export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}" >> /etc/profile.d/mcc-env.sh
echo "export AWS_REGION=${AWS_REGION}" >> /etc/profile.d/mcc-env.sh

echo "Executing entrypoint with parameters $@"
/entry.sh "$@"
