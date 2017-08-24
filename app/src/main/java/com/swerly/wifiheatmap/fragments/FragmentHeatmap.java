package com.swerly.wifiheatmap.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
        WifiHelper.WifiConnectionChangeCallback {
    private ImageView bkgView;
    private HeatmapView heatmapView;
    private WifiHelper wifiHelper;
    private View noWifiView;
    private boolean isPaused;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_heatmap, container, false);
        bkgView = view.findViewById(R.id.heatmap_bkg_view);
        heatmapView = view.findViewById(R.id.heatmap_view);
        noWifiView = view.findViewById(R.id.no_wifi_view);
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
    }

    private void showNoWifiView(){
        noWifiView.setVisibility(View.VISIBLE);
        bkgView.setVisibility(View.GONE);
        heatmapView.setVisibility(View.GONE);
        activityMain.hideFab();
    }
}
