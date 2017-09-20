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

package com.swerly.wifiheatmap.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.fragments.FragmentBase;
import com.swerly.wifiheatmap.fragments.FragmentHeatmap;
import com.swerly.wifiheatmap.utils.CacheHelper;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.data.HeatmapPixelCacheObject;
import com.swerly.wifiheatmap.utils.LoadCacheTask;

import java.util.ArrayList;

/**
 * Created by Seth on 8/13/2017.
 *
 * A splash screen to show to the user while we do an initial load of the heatmap data
 */

public class SplashActivity extends ActivityBase implements LoadCacheTask.CacheLoadCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new LoadCacheTask(this, this).execute(CacheHelper.HEATMAP_LIST);
    }

    @Override
    public void dataLoaded(String type, Object data) {
        //set the last viewed fragment to be home
        SharedPreferences.Editor prefEditor = getSharedPreferences(BaseApplication.PREFS, 0).edit();
        prefEditor.putString(BaseApplication.LAST_FRAG_PREF, FragmentBase.HOME_FRAGMENT);
        prefEditor.commit();

        //set the heatmap list
        app.setLoadedList((ArrayList<HeatmapData>)data);

        //start the main activity
        Intent intent = new Intent(this, ActivityMain.class);
        startActivity(intent);
        finish();
    }
}
