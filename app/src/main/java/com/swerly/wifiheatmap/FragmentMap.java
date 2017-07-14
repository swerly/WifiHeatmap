package com.swerly.wifiheatmap;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Seth on 7/6/2017.
 *
 * Allows the user to use google maps to find the location of the building they want to
 * create a wifi heatmap in
 */

public class FragmentMap extends FragmentBase implements
        EasyPermissions.PermissionCallbacks,
        LocationHelperCallback,
        SearchBarView.SearchBarCallback{
    private String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private MapController mapController;
    private LocationHelper locationHelper;
    private SupportMapFragment mapFragment;
    private SearchBarView searchBarView;

    public static FragmentHome newInstance(){
        return new FragmentHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapController = new MapController(getActivity());
        locationHelper = new LocationHelper(getActivity());
        searchBarView = getActivity().findViewById(R.id.map_searchbar);
        searchBarView.setSearchBarCallback(this);
        searchBarView.setToolbarId(R.id.main_toolbar);

        mapFragment = SupportMapFragment.newInstance(MapController.getMapOptions());
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.map_main_layout, mapFragment)
                .commit();
        mapFragment.getMapAsync(mapController);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStop(){
        super.onStop();
        locationHelper.stopLocationUpdates();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                searchBarView.animateOpenFrom(searchBarView);
                break;
            case R.id.action_location:
                if(EasyPermissions.hasPermissions(this.getActivity(), perms)){
                    //has permissions, start trying to find location
                    startLocationRequest();
                } else {
                    // Do not have permissions, request them now
                    EasyPermissions.requestPermissions(this, getString(R.string.location_rationale), 0, perms);
                }
                break;
            case R.id.action_help:
                Toast.makeText(activityMain, "Help Pressed", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        //return false so main activity can consume the up arrow event
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //let user know location permission was granted
        Toast.makeText(activityMain, getString(R.string.location_granted), Toast.LENGTH_SHORT)
                .show();
        //start the location request
        //has permissions, start trying to find location
        startLocationRequest();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //if permission is permanently denied, let user know and prompt to open settings
            new AppSettingsDialog.Builder(this).build().show();
        }
        else {
            //else just display a toast letting them know it was denied
            Toast.makeText(activityMain, getString(R.string.location_denied), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void gotLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d(BaseApplication.DEBUG_MESSAGE, latLng.toString());
        mapController.setUserLocation(latLng);
    }

    @Override
    public void performSearch(String searchText) {
        mapController.performSearch(searchText);
    }

    @Override
    public void returnFromSettings() {
        locationHelper.returnFromSettings();
    }

    private void startLocationRequest(){
        if(!locationHelper.requestLocation(this)){
            Toast.makeText(getActivity(), getString(R.string.no_location), Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.location_finding), Toast.LENGTH_LONG)
                    .show();
        }
    }


}
