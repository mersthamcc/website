#!/usr/bin/env bash

echo "Setting Credentials..."
b2 authorize-account
MYSQL_ROOT_PASSWORD=$(cat ${MYSQL_ROOT_PASSWORD_FILE})

echo "Starting database backup..."
FILENAME=all-databases-$(date "+%Y-%m-%d_%H%M%S").sql
FINAL_FILENAME=all-databases-$(date "+%A").sql.enc.bz2
OUTPUT_DIR=/mnt/swarm-data-volume/backups/scheduled
docker exec -i $(docker ps -q -f name=shared-mysql) mysqldump --triggers --routines --all-databases -r /backups/scheduled/${FILENAME}

cd ${OUTPUT_DIR}
echo "Compressing..."
bzip2 ${FILENAME}
echo "Encrypting..."
openssl enc -aes-256-cbc -e -pbkdf2 -pass file:<(echo "${MCC_BACKUP_PASSPHRASE}") -in ${FILENAME}.bz2 > ${FINAL_FILENAME}
echo "Uploading database backup to B2..."
b2 upload-file --noProgress mcc-database-backup ${FINAL_FILENAME} ${FINAL_FILENAME} > ${FINAL_FILENAME}.out 2>&1
rm -f ${FILENAME}.bz2

cd /mnt/swarm-data-volume/
echo "Tarballing Resources for Live"
tar cfvz $OUTPUT_DIR/live-resources.tgz live/ > /dev/null 2>&1
echo "Tarballing Resources for Test"
tar cfvz $OUTPUT_DIR/test-resources.tgz test/ > /dev/null 2>&1

echo "Uploading to B2..."
cd $OUTPUT_DIR
b2 upload-file --noProgress mcc-live-backup live-resources.tgz resources-$(date "+%A").tgz > live-resources.out 2>&1
b2 upload-file --noProgress mcc-test-backup test-resources.tgz resources-$(date "+%A").tgz > test-resources.out 2>&1
echo "Tidying up..."
rm -f live-resources.tgz test-resources.tgz

echo "Backups Complete!"
