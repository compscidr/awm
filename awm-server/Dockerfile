# https://www.digitalocean.com/community/tutorials/how-to-containerize-a-laravel-application-for-development-with-docker-compose-on-ubuntu-18-04
FROM php:8.1-apache as base
RUN docker-php-ext-install pdo pdo_mysql mysqli && a2enmod rewrite
RUN apt-get update && apt-get install wget

# get composer
WORKDIR /tmp
RUN wget https://raw.githubusercontent.com/composer/getcomposer.org/76a7060ccb93902cd7576b67264ad91c8a2700e2/web/installer -O - -q | php -- --quiet && mv composer.phar /usr/local/bin/composer

FROM base as app
# copy the app
WORKDIR /var/www/html
COPY . /var/www/html/
EXPOSE 80
RUN composer install
# CMD ["php artisan serve --host 0.0.0.0"]

FROM base as test
# git seems to be required for phpunit
RUN apt-get update && apt-get install --no-install-recommends -y git unzip

COPY . /usr/src/awm
WORKDIR /usr/src/awm

RUN composer require --dev phpunit/phpunit ^9 && composer require overtrue/phplint:^4.0 --dev

# todo: change this to CMD and have the CI run it
RUN ./vendor/bin/phpunit tests && ./vendor/bin/phplint ./ --exclude=vendor