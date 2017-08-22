package com.swerly.wifiheatmap.activities;

import android.content.Intent;
import android.os.Bundle;

import com.swerly.wifiheatmap.utils.CacheHelper;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.data.HeatmapPixelCacheObject;

import java.util.ArrayList;

/**
 * Created by Seth on 8/13/2017.
 */

public class SplashActivity extends ActivityBase implements CacheHelper.CacheLoadCallbacks {
    private boolean listLoaded;
    private boolean inProgressLoaded;

    private CacheHelper cacheHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cacheHelper = new CacheHelper(this, this);
        cacheHelper.startupLoad();
    }

    @Override
    public void heatmapListLoaded(ArrayList<HeatmapData> data) {
        listLoaded = true;
        app.setLoadedList(data);
        checkIfLoaded();
    }

    @Override
    public void heatmapInProgressLoaded(HeatmapData inProgress) {
        inProgressLoaded = true;
        app.setLoadedInProgress(inProgress);
        checkIfLoaded();
    }

    private void checkIfLoaded(){
        if (listLoaded && inProgressLoaded){
            Intent intent = new Intent(this, ActivityMain.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void heatmapPixelsLoaded(HeatmapPixelCacheObject pixels) {
        //dont need to load pixels until the user wants to edit a map
    }
}
