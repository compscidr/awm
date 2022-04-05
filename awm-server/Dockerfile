# https://www.digitalocean.com/community/tutorials/how-to-containerize-a-laravel-application-for-development-with-docker-compose-on-ubuntu-18-04
FROM php:8.1-apache as base
RUN docker-php-ext-install pdo pdo_mysql mysqli && a2enmod rewrite

# git and unizp required for composer to install stuff it seems
RUN apt-get update && apt-get install --no-install-recommends -y wget git unzip

# get composer
WORKDIR /tmp
RUN wget https://raw.githubusercontent.com/composer/getcomposer.org/76a7060ccb93902cd7576b67264ad91c8a2700e2/web/installer -O - -q | php -- --quiet && mv composer.phar /usr/local/bin/composer

FROM base as app

# copy the app
WORKDIR /var/www/html
COPY . /var/www/html/
EXPOSE 80
RUN composer install

# this will listen on port 8000 by default
CMD ["php artisan serve --host 0.0.0.0"]

FROM base as test

COPY . /usr/src/awm
WORKDIR /usr/src/awm

RUN composer require --dev phpunit/phpunit ^9 && composer require overtrue/phplint:^4.0 --dev

ENV PATH="/usr/src/awm/vendor/bin:${PATH}"
RUN cp template.env .env && php artisan key:generate && php artisan config:cache
CMD ["/usr/src/awm/vendor/bin/phpunit"]

FROM test as check
CMD ["/usr/src/awm/vendor/bin/phplint"]]