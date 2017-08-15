package com.swerly.wifiheatmap;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Seth on 7/3/2017.
 */

public class BaseApplication extends Application implements
        GoogleMap.SnapshotReadyCallback{
    public final static String DEBUG_MESSAGE = "HeatmapDebug";
    private static Context context;

    private CacheHelper cacheHelper;
    private int currentCount;
    private HeatmapData currentInProgress;
    private boolean backgroundReady;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        cacheHelper = new CacheHelper(this, null);
        backgroundReady = false;
    }

    public static Context getContext(){
        return context;
    }

    public void setCurrentCount(int count){
        this.currentCount = count;
    }

    public int getCurrentCount(){
        return currentCount;
    }

    public void incrementCount(){
        currentCount++;
        cacheHelper.saveCount(currentCount);
    }

    public HeatmapData getCurrentInProgress(){
        return currentInProgress;
    }

    public void setBackgroundInProgress(Bitmap bkg){
        if (currentInProgress == null){
            currentInProgress = new HeatmapData();
        }
        currentInProgress.setBackgroundImage(bkg);
        saveInProgress();
    }

    private void saveInProgress(){
        cacheHelper.saveInProgress(currentInProgress);
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        setBackgroundInProgress(bitmap);
        setBackgroundReady();
    }

    public boolean isBackgroundReady(){
        return backgroundReady;
    }

    public void setBackgroundReady(){
        backgroundReady = true;
    }

    public void resetCurrent(){
        currentInProgress = null;
        backgroundReady = false;
    }
}
