package com.swerly.wifiheatmap;

/**
 * Created by Seth on 8/21/2017.
 */

public class HeatmapPixelCacheObject {
    public String fName;
    public HeatmapPixel[][] pixels;

    public HeatmapPixelCacheObject(HeatmapPixel[][] pixels, String fName){
        this.pixels = pixels;
        this.fName = fName;
    }
}
