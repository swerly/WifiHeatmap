package com.swerly.wifiheatmap;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Seth on 7/3/2017.
 */

public class BaseApplication extends Application implements
        GoogleMap.SnapshotReadyCallback{
    public final static String DEBUG_MESSAGE = "HeatmapDebug";
    private static Context context;

    private ArrayList<HeatmapData> heatmaps;
    private CacheHelper cacheHelper;
    private HeatmapData currentInProgress;
    private boolean backgroundReady;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        cacheHelper = new CacheHelper(this, null);
        backgroundReady = false;
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        setBackgroundInProgress(bitmap);
        setBackgroundReady();
    }

    public void setLoadedList(ArrayList<HeatmapData> loadedList){
        if (loadedList == null){
            heatmaps = new ArrayList<>();
        } else {
            heatmaps = loadedList;
        }
    }

    public void setLoadedInProgress(HeatmapData loadedInProgress){
        this.currentInProgress = loadedInProgress;
    }

    public static Context getContext(){
        return context;
    }

    public HeatmapData getCurrentInProgress(){
        return currentInProgress;
    }

    public ArrayList<HeatmapData> getHeatmaps(){
        return heatmaps;
    }

    public void setCurrentInProgressPixels(HeatmapPixel[][] newPixels){
        currentInProgress.setPixels(newPixels);
        saveInProgress();
    }

    public void setCurrentInProgressFinished(Bitmap finishedHeatmap){
        currentInProgress.setFinishedHeatmap(finishedHeatmap);
    }

    public void setCurrentInProgressName(String name){
        currentInProgress.setName(name);
    }

    public void finishCurrentInProgress(){
        if (heatmaps == null){
            heatmaps = new ArrayList<>();
        }
        heatmaps.add(currentInProgress);
        resetCurrent();
        saveHeatmapList();
        deleteInProgress();
    }

    public void setBackgroundInProgress(Bitmap bkg){
        if (currentInProgress == null){
            currentInProgress = new HeatmapData();
        }
        currentInProgress.setBackgroundImage(bkg);
        saveInProgress();
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

    public void deleteInProgress(){
        cacheHelper.deleteInProgress();
    }

    private void saveInProgress(){
        cacheHelper.saveInProgress(currentInProgress);
    }

    private void saveHeatmapList(){
        cacheHelper.saveHeatmapList(heatmaps);
    }
}
