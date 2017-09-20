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
import com.swerly.wifiheatmap.data.ProxyBitmap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class LoadCacheTask extends AsyncTask<String, Void, Object> {
    private String cacheToLoad;
    private Context context;
    private CacheLoadCallback callback;

    public LoadCacheTask(Context context, CacheLoadCallback callback){
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(String... strings) {
        cacheToLoad = strings[0];
        if (!cacheToLoad.equals(CacheHelper.HEATMAP_LIST) && !cacheToLoad.equals(CacheHelper.BKG_IN_PROGRESS)){
            cacheToLoad = CacheHelper.HEATMAP_PIXEL + cacheToLoad;
        }
        FileInputStream fos = null;

        try {
            Log.d(BaseApplication.DEBUG_MESSAGE, "loading cache: " + cacheToLoad);
            fos = context.openFileInput(cacheToLoad);
            ObjectInputStream ois = new ObjectInputStream(fos);
            Object toReturn = null;
            toReturn = ois.readObject();
            ois.close();
            fos.close();
            Log.d(BaseApplication.DEBUG_MESSAGE, "cache loaded: " + cacheToLoad);

            //if we saved the bkg image, turn it into a bitmap before we return it
            if (cacheToLoad.equals(CacheHelper.BKG_IN_PROGRESS) && toReturn != null){
                toReturn = ((ProxyBitmap)toReturn).getBitmap();
            }

            return toReturn;
        } catch (FileNotFoundException e) {
            Log.d(BaseApplication.DEBUG_MESSAGE, "file not found: " + cacheToLoad);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result){
        if (callback == null){
            return;
        }

        callback.dataLoaded(cacheToLoad, result);
    }

    public interface CacheLoadCallback{
        void dataLoaded(String type, Object data);
    }
}