package com.swerly.wifiheatmap.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Seth on 8/18/2017.
 */

public class WifiHelper {
    public static final int NUMBER_LEVELS = 5;
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

    public void setupWifi(){
        if (isWifiEnabled()){
            if (isWifiConnected()){
                return;
            } else {
                new MaterialDialog.Builder(context)
                        .title("Connect Wifi")
                        .content("You must connect to Wifi to use this. Or you can save for later when you have wifi.")
                        .positiveText("Connect")
                        .negativeText("Save")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                ((Activity)context).startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //todo: save to appdata and exit
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        } else {
            new MaterialDialog.Builder(context)
                    .title("Enable Wifi")
                    .content("You must enable wifi to use this feature. Or you can save for later when you have wifi.")
                    .positiveText("Enable")
                    .negativeText("Save")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            ((Activity)context).startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //todo: save to appdata and exit
                            dialog.dismiss();
                        }
                    })
                    .show();
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

    public void listenForLevelChanges(final SignalChangedCallback callack){
        wifiSignalLevelReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                WifiInfo info = wifiManager.getConnectionInfo();

                callack.signalChanged(new WifiSignalLevel(info));
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
            level = WifiManager.calculateSignalLevel(rssi, NUMBER_LEVELS) + 1;
        }

        public int getLevel(){
            return level;
        }

        public int getRssi(){
            return  rssi;
        }
    }
}
