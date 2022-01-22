FROM php:8.1-apache
RUN docker-php-ext-install pdo pdo_mysql mysqli
RUN a2enmod rewrite
COPY source/ /var/www/html/
EXPOSE 80