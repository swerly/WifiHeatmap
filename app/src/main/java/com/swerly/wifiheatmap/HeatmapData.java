package com.swerly.wifiheatmap;


import android.graphics.Bitmap;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Seth on 8/11/2017.
 */

public class HeatmapData implements Serializable{
    private ProxyBitmap backgroundImage, finishedHeatmap;
    private String name;
    private Date dateTime;
    private HeatmapPixel[][] heatmapPixels;

    public HeatmapData(){
        name = "In Progress";
    }

    public void setBackgroundImage(Bitmap bkgImg){
        backgroundImage = new ProxyBitmap(bkgImg);
    }

    public Bitmap getBackgroundImage(){
        return backgroundImage.getBitmap();
    }

    public void setPixels(HeatmapPixel[][] newPixels){
        this.heatmapPixels = newPixels;
    }
    public HeatmapPixel[][] getPixels(){
        return heatmapPixels;
    }

    public void setName(String name){
        this.name = name;
        this.dateTime = Calendar.getInstance().getTime();
    }

    public String getName(){
        return this.name;
    }

    public String getDateTimeString(){
        String pattern = "MMMMM dd, hh:mm aaa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(this.dateTime);
    }

    public void setFinishedHeatmap(Bitmap finishedImage){
        this.finishedHeatmap = new ProxyBitmap(finishedImage);
    }

    public Bitmap getFinishedImage(){
        return finishedHeatmap.getBitmap();
    }
}
