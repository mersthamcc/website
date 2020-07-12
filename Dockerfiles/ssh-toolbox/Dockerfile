FROM panubo/sshd:latest

ENV B2_APPLICATION_KEY_ID=notsupplied
ENV B2_APPLICATION_KEY=notsupplied
ENV MCC_BACKUP_PASSPHRASE=notsupplied
ENV AWS_ACCESS_KEY_ID=notsupplied
ENV AWS_SECRET_ACCESS_KEY=notsupplied
ENV AWS_REGION=eu-west-2

RUN apk update \
 && apk add mysql-client python3 jq openssl docker-cli bzip2 curl sudo groff \
 && pip3 install --upgrade pip \
 && pip3 install b2 \
 && pip3 install awscli \
 && chgrp 999 $(which docker) \
 && chmod g+s $(which docker)

COPY b2-stats.sh /root/bin/b2-stats.sh
COPY backup.sh /root/bin/backup.sh
COPY cert-detail.sh /root/bin/cert-detail.sh
COPY cert-expiry.sh /root/bin/cert-expiry.sh
COPY mcc-entry.sh /mcc-entry.sh

ENTRYPOINT ["/mcc-entry.sh"]
CMD ["/usr/sbin/sshd", "-D", "-e", "-f", "/etc/ssh/sshd_config"]
