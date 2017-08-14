package com.swerly.wifiheatmap;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Seth on 8/13/2017.
 *
 * Regular bitmaps aren't serializable, so this class was created to store bitmaps
 *
 * http://xperience57.blogspot.com/2015/09/android-saving-bitmap-as-serializable.html
 */

public class ProxyBitmap implements Serializable {
    private final int [] pixels;
    private final int width , height;

    public ProxyBitmap(Bitmap bitmap){
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        pixels = new int [width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);
    }

    public Bitmap getBitmap(){
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }
}
