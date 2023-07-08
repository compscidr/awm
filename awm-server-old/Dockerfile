# https://www.digitalocean.com/community/tutorials/how-to-containerize-a-laravel-application-for-development-with-docker-compose-on-ubuntu-18-04
FROM php:8.1-fpm as base

# git and unizp required for composer to install stuff it seems
RUN apt-get update && apt-get install --no-install-recommends -y wget git unzip libonig-dev libpng-dev default-mysql-client

# Clear cache
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

# Install PHP extensions
RUN docker-php-ext-install pdo_mysql mbstring exif pcntl bcmath gd

# Get latest Composer
WORKDIR /tmp
RUN wget https://raw.githubusercontent.com/composer/getcomposer.org/76a7060ccb93902cd7576b67264ad91c8a2700e2/web/installer -O - -q | php -- --quiet && mv composer.phar /usr/local/bin/composer

FROM base as app

# Arguments defined in docker-compose.yml
ARG user=default
ARG uid=1000

# copy the app
# Create system user to run Composer and Artisan Commands
RUN useradd -G www-data,root -u $uid -d /home/$user $user
RUN mkdir -p /home/$user/.composer && \
    chown -R $user:$user /home/$user

WORKDIR /var/www/html
COPY . /var/www/html
EXPOSE 80
RUN composer install
ENTRYPOINT [ "entrypoint.sh" ]
CMD ["run"]

FROM base as test

COPY . /usr/src/awm
WORKDIR /usr/src/awm

RUN composer require --dev phpunit/phpunit ^9 && composer require overtrue/phplint:^4.0 --dev

ENV PATH="/usr/src/awm/vendor/bin:${PATH}"
RUN cp template.env .env && php artisan key:generate && php artisan config:cache
CMD ["/usr/src/awm/vendor/bin/phpunit"]

FROM test as check
CMD ["/usr/src/awm/vendor/bin/phplint"]]