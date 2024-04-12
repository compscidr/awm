# awm-android
The android specifics of the awm library
- the room database implementation
- the device collectors (ble, etc)

## db migrations:
see: https://developer.android.com/training/data-storage/room/migrating-db-versions

## todo:
- on UDP exporter, prepend the observation with the db version so the backend can decide whether to drop or not