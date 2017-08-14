package com.swerly.wifiheatmap;

import android.app.Application;
import android.content.Context;

/**
 * Created by Seth on 7/3/2017.
 */

public class BaseApplication extends Application {
    public final static String DEBUG_MESSAGE = "HeatmapDebug";
    private static Context context;
    private int currentCount;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
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
}
