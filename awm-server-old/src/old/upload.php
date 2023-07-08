<?php
require_once("common.php");
$data = json_decode(file_get_contents('php://input'), true);
if($data == null) {
  http_response_code(400);
  echo "Failed decoding json:";
  switch (json_last_error()) {
    case JSON_ERROR_NONE:
      echo ' - No errors';
    break;
    case JSON_ERROR_DEPTH:
      echo ' - Maximum stack depth exceeded';
    break;
    case JSON_ERROR_STATE_MISMATCH:
      echo ' - Underflow or the modes mismatch';
    break;
    case JSON_ERROR_CTRL_CHAR:
      echo ' - Unexpected control character found';
    break;
    case JSON_ERROR_SYNTAX:
      echo ' - Syntax error, malformed JSON';
    break;
    case JSON_ERROR_UTF8:
      echo ' - Malformed UTF-8 characters, possibly incorrectly encoded';
    break;
    default:
      echo ' - Unknown error';
    break;
  }
  exit;
}

//make sure the input is well formed and contains stats
if(array_key_exists("awm_measure", $data)) {
  $awm_measure = $data["awm_measure"];
} else {
  http_response_code(400);
  echo "Malformed data: missing awm-measure";
  exit;
}

if(array_key_exists("reporting_device", $awm_measure)) {
  $rd = $awm_measure["reporting_device"];

  $mysqli = attemptConnect();
  if($mysqli->connect_error){
    http_response_code(503);
    echo "Problem connecting to the database to store the data";
    exit;
  }

  $bt_mac_address = macstringtobigint($rd['bt_mac_address']);
  $wifi_mac_address = macstringtobigint($rd['wifi_mac_address']);
  $longitude = floatval($rd['longitude']);
  $latitude = floatval($rd['latitude']); 
  
  $battery = floatval($rd['battery_life']);
  if(!isset($rd['has_cellular_internet']) || !$rd['has_cellular_internet']) {
    $rd['has_cellular_internet'] = 0;
  } else {
    $rd['has_cellular_internet'] = 1;
  }
  if(!isset($rd['has_wifi_internet'])|| !$rd['has_wifi_internet']) {
    $rd['has_wifi_internet'] = 0;
  } else {
    $rd['has_wifi_internet'] = 1;
  }

  $sql = <<<EOT
  INSERT into `reporting_device` (`uuid`, `bt_mac_address`,
  `wifi_mac_address`, `ipv4_address`, `ipv6_address`, `timestamp`, `longitude`,
  `latitude`, `OS`, `battery_life`, `has_cellular_internet`,
  `has_wifi_internet`, `cellular_throughput`, `wifi_throughput`,
  `cellular_ping`, `wifi_ping`, `cellular_operator`, `cellular_network_type`)
  VALUES ('$rd[uuid]', '$bt_mac_address', '$wifi_mac_address',
  INET_ATON('$rd[ipv4_address]'), INET6_ATON('$rd[ipv6_address]'),
  '$rd[timestamp]', $longitude, $latitude, '$rd[OS]', $battery,
   $rd[has_cellular_internet], $rd[has_wifi_internet], $rd[cellular_throughput],
   $rd[wifi_throughput], $rd[cellular_ping], $rd[wifi_ping],
   '$rd[cellular_operator]', $rd[cellular_network_type])
EOT;
  $mysqli->real_query($sql);
  if($mysqli->connect_errno) {
    $mysqli->close();
    http_response_code(503);
    echo "Error storing the data in the db: ".$mysqli->connect_error;
    exit;
  }
  $insertid = mysqli_insert_id($mysqli);
  $mysqli->close();

} else {
  http_response_code(400);
  echo "Malformed data: missing reporting device";
  exit;
}

if(array_key_exists("devices", $awm_measure)) {
  $devices = $awm_measure["devices"];
  $mysqli = attemptConnect();
  if($mysqli->connect_error){
    http_response_code(503);
    echo "Problem connecting to the database to store the data";
    exit;
  }
  foreach($devices as $device) {
    $macaddress = macstringtobigint($device['mac_address']);
    $sql = <<<EOT
    INSERT INTO `observed_device` (`reporting_device_id`, `mac_address`,
    `mac_type`, `network_name`, `signal_strength` , `frequency`, 
    `channel_width`, `security`) VALUES ($insertid, $macaddress,
    $device[mac_type], '$device[network_name]', 
    $device[signal_strength], $device[frequency], 
    $device[channel_width], '$device[security]')
EOT;
    $mysqli->real_query($sql);
    if($mysqli->connect_errno){
      $mysqli->close();
      http_response_code(503);
      echo "Error storing the data in the db: ".$mysqli->connect_error;
      exit;
    }
  }
  $mysqli->close();
} else {
  echo "No devices found";
}
?>
