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

import com.swerly.wifiheatmap.data.HeatmapPixel;

import java.io.Serializable;

/**
 * Created by Seth on 8/21/2017.
 *
 * Object to store the heatmap pixels in the cache
 */

public class HeatmapPixelCacheObject implements Serializable {
    public String fName;
    public HeatmapPixel[][] pixels;

    /**
     * constructor for the cache object
     * @param pixels pixels to save
     * @param fName name of the file to save
     */
    public HeatmapPixelCacheObject(HeatmapPixel[][] pixels, String fName){
        this.pixels = pixels;
        this.fName = fName;
    }
}
