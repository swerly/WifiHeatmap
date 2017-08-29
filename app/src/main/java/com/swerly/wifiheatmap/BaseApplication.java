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
    private boolean isEditing;
    private HeatmapPixel[][] currentPixels;


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

    public void setIsEditing(){
        this.isEditing = true;
    }

    public void setNotEditing(){
        this.isEditing = false;
    }

    public boolean isEditing(){
        return  this.isEditing;
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

    public void setCurrentInProgress(HeatmapData currentInProgress){
        this.currentInProgress = currentInProgress;
    }

    public void setCurrentInProgressFinished(Bitmap finishedHeatmap){
        checkCurrentNull();
        currentInProgress.setFinishedHeatmap(finishedHeatmap);
    }

    public void setCurrentInProgressName(String name){
        checkCurrentNull();
        currentInProgress.setName(name);
    }

    public void setCurrentPixels(HeatmapPixel[][] pixelsToSet){
        this.currentPixels = pixelsToSet;
    }

    public void finishCurrentInProgress(){
        if (heatmaps == null){
            heatmaps = new ArrayList<>();
        }
        if (!heatmaps.contains(currentInProgress)) {
            heatmaps.add(currentInProgress);
        }
        saveHeatmapList();
        cacheHelper.savePixels(new HeatmapPixelCacheObject(currentPixels, currentInProgress.getPixelsFileName()));
        resetCurrent();
    }

    public void setBackgroundInProgress(Bitmap bkg){
        checkCurrentNull();
        currentInProgress.setBackgroundImage(bkg);
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

    public void deleteFromList(HeatmapData toDelete){
        cacheHelper.deletePixels(toDelete.getPixelsFileName());
        heatmaps.remove(toDelete);
        saveHeatmapList();
    }

    private void saveHeatmapList(){
        cacheHelper.saveHeatmapList(heatmaps);
    }
}
