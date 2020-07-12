# Inspired by https://git.unl.edu/unl-its/docker-ci
FROM php:7.4-alpine

RUN apk add --no-cache bash nodejs nodejs-npm $PHPIZE_DEPS \
    && pecl install xdebug-2.5.0 \
    && docker-php-ext-enable xdebug \
    && apk del --no-cache $PHPIZE_DEPS

RUN curl -o /usr/local/bin/composer https://getcomposer.org/composer.phar \
  && chmod 755 /usr/local/bin/composer

CMD ["bash"]
