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
