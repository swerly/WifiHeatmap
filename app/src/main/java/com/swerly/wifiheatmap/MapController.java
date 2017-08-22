package com.swerly.wifiheatmap;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Seth on 7/9/2017.
 */

public class MapController implements OnMapReadyCallback, GeocodingHelper.GeocodingResultCallback {
    private static final int MAX_ZOOM = 19;
    private GoogleMap mMap;
    private Context context;


    public MapController(Context context){
        this.context = context;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
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
        mMap.getUiSettings().setCompassEnabled(false);
    }

    public void setUserLocation(LatLng latlng){
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                latlng, MAX_ZOOM);
        mMap.animateCamera(location);
    }

    private void setZoomLevel(float zoom){
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    private void moveCameraTo(LatLng latlng){
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
    }

    public void performSearch(String searchString){
        if (searchString != null && !searchString.equals("")) {

            GeocodingHelper gh = new GeocodingHelper(this, context);
            gh.requestLatlngFromSearch(searchString);
        }
    }

    public static GoogleMapOptions getMapOptions(){
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .tiltGesturesEnabled(false);
        return options;
    }

    @Override
    public void gotLatlng(LatLng latLng, String resultMsg) {
        Toast.makeText(context, resultMsg, Toast.LENGTH_SHORT)
                .show();
        if (latLng != null) {
            setUserLocation(latLng);
        }
    }

    public void requestSnapshot(GoogleMap.SnapshotReadyCallback callback){
        mMap.snapshot(callback);
    }
}
