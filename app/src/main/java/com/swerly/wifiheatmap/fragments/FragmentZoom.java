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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.utils.SnapshotWaiter;
import com.swerly.wifiheatmap.utils.StaticUtils;

/**
 * Created by Seth on 8/10/2017.
 *
 * Fragment to control zooming in on a background image (since google maps limits the amount you can view)
 */

public class FragmentZoom extends FragmentBase implements
        SnapshotWaiter.SnapshotReadyCallback {

    private GestureImageView bkgView;
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
        bkgView.getController().getSettings()
                .setRotationEnabled(true);

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
        app.setBackgroundInProgress(bkg);
        app.setBackgroundReady();
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubTitle(R.string.zoom_subtitle);

        //if the background from the google map snapshot isnt ready wait for it
        if (app.isBackgroundReady()){
            setBackground();
        } else {
            new SnapshotWaiter(app, this).startWaiting();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        bkgSet = false;
    }

    @Override
    public void snapshotReady() {
        setBackground();
    }

    private void setBackground(){
        if (!bkgSet) {
            Bitmap bkgToSet = app.getCurrentInProgress().getBackgroundImage();
            bkgView.setImageBitmap(bkgToSet);
            bkgSet = true;
        }
    }
}
