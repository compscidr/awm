# AWM server
- listens on a REST API / CRUD
  - meant for working with the data after the fact
- listens on UDP for incoming awm data
  - meant for high volume best effort
  - tries to decode, dumps into db
- Compared with the Android side, where room automatically creates a db schema / migrations