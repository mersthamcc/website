# Inspired by https://git.unl.edu/unl-its/docker-ci
FROM php:7.4-alpine

RUN apk add --no-cache bash

COPY php-lint.sh docker-entrypoint.sh /usr/local/bin/
RUN chmod 755 /usr/local/bin/docker-entrypoint.sh /usr/local/bin/php-lint.sh

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["--help"]
