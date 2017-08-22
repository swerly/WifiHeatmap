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
    private String name, pixelsFileName;
    private Date dateTime;

    public HeatmapData(){
        name = "In Progress";
    }

    public void setBackgroundImage(Bitmap bkgImg){
        backgroundImage = new ProxyBitmap(bkgImg);
    }

    public Bitmap getBackgroundImage(){
        return backgroundImage.getBitmap();
    }

    public void setName(String name){
        this.name = name;
        this.dateTime = Calendar.getInstance().getTime();
        this.pixelsFileName = StaticUtils.sanitizeFileName(this.name);
    }

    public String getName(){
        return this.name;
    }

    public String getPixelsFileName(){
        return pixelsFileName;
    }

    public String getDateTimeString(){
        String pattern = "MMMM dd, hh:mm aaa";
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
