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

        ImageView loadingIcon = view.findViewById(R.id.loading_spinner);
        Drawable spinner = loadingIcon.getDrawable();
        if (spinner instanceof Animatable){
            ((Animatable) spinner).start();
        }

        heatmapCouldntLoadView = view.findViewById(R.id.could_not_load_view);
        Button settingsBtn = noWifiView.findViewById(R.id.settings_button);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        wifiHelper = new WifiHelper(getContext());

        if (app.isBackgroundReady()){
            setBackground();
        } else {
            new SnapshotWaiter(app, this).startWaiting();
        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
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
        app.setCurrentPixels(heatmapView.getHeatmapPixels());
    }

    @Override
    public void onStart(){
        super.onStart();
        wifiHelper.startListeningForWifiChanges(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        wifiHelper.stopListeningForWifiChanges();
    }

    @Override
    public void onResume(){
        super.onResume();
        isPaused = false;
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubTitle(R.string.heatmap_subtitle);
        heatmapView.startListeningForLevelChanges();
        doWifiCheck();
    }

    @Override
    public void onPause(){
        super.onPause();
        isPaused = true;
        heatmapView.stopListeningForLevelChanges();
    }

    @Override
    public void snapshotReady() {
        setBackground();
    }

    @Override
    public void heatmapLoadingDone(boolean status) {
        if (status){
            heatmapLoadContainer.setVisibility(View.GONE);
            heatmapLoadingView.setVisibility(View.GONE);
            editShowFab = true;
        } else {
            heatmapLoadingView.setVisibility(View.GONE);
            heatmapCouldntLoadView.setVisibility(View.VISIBLE);
        }
        showHideFab();
    }

    private void setBackground(){
        Bitmap bkgToSet = app.getCurrentInProgress().getBackgroundImage();
        bkgView.setImageBitmap(bkgToSet);
    }

    @Override
    public void wifiConnectionChange(boolean wifiStatus) {
        if (!isPaused) {
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
            wifiHelper.setupWifi();
        }
    }

    private void hideNoWifiView(){
        noWifiView.setVisibility(View.GONE);
        bkgView.setVisibility(View.VISIBLE);
        heatmapView.setVisibility(View.VISIBLE);
        activityMain.showFab();
        showHideFab();
        wifiShowFab = true;
    }

    private void showNoWifiView(){
        noWifiView.setVisibility(View.VISIBLE);
        activityMain.hideHelp();
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

    private void showHideFab(){
        if (wifiShowFab && editShowFab){
            activityMain.showFab();
        } else {
            activityMain.hideFab();
        }
    }
}
