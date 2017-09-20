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

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.utils.CacheHelper;
import com.swerly.wifiheatmap.utils.LoadCacheTask;
import com.swerly.wifiheatmap.utils.ShareBitmap;
import com.swerly.wifiheatmap.utils.StaticUtils;

import java.util.ArrayList;

/**
 * Created by Seth on 8/22/2017.
 *
 * Fragment to show a bitmap of a created heatmap
 */

public class FragmentView extends FragmentBase implements LoadCacheTask.CacheLoadCallback {
    private String subtitle;
    private String nameToView;
    private Bitmap bkgImage;
    private View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mainView = inflater.inflate(R.layout.fragment_view, container, false);

        SharedPreferences prefs = activityMain.getSharedPreferences(BaseApplication.PREFS, 0);
        Bundle arguments = getArguments();
        if (arguments.containsKey("name")){
            nameToView = arguments.getString("name");
            //save the index
            SharedPreferences.Editor prefEditor = prefs.edit();
            prefEditor.putString(BaseApplication.CURRENT_VIEW_NAME, nameToView);
            prefEditor.commit();
        } else {
            nameToView = prefs.getString(BaseApplication.CURRENT_VIEW_NAME, "null");
        }

        startLoadingSpinner(mainView);
        getList();

        return mainView;
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_share:
                //start the sharing
                new ShareBitmap(activityMain).execute(bkgImage);
                break;
            case R.id.action_help:
                //show the help view
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

    }

    @Override
    public void dataLoaded(String type, Object data) {
        ArrayList<HeatmapData> list = (ArrayList<HeatmapData>) data;
        setupImageToView(list);
    }

    private void getList(){
        //get the application (should have heatmap data)
        BaseApplication app = activityMain.getApp();
        //if the app is null (it shouldnt be), the loaded data is null, else its the loaded data
        ArrayList<HeatmapData> loadedHeatmaps = app == null ? null : app.getHeatmaps();

        //if the loaded heatmaps are null and current list is null, start loading
        if (loadedHeatmaps == null){
            showLoading();
            new LoadCacheTask(getActivity(), this).execute(CacheHelper.HEATMAP_LIST);
        } else {
            //if loaded heatmaps arent null, setup the view bkg image
            setupImageToView(loadedHeatmaps);
        }
    }

    private void setupImageToView(ArrayList<HeatmapData> list){
        if (list == null || list.isEmpty()){
            displayLoadError();
        }
        HeatmapData toView = null;
        for (HeatmapData current : list){
            if (current.getName().equals(nameToView)){
                toView = current;
            }
        }
        hideLoading();
        if (toView == null){
            displayLoadError();
        } else {
            bkgImage = toView.getFinishedImage();
            subtitle = toView.getName();
            setSubTitle(subtitle);
            //setup the gesture view with the bitmap
            GestureImageView bkgView = mainView.findViewById(R.id.view_view);
            bkgView.setImageBitmap(toView.getFinishedImage());
        }
    }

    private void displayLoadError(){
        activityMain.showErrorPopup();
    }
}
