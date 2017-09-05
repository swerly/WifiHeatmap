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

import com.swerly.wifiheatmap.utils.StaticUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;


/**
 * Created by Seth on 8/11/2017.
 *
 * Holds information about the created heatmap, to be displayed in cards on the homescreen
 */

public class HeatmapData implements Serializable{
    private ProxyBitmap backgroundImage, finishedHeatmap;
    private String name, pixelsFileName;
    private Date dateTime;

    /**
     * Enum for different sorting comparators
     */
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

    /**
     * gets a specific comparator
     * @param comparator the type of comparator to return
     * @return the specified comparator object
     */
    public static Comparator<HeatmapData> getComparator(final HeatmapDataComparator comparator){
        return comparator;
    }

    public HeatmapData(){
        //default name is in progress
        name = "In Progress";
    }

    /**
     * sets the background image for the heatmap data
     * @param bkgImg bitmap to set
     */
    public void setBackgroundImage(Bitmap bkgImg){
        backgroundImage = new ProxyBitmap(bkgImg);
    }

    /**
     * gets the background image for the current heatmap
     *
     * this is used when the user wants to edit a heatmap
     * @return returns the bitmap for the background image
     */
    public Bitmap getBackgroundImage(){
        return backgroundImage.getBitmap();
    }

    /**
     * sets the name, date/time, and pixel data filename for the current heatmap
     * @param name
     */
    public void setName(String name){
        //name is the display name in the card list
        this.name = name;
        //the time that this heatmap was created
        this.dateTime = Calendar.getInstance().getTime();
        //the filename of the associated heatmap data
        //created from the date/time this was created and a sanitized filename
        this.pixelsFileName = getDateTimeFileName() + StaticUtils.sanitizeFileName(this.name);
    }

    /**
     * get the name of the heatmap
     * @return String name
     */
    public String getName(){
        return this.name;
    }

    /**
     * get the filename for the associated pixel data
     * @return String filename for the associated pixel data
     */
    public String getPixelsFileName(){
        return pixelsFileName;
    }

    /**
     * get the date object for when this heatmap was created
     * @return the date object for when this heatmap was created
     */
    public Date getDateTime(){
        return dateTime;
    }

    /**
     * get the user readable date / time as a formatted string
     * @return string of the date / time this heatmap was created
     */
    public String getDateTimeString(){
        String pattern = "MMMM dd, hh:mm aaa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(this.dateTime);
    }

    /**
     * get the date / time to be used as a part of the filename
     * @return
     */
    private String getDateTimeFileName(){
        return new SimpleDateFormat("yyyyMMddHHmm").format(dateTime);
    }

    /**
     * set the bitmap for the finished heatmap image
     * @param finishedImage the finished heatmap bitmap that was created by the user
     */
    public void setFinishedHeatmap(Bitmap finishedImage){
        //convert it to a proxy bitmap so it can be serialized
        this.finishedHeatmap = new ProxyBitmap(finishedImage);
    }

    /**
     * get the finished heatmap image to be used in the list of heatmaps on the home screen
     * @return the bitmap of the finished heatmap
     */
    public Bitmap getFinishedImage(){
        return finishedHeatmap.getBitmap();
    }

    /**
     * equals method, compares to another heatmapdata object
     * @param toCheck the object to compare this to
     * @return true if objects are equal
     */
    public boolean equals(HeatmapData toCheck){
        if (!this.name.equals(toCheck.getName()))
            return false;
        if (!this.getDateTimeString().equals(toCheck.getDateTimeString()))
            return false;
        return true;
    }
}
