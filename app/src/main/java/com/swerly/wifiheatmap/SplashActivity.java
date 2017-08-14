package com.swerly.wifiheatmap;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Seth on 8/13/2017.
 */

public class SplashActivity extends ActivityBase implements CacheHelper.CacheLoadCallbacks {
    private boolean listLoaded;
    private boolean countLoaded;
    private boolean inProgressLoaded;

    private CacheHelper cacheHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cacheHelper = new CacheHelper(this, this);
        cacheHelper.startCountLoad();
    }

    @Override
    public void heatmapListLoaded(ArrayList<HeatmapData> data) {
        listLoaded = true;
        //TODO: save to appdata
        checkIfLoaded();
    }

    @Override
    public void heatmapCountLoaded(int count) {
        countLoaded = true;
        app.setCurrentCount(count);
        checkIfLoaded();
    }

    @Override
    public void heatmapInProgressLoaded(HeatmapData inProgress) {
        inProgressLoaded = true;
        //TODO: save to appdata
        checkIfLoaded();
    }

    private void checkIfLoaded(){
        if (countLoaded){
            Intent intent = new Intent(this, ActivityMain.class);
            startActivity(intent);
            finish();
        }
    }
}
