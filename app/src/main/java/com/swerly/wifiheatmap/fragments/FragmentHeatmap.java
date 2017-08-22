package com.swerly.wifiheatmap.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        HeatmapView.HeatmapCacherCallback {
    private ImageView bkgView;
    private HeatmapView heatmapView;
    private WifiHelper wifiHelper;
    private View noWifiView;

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
        heatmapView.setCacherCallback(this);
        noWifiView = view.findViewById(R.id.no_wifi_view);

        wifiHelper = new WifiHelper(getContext());

        if (app.isBackgroundReady()){
            setBackground();
        } else {
            new SnapshotWaiter(app, this).startWaiting();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        actionBarHelper.setupForFragment(this, menu, inflater);
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
        //dont need to do anything because fab helper takes care of it
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubTitle(R.string.heatmap_subtitle);
        wifiHelper.startListeningForWifiChanges(this);
        heatmapView.startListeningForLevelChanges();
        doWifiCheck();
    }

    @Override
    public void onPause(){
        super.onPause();
        wifiHelper.stopListeningForWifiChanges();
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
        if (wifiStatus){
            hideNoWifiView();
        } else {
            showNoWifiView();
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
    }

    private void showNoWifiView(){
        noWifiView.setVisibility(View.VISIBLE);
        bkgView.setVisibility(View.GONE);
        heatmapView.setVisibility(View.GONE);
    }

    @Override
    public void pointsChanged(HeatmapPixel[][] newPoints) {
        app.setCurrentInProgressPixels(newPoints);
    }
}
