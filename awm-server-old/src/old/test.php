<?php
require_once("scripts/common.php");

$mysqli = attemptConnect();
if($mysqli->connect_error){
  http_response_code(503);
  echo "Problem connecting to the database to store the data";
  exit;
}

$sql = "SELECT * FROM reporting_device";
$result = $mysqli->query($sql);
if($result !== false && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $reporting_id = $row["id"];
        $sql2 = "SELECT * FROM observed_device WHERE reporting_device_id = $reporting_id";
        $result2 = $mysqli->query($sql2);
        $long = $row["longitude"];
        $lat = $row["latitude"];
        echo "FOUND ".$result2->num_rows." devices at ".$lat.",".$long."<br/>\n";
    }
}

// want to find the average number of devices at particular latlng from all measurements


?>