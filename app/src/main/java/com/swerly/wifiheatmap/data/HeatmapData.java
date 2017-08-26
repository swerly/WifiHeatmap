package com.swerly.wifiheatmap.data;


import android.graphics.Bitmap;

import com.swerly.wifiheatmap.utils.StaticUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;


/**
 * Created by Seth on 8/11/2017.
 */

public class HeatmapData implements Serializable{
    private ProxyBitmap backgroundImage, finishedHeatmap;
    private String name, pixelsFileName;
    private Date dateTime;

    public enum HeatmapDataComparator implements Comparator<HeatmapData>{
        NAME_SORT{
            public int compare(HeatmapData d1, HeatmapData d2){
                return d1.getName().compareTo(d2.getName());
            }
        },
        DATE_SORT{
            public int compare(HeatmapData d1, HeatmapData d2){
                return d1.getDateTime().compareTo(d2.getDateTime());
            }
        }
    }

    public static Comparator<HeatmapData> getComparator(final HeatmapDataComparator comparator){
        return comparator;
    }

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
        this.pixelsFileName = getDateTimeFileName() + StaticUtils.sanitizeFileName(this.name);
    }

    public String getName(){
        return this.name;
    }

    public String getPixelsFileName(){
        return pixelsFileName;
    }

    public Date getDateTime(){
        return dateTime;
    }

    public String getDateTimeString(){
        String pattern = "MMMM dd, hh:mm aaa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(this.dateTime);
    }

    private String getDateTimeFileName(){
        return new SimpleDateFormat("yyyyMMddHHmm").format(dateTime);
    }

    public void setFinishedHeatmap(Bitmap finishedImage){
        this.finishedHeatmap = new ProxyBitmap(finishedImage);
    }

    public Bitmap getFinishedImage(){
        return finishedHeatmap.getBitmap();
    }

    public boolean equals(HeatmapData toCheck){
        if (!this.name.equals(toCheck.getName()))
            return false;
        if (!this.getDateTimeString().equals(toCheck.getDateTimeString()))
            return false;
        return true;
    }
}
