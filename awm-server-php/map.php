<?php
require_once("common.php");

$mysqli = attemptConnect();
if($mysqli->connect_error){
  http_response_code(503);
  echo "Problem connecting to the database to store the data";
  exit;
}
?>

<html>
<head>
<script src="//cdnjs.cloudflare.com/ajax/libs/d3/3.5.3/d3.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/topojson/1.6.9/topojson.min.js"></script>
<script src="/datamaps.all.min.js"></script>
<style>
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      #map {
        height: 100%;
      }
      /* Optional: Makes the sample page fill the window. */
      html, body {
        height: 100%;
        margin: 0;
        padding: 0;
      }
    </style>
</head>
<body>
  <div id="map"></div>
  <script>
      var map, heatmap;
      var infowindow;
      function initMap() {
        map = new google.maps.Map(document.getElementById('map'), {
          //center: {lat: 36.6270221, lng: -91.6137488},
          //zoom: 4.0
          center: {lat: 54.1779309, lng: -58.4336361},
          zoom: 16.5
        });

        var btpoints = [
        <?php
        $mysqli = attemptConnect();
        if($mysqli->connect_error){
          return;
        }

        $sql = "SELECT t.longitude, t.latitude, MAX(t.count), t.id as count FROM ((SELECT r.id, r.longitude, r.latitude, COUNT(o.reporting_device_id) as count, o.mac_type from reporting_device as r INNER JOIN observed_device o ON r.id = o.reporting_device_id WHERE r.longitude != 0 AND o.mac_type = 0 GROUP BY o.reporting_device_id) as t) GROUP BY t.longitude, t.latitude, t.id";
        $result = $mysqli->query($sql);
        if($result !== false && $result->num_rows == 0) {
          $mysqli->close();
          return;
        }

        while($row = $result->fetch_row()) {
          $id = $row[3];
          $sql2 = "SELECT * FROM observed_device WHERE reporting_device_id = $id";
          $result2 = $mysqli->query($sql2);
          $macs = "";
          while($row2 = $result2->fetch_row()) {
            $macs .= $row2[0] . " " . int2macaddress($row2[2]) . " " . $row2[4] . " " . $row2[5] . " " . $row2[6] . " " . $row2[7] . " " . $row2[8] .  "<br/>";
          }
          
          $sql3 = "SELECT * FROM reporting_device where id = $id";
          $result3 = $mysqli->query($sql3);
          $row3 = $result3->fetch_row();
          $wifi_mac = $row3[3];
          $bt_mac = $row3[2];
          $html = "Observed from: " . int2macaddress($wifi_mac) . " " . int2macaddress($bt_mac) . "<br/>$macs";
          
          echo "{location: new google.maps.LatLng($row[1], $row[0]), weight: $row[2], html: '$row[1], $row[0] <br/>$html'},\n";
        }

        $mysqli->close();
        ?>
        ];
        
        var wifipoints = [
        <?php
        $mysqli = attemptConnect();
        if($mysqli->connect_error){
          return;
        }

        $sql = "SELECT t.longitude, t.latitude, MAX(t.count), t.id as count FROM ((SELECT r.id, r.longitude, r.latitude, COUNT(o.reporting_device_id) as count, o.mac_type from reporting_device as r INNER JOIN observed_device o ON r.id = o.reporting_device_id WHERE r.longitude != 0 AND o.mac_type = 1 GROUP BY o.reporting_device_id) as t) GROUP BY t.longitude, t.latitude, t.id";
        $result = $mysqli->query($sql);
        if($result !== false && $result->num_rows == 0) {
          $mysqli->close();
          return;
        }

        while($row = $result->fetch_row()) {
          $id = $row[3];
          $sql2 = "SELECT * FROM observed_device WHERE reporting_device_id = $id";
          $result2 = $mysqli->query($sql2);
          $macs = "";
          while($row2 = $result2->fetch_row()) {
            $macs .= $row2[0] . " " . int2macaddress($row2[2]) . " " . $row2[4] . " " . $row2[5] . " " . $row2[6] . " " . $row2[7] . " " . $row2[8] . "<br/>";
          }
          
          $sql3 = "SELECT * FROM reporting_device where id = $id";
          $result3 = $mysqli->query($sql3);
          $row3 = $result3->fetch_row();
          $wifi_mac = $row3[3];
          $bt_mac = $row3[2];
          $html = "Observed from: " . int2macaddress($wifi_mac) . " " . int2macaddress($bt_mac) . "<br/>$macs";
          
          echo "{location: new google.maps.LatLng($row[1], $row[0]), weight: $row[2], html: '$row[1], $row[0] <br/>$html'},\n";
        }

        $mysqli->close();
        ?>
        ];

        heatmap = new google.maps.visualization.HeatmapLayer({
          data: btpoints,
          map: map,
          maxIntensity: 100,
          radius: 20,
          opacity: 0.5
        });

        heatmap2 = new google.maps.visualization.HeatmapLayer({
          data: wifipoints,
          map: map,
          maxIntensity: 100,
          radius: 20,
          opacity: 0.5,
          gradient: [
            'rgba(0, 255, 255, 0)',
            'rgba(0, 255, 255, 1)',
            'rgba(0, 191, 255, 1)',
            'rgba(0, 127, 255, 1)',
            'rgba(0, 63, 255, 1)',
            'rgba(0, 0, 255, 1)',
            'rgba(0, 0, 223, 1)',
            'rgba(0, 0, 191, 1)',
            'rgba(0, 0, 159, 1)',
            'rgba(0, 0, 127, 1)',
            'rgba(63, 0, 91, 1)',
            'rgba(127, 0, 63, 1)',
            'rgba(191, 0, 31, 1)',
            'rgba(255, 0, 0, 1)'
          ]
        });
        
        setMarkers(map, btpoints, "#2d6fa0");
        setMarkers(map, wifipoints, "#a04a2d");
        
        infowindow = new google.maps.InfoWindow({
          content: "Loading..."
        });
      }
      
      /* 
       * This functions sets the markers (array)
       */
        function setMarkers(map, markers, color) {
          for (var i = 0; i < markers.length; i++) {
            var site = markers[i];
            
            var marker = new google.maps.Marker({
              position: site.location,
              icon: {
                path: google.maps.SymbolPath.CIRCLE,
                scale: 2,
                strokeColor: color
              },
              map: map,
              title: 'a title',
              zIndex: 1,
              html: site.html
            });
            
            google.maps.event.addListener(marker, "click", function () {
              infowindow.setContent(this.html);
              infowindow.open(map, this);
            });
          }
        }

      </script>
      <script src="https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/markerclusterer.js"></script>
      <script src="https://maps.googleapis.com/maps/api/js?key=<?php echo MAPSKEY; ?>&callback=initMap&libraries=visualization" async defer></script>
    </body>
</html>
<?

$mysqli->close();

?>
