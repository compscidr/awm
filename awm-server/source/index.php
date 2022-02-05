<?php
// https://www.taniarascia.com/the-simplest-php-router
$request = $_SERVER['REQUEST_URI'];

switch ($request) {
    case '/':
        require __DIR__ . '/views/index.php';
        break;
    case '/upload':
        require __DIR__ . '/scripts/upload.php';
        break;
    case '/download':
        require __DIR__ . '/scripts/download.php';
        break;
    default:
        http_response_code(404);
        require __DIR__ . '/views/404.php';
        break;
}

?>