FROM php:7.4-fpm-buster

RUN apt-get update \
 && apt-get install -y \
    git \
    libfreetype6-dev \
    libjpeg62-turbo-dev \
    libpng-dev \
    libmcrypt-dev \
    zip \
    gnupg \
    libgpgme11-dev \
    libtidy-dev tidy \
    libpq-dev libicu-dev \
    libzip-dev

RUN docker-php-ext-install -j$(nproc) iconv \
 && docker-php-ext-configure gd \
 && docker-php-ext-install -j$(nproc) gd pdo pdo_pgsql intl\
 && pecl install gnupg redis-4.3.0 \
 && docker-php-ext-enable gnupg \
 && docker-php-ext-enable redis \
 && docker-php-ext-install tidy \
 && docker-php-ext-enable tidy \
 && docker-php-ext-install zip \
 && docker-php-ext-enable zip \
 && docker-php-ext-enable pdo pdo_pgsql intl

RUN pecl install xdebug \
 && docker-php-ext-enable xdebug \
 && echo "xdebug.mode=debug" >> /usr/local/etc/php/conf.d/docker-php-ext-xdebug.ini \
 && echo "xdebug.client_host = host.docker.internal" >> /usr/local/etc/php/conf.d/docker-php-ext-xdebug.ini

COPY dev-config/php.ini /usr/local/etc/php/php.ini

WORKDIR /var/www
