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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.utils.CacheHelper;
import com.swerly.wifiheatmap.utils.LoadCacheTask;
import com.swerly.wifiheatmap.utils.SnapshotWaiter;
import com.swerly.wifiheatmap.utils.StaticUtils;

/**
 * Created by Seth on 8/10/2017.
 *
 * Fragment to control zooming in on a background image (since google maps limits the amount you can view)
 */

public class FragmentZoom extends FragmentBase implements
        SnapshotWaiter.SnapshotReadyCallback,
        LoadCacheTask.CacheLoadCallback {

    private GestureImageView bkgView;
    private Bitmap bkg;
    private boolean bkgSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_zoom, container, false);
        bkgView = view.findViewById(R.id.gesture_view);
        //set the gesture view so we can rotate the image
        bkgView.getController().getSettings()
                .setRotationEnabled(true);

        startLoadingSpinner(view);

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
        //save the current view as the apps current heatmap background
        Bitmap bkg = StaticUtils.getScreenShot(bkgView);
        activityMain.onSnapshotReady(bkg);
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubTitle(R.string.zoom_subtitle);

        handleBackground();
    }

    @Override
    public void onPause(){
        super.onPause();
        bkgSet = false;
    }

    /**
     * callback when we are waiting for the data to save
     */
    @Override
    public void snapshotReady() {
        Bitmap bkg = activityMain.getApp().getBkgInProgress();
        if (bkg != null){
            setBackground(bkg);
        } else {
            activityMain.showErrorPopup();
            Log.d(BaseApplication.DEBUG_MESSAGE, "zoom frag ERROR BKG IN PROG NULL");
        }
    }

    /**
     * callback when data is loaded from cache
     * @param type type of data that was loaded
     * @param data data that was loaded
     */
    @Override
    public void dataLoaded(String type, Object data) {
        if (data != null){
            Bitmap bkg = (Bitmap) data;
            activityMain.onSnapshotReady(bkg);
            setBackground(bkg);
        } else {
            activityMain.showErrorPopup();
            Log.d(BaseApplication.DEBUG_MESSAGE, "zoom frag ERROR LOADING DATA");
        }
    }

    private void handleBackground(){
        BaseApplication app = activityMain.getApp();
        //if the background from the google map snapshot isnt ready wait for it
        bkg = app == null ? null : app.getBkgInProgress();

        //if the bkg is null but we are currently in the process of saving it
        if (bkg == null && activityMain.isSavingBkg()){
            Log.d(BaseApplication.DEBUG_MESSAGE, "zoom frag currently saving...");
            //wait for the snapshot to save
            new SnapshotWaiter(activityMain, this).startWaiting();
            showLoading();
        }
        //if the bkg is null and we are NOT in the process of saving it
        else if (bkg == null && !activityMain.isSavingBkg()){
            Log.d(BaseApplication.DEBUG_MESSAGE, "zoom frag starting load of bkg...");
            //start loading the snapshot
            new LoadCacheTask(activityMain, this).execute(CacheHelper.BKG_IN_PROGRESS);
            showLoading();
        }
        //else if the bkg isn't null, we can set it
        else if (bkg != null){
            Log.d(BaseApplication.DEBUG_MESSAGE, "zoom frag bkg already saved and loaded");
            setBackground(bkg);
        }
    }

    private void setBackground(Bitmap bkg){
        hideLoading();
        if (!bkgSet) {
            bkgView.setImageBitmap(bkg);
        }
    }
}
