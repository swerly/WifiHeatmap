package com.swerly.wifiheatmap;


import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Seth on 8/11/2017.
 */

public class HeatmapData implements Serializable{
    private ProxyBitmap backgroundImage;
    private String name;
    private Date dateTime;

    public void setBackgroundImage(Bitmap bkgImg){
        backgroundImage = new ProxyBitmap(bkgImg);
    }
    public Bitmap getBackgroundImage(){
        return backgroundImage.getBitmap();
    }
    //TODO: some array of points for the boundry
    //TODO: some array of "heatmap pixels" for the actual heatmap
}
