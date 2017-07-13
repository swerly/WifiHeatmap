package com.swerly.wifiheatmap;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Seth on 7/9/2017.
 */

public class MapController implements OnMapReadyCallback {
    private static final int MAX_ZOOM = 17;
    private GoogleMap mMap;

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        initializeMap();
    }

    private void initializeMap(){
        LatLng usa = new LatLng(37, -100);
        moveCameraTo(usa);
    }

    public void setUserLocation(LatLng latlng){
        moveCameraTo(latlng);
        setZoomLevel(MAX_ZOOM);
    }

    private void setZoomLevel(float zoom){
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    private void moveCameraTo(LatLng latlng){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
    }

    public static GoogleMapOptions getMapOptions(){
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .tiltGesturesEnabled(false);
        return options;
    }
}
