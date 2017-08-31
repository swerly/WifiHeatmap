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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Seth on 8/18/2017.
 */

public class WifiHelper {
    public static final int NUMBER_LEVELS = 9;

    private static final int MIN_RSSI = -85;

    private static final int MAX_RSSI = -48;

    private Context context;
    private WifiManager wifiManager;
    private BroadcastReceiver wifiConnectionReceiver;
    private BroadcastReceiver wifiEnabledReceiver;
    private BroadcastReceiver wifiSignalLevelReceiver;

    public WifiHelper(Context context){
        this.context = context;

        setup();
    }

    private void setup(){
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean wifiEnabledAndConnected(){
        if (isWifiEnabled()) {
            if (isWifiConnected()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isWifiEnabled(){
        return wifiManager.isWifiEnabled();
    }

    public boolean isWifiConnected(){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public void startListeningForWifiChanges(final WifiConnectionChangeCallback callback){
        //receiver looking for wifi connect/disconnect
        wifiConnectionReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                SupplicantState newState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                switch(newState){
                    case ASSOCIATED:
                        callback.wifiConnectionChange(true);
                        break;
                    case DISCONNECTED:
                        callback.wifiConnectionChange(false);
                        break;
                }
            }};
        context.registerReceiver(wifiConnectionReceiver, new IntentFilter(
                WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));

        //reciever looking for wifi turn off (because apparently disconnect != turn off)
        wifiEnabledReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!isWifiEnabled()){
                    callback.wifiConnectionChange(false);
                }
            }
        };
        context.registerReceiver(wifiEnabledReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION)
        );
    }

    public void stopListeningForWifiChanges(){
        context.unregisterReceiver(wifiEnabledReceiver);
        context.unregisterReceiver(wifiConnectionReceiver);
    }

    public void listenForLevelChanges(final SignalChangedCallback callback){
        wifiSignalLevelReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                WifiInfo info = wifiManager.getConnectionInfo();

                callback.signalChanged(new WifiSignalLevel(info));
            }
        };
        context.registerReceiver(wifiSignalLevelReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    public void stopListeningForLevelChanges(){
        context.unregisterReceiver(wifiSignalLevelReceiver);
    }

    public interface SignalChangedCallback{
        void signalChanged(WifiSignalLevel signalLevel);
    }

    public interface WifiConnectionChangeCallback{
        void wifiConnectionChange(boolean wifiStatus);
    }

    public class WifiSignalLevel{
        private int level, rssi;
        public WifiSignalLevel(WifiInfo info){
            rssi = info.getRssi();
            level = calculateSignalLevel(rssi, NUMBER_LEVELS) + 1;
        }

        public int getLevel(){
            return level;
        }

        public int getRssi(){
            return  rssi;
        }
    }

    //taken from android source but modified rssi levels a bit
    private int calculateSignalLevel(int rssi, int numLevels) {
        if (rssi <= MIN_RSSI) {
            return 0;
        } else if (rssi >= MAX_RSSI) {
            return numLevels - 1;
        } else {
            float inputRange = (MAX_RSSI - MIN_RSSI);
            float outputRange = (numLevels - 1);
            return (int)((float)(rssi - MIN_RSSI) * outputRange / inputRange);
        }
    }
}
