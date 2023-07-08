#!/bin/bash
# https://github.com/laradock/laradock/issues/610#issuecomment-697376015
# wait for mysql
while ! mysqladmin ping -h"mysql" -u"${DB_USERNAME}" -p"${DB_PASSWORD}" --silent; do
  sleep 1
done

php artisan storage:link
php artisan key:generate
php artisan config:cache
php artisan migrate --seed

php-fpm