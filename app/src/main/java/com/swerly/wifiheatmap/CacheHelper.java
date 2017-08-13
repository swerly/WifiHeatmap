package com.swerly.wifiheatmap;

import android.content.Context;
import android.os.AsyncTask;

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
    private static String HEATMAP_COUNT = "heatmap_count";
    private static String HEATMAP_LIST = "heatmap_list";
    private static String HEATMAP_IN_PROGRESS = "heatmap_in_progress";

    private Context context;
    private CacheLoadCallbacks loadCallbacks;

    public CacheHelper(Context context, CacheLoadCallbacks loadCallbacks){
        this.context = context;
        this.loadCallbacks = loadCallbacks;
    }

    public void saveInProgress(HeatmapData inProgressData){
        new SaveCacheTask().execute(inProgressData);
    }

    public void saveCount(int count){
        new SaveCacheTask().execute(count);
    }

    public void startCountLoad(){
        new LoadCacheTask().execute(HEATMAP_COUNT);
    }

    private class SaveCacheTask extends AsyncTask<Object, Void, Void>{

        @Override
        protected Void doInBackground(Object... inputObj) {
            String outputFile;
            Object objToSave = inputObj[0];
            //if the object we want to save is an arraylist then we are saving all heatmap data
            if (objToSave instanceof ArrayList<?>){
                outputFile = HEATMAP_LIST;
            } else if (objToSave instanceof HeatmapData){
                outputFile = HEATMAP_IN_PROGRESS;
            } else {
                outputFile = HEATMAP_COUNT;
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
            FileInputStream fos = null;

            try {
                fos = context.openFileInput(cacheToLoad);
                ObjectInputStream ois = new ObjectInputStream(fos);
                Object toReturn = ois.read();
                ois.close();
                fos.close();
                return toReturn;
            } catch (FileNotFoundException e) {
                //should have log message here? return null?
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result){
            if (loadCallbacks == null){
                return;
            }
            if (cacheToLoad.equals(HEATMAP_COUNT)){
                int count;
                if (result == null){
                    count = 0;
                } else {
                    count = (int) result;
                }
                loadCallbacks.heatmapCountLoaded(count);
            } else if (cacheToLoad.equals(HEATMAP_IN_PROGRESS)){
                loadCallbacks.heatmapInProgressLoaded((HeatmapData) result);
            } else {
                loadCallbacks.heatmapListLoaded((ArrayList<HeatmapData>) result);
            }
        }
    }

    public interface CacheLoadCallbacks{
        void heatmapListLoaded(ArrayList<HeatmapData> data);
        void heatmapCountLoaded(int count);
        void heatmapInProgressLoaded(HeatmapData inProgress);
    }
}
