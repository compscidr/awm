<?php
require_once("config.php");

function attemptConnect() {
  $mysqli = new mysqli(HOST, USERNAME, PASSWORD, DBNAME);
  return $mysqli;
}

//http://www.onurguzel.com/storing-mac-address-in-a-mysql-database/
function macstringtobigint($mac) {
  //strip out the colons from the mac address string
  $base10mac = str_replace(":", "", $mac);

  return base_convert($base10mac, 16, 10);
}

function int2macaddress($int) {
    $hex = base_convert($int, 10, 16);
    while (strlen($hex) < 12)
        $hex = '0'.$hex;
    return strtoupper(implode(':', str_split($hex,2)));
}


/**
 * This class represents the device which made the observations of other
 * devices nearby. Since this is the one with a GPS, it has a position
 * associated with it. The id field is the id in the database, not the uuid.
 */
class Observer {
  private $id;
  private $longitude;
  private $latitude;

  function __construct($id, $longitude, $latitude) {
    $this->id = $id;
    $this->$longitude = $longitude;
    $this->$latitude = $latitude;
  }
}

function getObservers($latitude_top_left, $longitude_top_left,
  $latitude_bottom_right, $longitude_bottom_right) {
  $mysqli = attemptConnect();
  if($mysqli->connect_error){
    return;
  }

  $sql = "SELECT id, longitude, latitude from reporting_device WHERE longitude > '$longitude_top_left' AND longitude < '$longitude_bottom_right' AND latitude > '$latitude_top_left' AND latitude < '$latitude_bottom_right'";
  $result = $mysqli->query($sql);
  if($result !== false && $result->num_rows == 0) {
    $mysqli->close();
    return;
  }

  echo "<br/>";
  while($row = $result->fetch_row()) {
    echo "$row[0]: ($row[1], $row[2])<br/>";
  }

  $mysqli->close();
}



?>
