package com.swerly.wifiheatmap;

import android.os.Handler;

/**
 * Created by Seth on 8/14/2017.
 */

public class SnapshotWaiter {
    private static int WAIT_DELAY = 50;
    private BaseApplication app;
    private SnapshotReadyCallback readyCallback;

    public SnapshotWaiter(BaseApplication app, SnapshotReadyCallback readyCallback){
        this.app = app;
        this.readyCallback = readyCallback;
    }

    public void startWaiting(){
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable(){
            public void run(){
                if (app.isBackgroundReady()){
                    readyCallback.snapshotReady();
                } else {
                    handler.postDelayed(this, WAIT_DELAY);
                }
            }
        }, WAIT_DELAY);
    }

    public interface SnapshotReadyCallback{
        void snapshotReady();
    }
}
