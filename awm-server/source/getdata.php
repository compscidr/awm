<?php
require_once("common.php");

if(!isset($_GET['latitude_top_left']) || !isset($_GET['longitude_top_left']) ||
  !isset($_GET['latitude_bottom_right']) ||
  !isset($_GET['longitude_bottom_right'])) {

  http_response_code(400);
  echo "Need to provide bounding longitude and latitudes.";
  exit;
}

$latitude_top_left = $_GET['latitude_top_left'];
$longitude_top_left = $_GET['longitude_top_left'];
$latitude_bottom_right = $_GET['latitude_bottom_right'];
$longitude_bottom_right = $_GET['longitude_bottom_right'];

echo "($latitude_top_left, $longitude_top_left), ($latitude_bottom_right,";
echo "$longitude_bottom_right)";

$observers = getObservers($longitude_top_left, $latitude_top_left,
  $longitude_bottom_right, $latitude_bottom_right);



?>
