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
    public static String HEATMAP_IN_PROGRESS = "heatmap_in_progress";
    public static String HEATMAP_PIXEL = "heatmap_pixel_cache";

    private Context context;
    private CacheLoadCallbacks loadCallbacks;

    public CacheHelper(Context context, CacheLoadCallbacks loadCallbacks){
        this.context = context;
        this.loadCallbacks = loadCallbacks;
    }

    public void saveInProgress(HeatmapData inProgressData){
        new SaveCacheTask().execute(inProgressData);
    }

    public void savePixels(HeatmapPixelCacheObject pixelsToCache){
        new SaveCacheTask().execute(pixelsToCache);
    }

    public void saveHeatmapList(ArrayList<HeatmapData> heatmapList){
        new SaveCacheTask().execute(heatmapList);
    }

    public void startupLoad(){
        new LoadCacheTask().execute(HEATMAP_IN_PROGRESS);
        new LoadCacheTask().execute(HEATMAP_LIST);
    }

    public void deleteInProgress(){
        context.deleteFile(HEATMAP_IN_PROGRESS);
        context.deleteFile(HEATMAP_PIXEL + HEATMAP_IN_PROGRESS);
    }

    private class SaveCacheTask extends AsyncTask<Object, Void, Void>{

        @Override
        protected Void doInBackground(Object... inputObj) {
            String outputFile = "";
            Object objToSave = inputObj[0];
            //if the object we want to save is an arraylist then we are saving all heatmap data
            if (objToSave instanceof ArrayList<?>){
                outputFile = HEATMAP_LIST;
            } else if (objToSave instanceof HeatmapData){
                outputFile = HEATMAP_IN_PROGRESS;
            } else if (objToSave instanceof HeatmapPixelCacheObject){
                outputFile = HEATMAP_PIXEL + ((HeatmapPixelCacheObject)objToSave).fName;
            }
            FileOutputStream fos = null;
            try {
                fos = context.openFileOutput(outputFile, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(objToSave);
                oos.flush();
                fos.getFD().sync();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class LoadCacheTask extends AsyncTask<String, Void, Object>{
        private String cacheToLoad;

        @Override
        protected Object doInBackground(String... strings) {
            cacheToLoad = strings[0];
            if (!cacheToLoad.equals(HEATMAP_LIST) && !cacheToLoad.equals(HEATMAP_IN_PROGRESS)){
                cacheToLoad = HEATMAP_PIXEL + cacheToLoad;
            }
            FileInputStream fos = null;

            try {
                fos = context.openFileInput(cacheToLoad);
                ObjectInputStream ois = new ObjectInputStream(fos);
                Object toReturn = null;
                toReturn = ois.readObject();
                ois.close();
                fos.close();
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
            if (loadCallbacks == null){
                return;
            }

            if (cacheToLoad.equals(HEATMAP_IN_PROGRESS)){
                loadCallbacks.heatmapInProgressLoaded((HeatmapData) result);
            } else if (cacheToLoad.equals(HEATMAP_LIST)) {
                loadCallbacks.heatmapListLoaded((ArrayList<HeatmapData>) result);
            } else {
                loadCallbacks.heatmapPixelsLoaded((HeatmapPixelCacheObject) result);
            }
        }
    }

    public interface CacheLoadCallbacks{
        void heatmapListLoaded(ArrayList<HeatmapData> data);
        void heatmapInProgressLoaded(HeatmapData inProgress);
        void heatmapPixelsLoaded(HeatmapPixelCacheObject pixels);
    }
}
