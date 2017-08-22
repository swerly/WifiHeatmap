package com.swerly.wifiheatmap.data;

import com.swerly.wifiheatmap.data.HeatmapPixel;

import java.io.Serializable;

/**
 * Created by Seth on 8/21/2017.
 */

public class HeatmapPixelCacheObject implements Serializable {
    public String fName;
    public HeatmapPixel[][] pixels;

    public HeatmapPixelCacheObject(HeatmapPixel[][] pixels, String fName){
        this.pixels = pixels;
        this.fName = fName;
    }
}
