package com.swerly.wifiheatmap;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.data.HeatmapPixel;
import com.swerly.wifiheatmap.data.HeatmapPixelCacheObject;
import com.swerly.wifiheatmap.utils.CacheHelper;

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
    private HeatmapPixel[][] currentPixels;
    private int indexToView;


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

    public void setIndexToView(int toView){
        this.indexToView = toView;
    }

    public int getIndexToView(){
        return indexToView;
    }

    public void setCurrentInProgressPixels(HeatmapPixel[][] newPixels){
        currentPixels = newPixels;
        cacheHelper.savePixels(new HeatmapPixelCacheObject(newPixels, CacheHelper.HEATMAP_IN_PROGRESS));
    }

    public void setCurrentInProgressFinished(Bitmap finishedHeatmap){
        checkCurrentNull();
        currentInProgress.setFinishedHeatmap(finishedHeatmap);
    }

    public void setCurrentInProgressName(String name){
        checkCurrentNull();
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
        checkCurrentNull();
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

    private void checkCurrentNull(){
        if (currentInProgress == null){
            currentInProgress = new HeatmapData();
        }
    }

    public void deleteInProgress(){
        cacheHelper.deleteInProgress();
    }

    private void saveInProgress(){
        cacheHelper.saveInProgress(currentInProgress);
        cacheHelper.savePixels(new HeatmapPixelCacheObject(currentPixels, currentInProgress.getPixelsFileName()));
    }

    private void saveHeatmapList(){
        cacheHelper.saveHeatmapList(heatmaps);
    }
}
