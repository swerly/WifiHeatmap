# WifiHeatmap

The app can be found on the Google Play Store [here.](https://play.google.com/store/apps/details?id=com.swerly.wifiheatmap)

Use Google Maps to find the location of interest, then map the Wifi connectivity by walking!

Steps:
- Get a top down view of desired location using Google Maps.
- Position the location of interest as large as possible within the phone screen.
- Walk around with your finger on the screen following your movements throughout the location.
- Watch as a heatmap is created that shows the strength of the Wifi signal.


Note: You must be able to walk around the building you are mapping to get an accurate heatmap.

Build Instructions
------------------
To build this program, you must have API keys for both Google Geocoding services and Google Maps.
These keys go into the file `app\src\main\res\values\keys.xml` and are formatted as follows:
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="geocode_api_key">GEOCODE_API_KEY_GOES_HERE</string>
    <string name="maps_api_key">MAPS_API_KEY_GOES_HERE</string>
</resources>
```

License
-------
Copyright (C) 2017  Seth Werly

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
