package com.swerly.wifiheatmap.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.swerly.wifiheatmap.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Seth on 8/7/2017.
 */

public class StaticUtils {
    public static boolean isConnectedToNetwork(Context thisActivity) {
        ConnectivityManager connMgr = (ConnectivityManager) thisActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static Bitmap getScreenShot(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static String sanitizeFileName(String fName){
        return fName.replaceAll("[^a-zA-Z0-9.-]", "");
    }
}
