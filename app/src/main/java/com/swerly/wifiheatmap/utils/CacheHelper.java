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

package com.swerly.wifiheatmap.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.data.HeatmapPixelCacheObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Seth on 8/11/2017.
 */

public class CacheHelper {
    public static String HEATMAP_LIST = "heatmap_list";
    public static String HEATMAP_PIXEL = "heatmap_pixel_cache";

    private Context context;
    private LoadCacheTask.CacheLoadCallbacks loadCallbacks;

    public CacheHelper(Context context, LoadCacheTask.CacheLoadCallbacks loadCallbacks){
        this.context = context;
        this.loadCallbacks = loadCallbacks;
    }

    public void savePixels(HeatmapPixelCacheObject pixelsToCache){
        new SaveCacheTask().execute(pixelsToCache);
    }

    public void saveHeatmapList(ArrayList<HeatmapData> heatmapList){
        new SaveCacheTask().execute(heatmapList);
    }

    public void startupLoad(){
        new LoadCacheTask(context, loadCallbacks).execute(HEATMAP_LIST);
    }

    public void deletePixels(String pixelsToDelete){
        context.deleteFile(pixelsToDelete);
    }

    private class SaveCacheTask extends AsyncTask<Object, Void, Void>{

        @Override
        protected Void doInBackground(Object... inputObj) {
            String outputFile = "";
            Object objToSave = inputObj[0];
            //if the object we want to save is an arraylist then we are saving all heatmap data
            if (objToSave instanceof ArrayList<?>){
                outputFile = HEATMAP_LIST;
            } else if (objToSave instanceof HeatmapPixelCacheObject){
                outputFile = HEATMAP_PIXEL + ((HeatmapPixelCacheObject)objToSave).fName;
            }
            Log.d(BaseApplication.DEBUG_MESSAGE, "saving " + outputFile);
            FileOutputStream fos = null;
            try {
                fos = context.openFileOutput(outputFile, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(objToSave);
                oos.flush();
                fos.getFD().sync();
                fos.close();
                Log.d(BaseApplication.DEBUG_MESSAGE, "DONE saving " + outputFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
