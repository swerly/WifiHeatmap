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

import java.io.Serializable;

/**
 * Created by Seth on 8/17/2017.
 */

public class HeatmapPixel implements Serializable{
    private int level;
    private int x, y;

    public HeatmapPixel(int x, int y){
        this.x = x;
        this.y = y;
        level = Integer.MIN_VALUE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLevel(){
        return level;
    }

    public void setLevel(int level){
        this.level = level;
    }
}
