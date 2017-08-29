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
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.swerly.wifiheatmap.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Seth on 8/22/2017.
 */

public class ShareBitmap {
    Context context;
    MaterialDialog loadingDialog;

    public ShareBitmap(Context context){
        this.context = context;
    }

    private void dismissDialog(){
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    public void execute(Bitmap toShare){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(R.string.share_heatmap);
        builder.content(R.string.share_loading);
        builder.progress(true, 0);
        loadingDialog = builder.show();

        new AsyncTask<Bitmap, Void, File>(){
            @Override
            protected File doInBackground(Bitmap... bitmaps) {
                try {
                    // save the cached file
                    File cachePath = new File(context.getCacheDir(), "images");
                    cachePath.mkdirs();
                    // the cached image will be overwritten each time. do i need to delete the cached image?
                    // nah
                    FileOutputStream fos = new FileOutputStream(cachePath + "/tempHeatmap.png");
                    bitmaps[0].compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.getFD().sync();
                    fos.close();
                    //get the cached file
                    File result = new File(cachePath, "tempHeatmap.png");
                    return result;
                } catch (IOException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(File result) {
                dismissDialog();
                Uri contentUri = FileProvider.getUriForFile(context, "com.swerly.wifiheatmap.fileprovider", result);

                //share the file
                if (contentUri != null){
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String type = context.getContentResolver().getType(contentUri);
                    shareIntent.setDataAndType(contentUri, type);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_heatmap)));
                } else {
                    Toast.makeText(context, R.string.share_error, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }.execute(toShare);
    }
}
