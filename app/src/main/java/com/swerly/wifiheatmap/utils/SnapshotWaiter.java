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

import android.os.Handler;

import com.swerly.wifiheatmap.BaseApplication;

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
