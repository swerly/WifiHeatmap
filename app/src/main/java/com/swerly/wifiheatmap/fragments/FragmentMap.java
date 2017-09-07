/*
 * Copyright (c) 2017 Seth Werly.
 *
 * This file is part of WifiHeatmap.
 *
 *     WifiHeatmap is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     WifiHeatmap is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with WifiHeatmap.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.swerly.wifiheatmap.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.swerly.wifiheatmap.utils.LocationHelper;
import com.swerly.wifiheatmap.utils.MapController;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.utils.WifiHelper;
import com.swerly.wifiheatmap.views.SearchBarView;
import com.swerly.wifiheatmap.utils.StaticUtils;

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
        LocationHelper.LocationHelperCallback,
        SearchBarView.SearchBarCallback, WifiHelper.WifiConnectionChangeCallback, MapController.MapCreatedCallback {
    private String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private MapController mapController;
    private LocationHelper locationHelper;
    private SupportMapFragment mapFragment;
    private SearchBarView searchBarView;
    private View searchButtonView;
    private WifiHelper wifiHelper;
    private View noWifiView;
    private boolean wifiStatus, isPaused;

    public static FragmentHome newInstance(){
        return new FragmentHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setup the controllers and helpers
        mapController = new MapController(getActivity(), this);
        locationHelper = new LocationHelper(getActivity());
        wifiHelper = new WifiHelper(getContext());
        wifiStatus = wifiHelper.isWifiConnected();

        //setup the fragment for the map
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
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //setup the views
        //im getting lazy commenting this code i should have done it as i was writing it
        //ughghghghghgh
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchBarView = activityMain.findViewById(R.id.map_searchbar);
        setupSearchBar();
        noWifiView = view.findViewById(R.id.no_wifi_view);
        noWifiView.bringToFront();
        Button settingsBtn = noWifiView.findViewById(R.id.settings_button);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        //start listening for wifi connection changes
        wifiHelper.startListeningForWifiChanges(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        isPaused = false;
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubTitle(R.string.map_subtitle);

        wifiConnectionChange(wifiStatus);
    }

    @Override
    public void onPause(){
        super.onPause();
        isPaused = true;
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
                //setup the search view
                //do i really want to go through the rest of these files and ommcnet...
                if (searchButtonView == null){
                    searchButtonView = getActivity().findViewById(R.id.action_search);
                }

                if (searchBarView == null){
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.search_unavailable)
                            .content(R.string.search_unavailable_content)
                            .positiveText(R.string.ok)
                            .show();
                    return true;
                }
                if (!wifiStatus){
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.no_internet_title)
                            .content(R.string.no_internet_content)
                            .positiveText(R.string.ok)
                            .show();
                } else {
                    //animate open the search view
                    searchBarView.animateOpenFrom(searchButtonView);
                }
                break;
            case R.id.action_location:
                //if no internet display a dialog
                if (!wifiStatus){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.no_internet_title);
                    builder.setMessage(R.string.no_internet_location);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    //check permissions
                    if (EasyPermissions.hasPermissions(this.getActivity(), perms)) {
                        //has permissions, start trying to find location
                        startLocationRequest();
                    } else {
                        // Do not have permissions, request them now
                        EasyPermissions.requestPermissions(this, getString(R.string.location_rationale), 0, perms);
                    }
                }
                break;
            case R.id.action_help:
                //show the help
                activityMain.showHelp();
                break;
            default:
                break;
        }

        //return false so main activity can consume the up arrow event
        return false;
    }

    @Override
    public boolean onBackPressed() {
        return searchBarView.animateClose();
    }

    @Override
    public void onFabPressed() {
        //when the fab is pressed, close the search bar
        searchBarView.animateClose();
        //and request a snapshot from the map fragment
        mapController.requestSnapshot(app);
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
        //location was found, do stuff with it man im so lazy i should come back to these comments later
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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

    /**
     * start a location request
     */
    private void startLocationRequest(){
        if(!locationHelper.requestLocation(this)){
            Toast.makeText(getActivity(), getString(R.string.no_location), Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.location_finding), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void wifiConnectionChange(boolean wifiStatus) {
        //do things when a wifi connection change happened
        this.wifiStatus = wifiStatus;
        if (!isPaused) {
            if (wifiStatus) {
                hideNoWifiView();
            } else {
                showNoWifiView();
            }
        }
    }

    /**
     * hide the no wifi view and show the google map fragment
     */
    private void hideNoWifiView(){
        noWifiView.setVisibility(View.GONE);
        activityMain.showFab();

        getChildFragmentManager()
                .beginTransaction()
                .show(mapFragment)
                .commit();
    }

    /**
     * show the no wifi view and hide the google map fragment
     */
    private void showNoWifiView(){
        noWifiView.setVisibility(View.VISIBLE);
        activityMain.hideHelp();
        //start the no wifi spinning drawable animation
        ImageView loadingIcon = noWifiView.findViewById(R.id.no_wifi_spinner);
        Drawable spinner = loadingIcon.getDrawable();
        if (spinner instanceof Animatable){
            ((Animatable) spinner).start();
        }
        activityMain.hideFab();

        getChildFragmentManager()
                .beginTransaction()
                .hide(mapFragment)
                .commit();
    }

    @Override
    public void mapCreated() {
        noWifiView.bringToFront();
    }

    private void setupSearchBar(){
        if(searchBarView == null) {
            searchBarView = activityMain.findViewById(R.id.map_searchbar);
        }
        if (searchBarView == null){
            return;
        }
        searchBarView.setSearchBarCallback(this);
        searchBarView.setToolbarId(R.id.main_toolbar);
    }
}
