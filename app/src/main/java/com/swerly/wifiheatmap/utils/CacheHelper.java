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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.data.HeatmapPixelCacheObject;
import com.swerly.wifiheatmap.data.ProxyBitmap;

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
    public static String BKG_IN_PROGRESS = "bkg_in_progress";

    private Context context;

    public CacheHelper(Context context){
        this.context = context;
    }

    /**
     * Saves a list of heatmaps to the cache
     * @param heatmapList list to save to cache
     */
    public void saveHeatmapList(ArrayList<HeatmapData> heatmapList){
        new SaveCacheTask().execute(heatmapList);
    }

    /**
     * Deletes a list of pixels from the cache
     * @param pixelsToDelete filename of pixels to delete
     */
    public void deletePixels(String pixelsToDelete){
        context.deleteFile(pixelsToDelete);
    }

    public void savePixels(HeatmapPixelCacheObject toSave){
        new SaveCacheTask().execute(toSave);
    }

    public void saveBkgInProgress(Bitmap bkg){
        new SaveCacheTask().execute(bkg);
    }

    public void deleteInProgressBkg(){
        context.deleteFile(BKG_IN_PROGRESS);
    }

    public class SaveCacheTask extends AsyncTask<Object, Void, Void>{

        @Override
        protected Void doInBackground(Object... inputObj) {
            String outputFile = "";
            Object objToSave = inputObj[0];
            //if the object we want to save is an arraylist then we are saving all heatmap data
            if (objToSave instanceof ArrayList<?>){
                outputFile = HEATMAP_LIST;
            } else if (objToSave instanceof HeatmapPixelCacheObject){
                outputFile = HEATMAP_PIXEL + ((HeatmapPixelCacheObject)objToSave).fName;
            } else if (objToSave instanceof Bitmap){
                objToSave = new ProxyBitmap((Bitmap) objToSave);
                outputFile = BKG_IN_PROGRESS;
            }
            Log.d(BaseApplication.DEBUG_MESSAGE, "saving " + outputFile);
            FileOutputStream fos = null;
            try {
                fos = context.openFileOutput(outputFile, Context.MODE_MULTI_PROCESS);
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
