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
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.data.HeatmapPixel;
import com.swerly.wifiheatmap.data.HeatmapPixelCacheObject;
import com.swerly.wifiheatmap.utils.CacheHelper;

import java.util.ArrayList;

/**
 * Created by Seth on 7/3/2017.
 */

public class BaseApplication extends Application{
    public final static String DEBUG_MESSAGE = "HeatmapDebug";
    public final static String PREFS = "WifiHeatmapPrefs";
    public final static String LAST_FRAG_PREF = "lastFragmentOpened";
    public final static String CURRENT_VIEW_NAME = "currentViewName";
    public final static String IS_FIRST_RUN = "isFirstRun";
    public final static String DONT_ASK_RATING = "dontAskRating";
    private static Context context;

    public static Context getContext(){
        return context;
    }

    private ArrayList<HeatmapData> heatmaps;
    private Bitmap bkgInProgress;
    private CacheHelper cacheHelper;
    private HeatmapPixel[][] currentPixels;
    private HeatmapData dataToEdit;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        cacheHelper = new CacheHelper(this);
    }

    /**
     * sets the current heatmap list when we load it from a cache
     * @param loadedList list that has just been loaded
     */
    public void setLoadedList(ArrayList<HeatmapData> loadedList){
        if (loadedList == null){
            heatmaps = new ArrayList<>();
        } else {
            heatmaps = loadedList;
        }
    }

    /**
     * Updates the heatmap list if it has been modified
     * @param newList the new modified list that we want to save
     */
    public void setModifiedList(ArrayList<HeatmapData> newList){
        heatmaps = newList;
        checkCacheHelper();
        cacheHelper.saveHeatmapList(newList);
    }

    /**
     * Returns the current list of heatmaps
     * @return current list of heatmaps
     */
    public ArrayList<HeatmapData> getHeatmaps(){
        return heatmaps;
    }


    /**
     * stores and saves the current background bitmap
     * @param bkg bitmap to save/set
     */
    public void saveBkgInProgress(Bitmap bkg){
        bkgInProgress = bkg;
        checkCacheHelper();
        cacheHelper.saveBkgInProgress(bkg);
    }

    /**
     * deletes the current in progress bkg so we aren't caching unnecessary data when we dont need to
     */
    public void deleteInProgressBkg(){
        cacheHelper.deleteInProgressBkg();
    }

    /**
     * Gets the current background bitmap
     * @return bitmap of the current background
     */
    public Bitmap getBkgInProgress(){
        return bkgInProgress;
    }

    /**
     * set the current array of heatmap pixels
     * @param pixelsToSet pixels to set
     */
    public void setCurrentPixels(HeatmapPixel[][] pixelsToSet){
        this.currentPixels = pixelsToSet;
    }

    /**
     * set the edited data when a user wants to edit a heatmap
     * @param dataToEdit data that user wants to edit
     */
    public void setDataToEdit(HeatmapData dataToEdit){
        this.dataToEdit = dataToEdit;
        bkgInProgress = dataToEdit.getBackgroundImage();
    }

    /**
     * resets the editing data to null
     */
    public void resetDataToEdit(){
        this.dataToEdit = null;
        bkgInProgress = null;
    }

    /**
     * gets the data that is currently being edited
     * @return the data that is currently being edited
     */
    public HeatmapData getDataToEdit(){
        return dataToEdit;
    }

    public void addNewHeatmap(HeatmapData newData){
        if (heatmaps == null){
            heatmaps = new ArrayList<>();
        }
        heatmaps.add(newData);
        cacheHelper.saveHeatmapList(heatmaps);
        cacheHelper.savePixels(new HeatmapPixelCacheObject(currentPixels, newData.getPixelsFileName()));
    }

    public void saveEditedHeatmap(Bitmap finishedHeatmap){
        dataToEdit.setFinishedHeatmap(finishedHeatmap);
        checkCacheHelper();
        cacheHelper.saveHeatmapList(heatmaps);
        cacheHelper.savePixels(new HeatmapPixelCacheObject(currentPixels, dataToEdit.getPixelsFileName()));
        dataToEdit = null;
    }

    /*
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
    }*/

    /**
     * Makes sure the cache helper isn't null before we want to use it
     */
    private void checkCacheHelper(){
        if (cacheHelper == null){
            cacheHelper = new CacheHelper(this);
        }
    }
}
