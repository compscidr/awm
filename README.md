# awm-lib: Android Wireless Measurement Library
[![Android Build Status](https://travis-ci.com/compscidr/awm-lib.svg?branch=master)](https://travis-ci.com/compscidr/awm-lib?branch=master)

This project aims to collect and open source wireless statistics and information
in a way that does not disrupt the functioning of apps, but provides useful data
such as:

- [ ] what percentage of the day a user has internet access?
- [ ] how long does the user use Wi-Fi versus Cellular data?
- [x] how often and for how long are users within Bluetooth range of each other?
- [x] how often and for how long are users within Wi-Fi or Wi-Fi direct range of each other?
- [ ] how often and for how long are users within NFC range of each other?
- [ ] periodically sample their network performance: throughput, delay, jitter, etc.
- [x] obtain and track battery stats to measure the rate of discharge while scanning bt, wifi and wifi directly continually

## Using the library
Add to your `build.gradle` file the following:
```
dependencies {
  implementation 'com.jasonernst:awm:1.1.23'
}
```
This has been tested with gradle 4.9 and Android Studio 3.3.


Simply construct an `AndroidWirelessStatsCollector` and call the `start()`
function. You can do this in the `onCreate` call if you like. The first boolean value
is used to set whether data should be sent immediately to the network server, or
if the data should be stored first locally and sent in larger groups, which is
more efficient, but less "real-time". The second boolean is for whether you'd
like to start with a fresh data file on each time the app starts (instead of
using the cached saved data that may not have uploaded if Internet access was
not available)

```
awsc = new AndroidWirelessStatsCollector(this, false, true);
awsc.start();
```

Then to clean up, in the onDestroy just call the `stop()` function.
```
awsc.stop();
```

## Receiving the stats that are being collected
The library is setup to publish RxJava events to the applications so they can
receive the stats as they are being collected. Currently, there are Bluetooth,
Wi-Fi hotspots (APs) and GPS stats being collected.

In order to receive the EventBus events you need to initailise the event bus:
```
protected Bus eventBus = BusProvider.getInstance();
```

and register to receive the events:
```
eventBus.register(this);
```

To obtain Bluetooth or Wi-Fi hotspot stats:
```
@Subscribe public void updateNetworkDevices(NetworkStat networkStat) {
  if (networkStat.getType() == BLUETOOTH) {
    ...
  } else if (networkStat.getType() == WIFI) {
    ...
  }
}
```

To obtain GPS stats:
```
@Subscribe public void updateGPS(GPSStats gpsStats) {
  ...
}
```

To obtain Battery stats:
```
@Subscribe public void updateBattery(BatteryStats batteryStats) {
  ...
}
```

You can see an example of this data being displayed in an activity in the example project:
https://github.com/compscidr/awm-lib-example/blob/master/app/src/main/java/io/rightmesh/awm_lib_example/MainActivity.java
