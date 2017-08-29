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

package com.swerly.wifiheatmap.data;

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
