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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
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

import com.swerly.wifiheatmap.data.HeatmapPixel;
import com.swerly.wifiheatmap.views.HeatmapView;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.utils.SnapshotWaiter;
import com.swerly.wifiheatmap.utils.WifiHelper;

/**
 * Created by Seth on 7/6/2017.
 *
 * Fragment used to draw the heatmaps
 */

public class FragmentHeatmap extends FragmentBase implements
        SnapshotWaiter.SnapshotReadyCallback,
        WifiHelper.WifiConnectionChangeCallback,
        HeatmapView.HeatmapLoadingDone{
    private ImageView bkgView;
    private HeatmapView heatmapView;
    private WifiHelper wifiHelper;
    private View noWifiView;
    private View heatmapLoadingView;
    private View heatmapCouldntLoadView;
    private View heatmapLoadContainer;
    private boolean isPaused, wifiShowFab, editShowFab;
    private String toLoad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey("toLoad")) {
            toLoad = args.getString("toLoad");
            activityMain.hideHelp();
        } else {
            toLoad = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //setup views
        View view = inflater.inflate(R.layout.fragment_heatmap, container, false);
        bkgView = view.findViewById(R.id.heatmap_bkg_view);
        heatmapView = view.findViewById(R.id.heatmap_view);
        heatmapView.setToLoad(toLoad, this);
        editShowFab = false;
        noWifiView = view.findViewById(R.id.no_wifi_view);
        heatmapLoadContainer = view.findViewById(R.id.heatmap_load_container);
        heatmapLoadingView = view.findViewById(R.id.heatmap_currently_loading);
        heatmapCouldntLoadView = view.findViewById(R.id.could_not_load_view);

        //start the spinning of the loading icon
        ImageView loadingIcon = view.findViewById(R.id.loading_spinner);
        Drawable spinner = loadingIcon.getDrawable();
        if (spinner instanceof Animatable){
            ((Animatable) spinner).start();
        }

        //setup the button to go to settings if no wifi is enabled
        Button settingsBtn = noWifiView.findViewById(R.id.settings_button);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        wifiHelper = new WifiHelper(getContext());

        //if the background image is ready, set it
        if (app.isBackgroundReady()){
            setBackground();
        }
        //else create a function on a timer that will check to see when the background image is ready
        else {
            new SnapshotWaiter(app, this).startWaiting();
        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //show help screen when its selected
            case R.id.action_help:
                activityMain.showHelp();
                break;
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onFabPressed() {
        //set and save the current pixels when the fab is pressed
        app.setCurrentPixels(heatmapView.getHeatmapPixels());
    }

    @Override
    public void onStart(){
        super.onStart();
        //start a broadcast listener to listen for wifi connection changes
        wifiHelper.startListeningForWifiChanges(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        //stop the broadcast listener, dont need updates when the fragment isnt active
        wifiHelper.stopListeningForWifiChanges();
    }

    @Override
    public void onResume(){
        super.onResume();
        isPaused = false;
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set the subtitle
        setSubTitle(R.string.heatmap_subtitle);
        //start listening for wifi level changes
        heatmapView.startListeningForLevelChanges();
        doWifiCheck();
    }

    @Override
    public void onPause(){
        super.onPause();
        isPaused = true;
        //dont need to listen for level changes anymore
        heatmapView.stopListeningForLevelChanges();
    }

    @Override
    public void snapshotReady() {
        // the background is now ready so we can set it
        setBackground();
    }

    @Override
    public void heatmapLoadingDone(boolean status) {
        //if the heatmap was successfully loaded
        if (status){
            // hide the loading view
            heatmapLoadContainer.setVisibility(View.GONE);
            heatmapLoadingView.setVisibility(View.GONE);
            editShowFab = true;
        }
        //else hide the loading view and display the error view
        else {
            heatmapLoadingView.setVisibility(View.GONE);
            heatmapCouldntLoadView.setVisibility(View.VISIBLE);
        }
        //decide what to do with the fab
        showHideFab();
    }

    /**
     * sets the background image
     */
    private void setBackground(){
        //get the current background from the app and display it
        Bitmap bkgToSet = app.getCurrentInProgress().getBackgroundImage();
        bkgView.setImageBitmap(bkgToSet);
    }

    @Override
    public void wifiConnectionChange(boolean wifiStatus) {
        //dont do anything if we're paused
        if (!isPaused) {
            //show / hide the no wifi view depending on the status
            if (wifiStatus) {
                hideNoWifiView();
            } else {
                showNoWifiView();
            }
        }
    }

    private void doWifiCheck(){
        if (wifiHelper.wifiEnabledAndConnected()){
            hideNoWifiView();
        } else {
            showNoWifiView();
        }
    }

    /**
     * sets up the fragment views when hiding the no wifi view
     */
    private void hideNoWifiView(){
        noWifiView.setVisibility(View.GONE);
        bkgView.setVisibility(View.VISIBLE);
        heatmapView.setVisibility(View.VISIBLE);
        activityMain.showFab();
        showHideFab();
        wifiShowFab = true;
    }

    /**
     * shows the no wifi view when wifi isn't enabled
     */
    private void showNoWifiView(){
        noWifiView.setVisibility(View.VISIBLE);
        activityMain.hideHelp();

        //start the loading spinner
        ImageView loadingIcon = noWifiView.findViewById(R.id.no_wifi_spinner);
        Drawable spinner = loadingIcon.getDrawable();
        if (spinner instanceof Animatable){
            ((Animatable) spinner).start();
        }
        bkgView.setVisibility(View.GONE);
        heatmapView.setVisibility(View.GONE);
        showHideFab();
        wifiShowFab = false;
    }

    /**
     * decide what to do with the fab
     */
    private void showHideFab(){
        //if wifi is enabled and heatmap to edit is done loading show the fab
        if (wifiShowFab && editShowFab){
            activityMain.showFab();
        } else {
            activityMain.hideFab();
        }
    }
}
