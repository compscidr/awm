# awm-lib: Android Wireless Measurement Library
[![awm-lib test](https://github.com/compscidr/awm-lib/actions/workflows/check-and-test.yml/badge.svg)](https://github.com/compscidr/awm-lib/actions/workflows/check-and-test.yml) [![codecov](https://codecov.io/gh/compscidr/awm/branch/master/graph/badge.svg?token=84hTr5IfVQ)](https://codecov.io/gh/compscidr/awm)

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

## TODO:
- publish artifacts to gh releases or maven central
- publish sample app to google play store
- readthedocs
- codecov
- tests
- google maps plotting on server side
- google maps plotting on sample app
- user accounts to only see your own data
