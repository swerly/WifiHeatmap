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

package com.swerly.wifiheatmap.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.data.DrawingInstructions;
import com.swerly.wifiheatmap.data.HeatmapPixel;

/**
 * Created by Seth on 8/17/2017.
 */

public class HeatmapPixelDrawer {
    private Context context;
    private Canvas currentCanvas;
    private int width, height;
    private float density;
    private HeatmapPixel[][] pixelArray;
    private Paint redPaint, redOrangePaint, orangePaint, orangeYellowPaint, yellowPaint, yellowGreenPaint, greenPaint, greenBluePaint, bluePaint;

    public HeatmapPixelDrawer(Context context, Canvas canvas, int width, int height, float density, HeatmapPixel[][] toEdit){
        this.context = context;
        this.currentCanvas = canvas;
        this.width = width;
        this.height = height;
        this.density = density;
        pixelArray = new HeatmapPixel[width][height];


        if (toEdit == null) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    pixelArray[i][j] = new HeatmapPixel(i, j);
                }
            }
        } else {
            pixelArray = toEdit;
        }

        setupPaints();
    }

    private void setupPaints(){
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setColor(context.getResources().getColor(R.color.red));

        redOrangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redOrangePaint.setStyle(Paint.Style.FILL);
        redOrangePaint.setColor(context.getResources().getColor(R.color.redorange));

        orangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        orangePaint.setStyle(Paint.Style.FILL);
        orangePaint.setColor(context.getResources().getColor(R.color.orange));

        orangeYellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        orangeYellowPaint.setStyle(Paint.Style.FILL);
        orangeYellowPaint.setColor(context.getResources().getColor(R.color.orangeyellow));

        yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowPaint.setStyle(Paint.Style.FILL);
        yellowPaint.setColor(context.getResources().getColor(R.color.yellow));

        yellowGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowGreenPaint.setStyle(Paint.Style.FILL);
        yellowGreenPaint.setColor(context.getResources().getColor(R.color.yellowgreen));

        greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setStyle(Paint.Style.FILL);
        greenPaint.setColor(context.getResources().getColor(R.color.green));

        greenBluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenBluePaint.setStyle(Paint.Style.FILL);
        greenBluePaint.setColor(context.getResources().getColor(R.color.greenblue));

        bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint.setStyle(Paint.Style.FILL);
        bluePaint.setColor(context.getResources().getColor(R.color.blue));
    }

    private Paint getPaint(int level){
        switch (level){
            case 9:
                return redPaint;
            case 8:
                return redOrangePaint;
            case 7:
                return orangePaint;
            case 6:
                return orangeYellowPaint;
            case 5:
                return yellowPaint;
            case 4:
                return yellowGreenPaint;
            case 3:
                return greenPaint;
            case 2:
                return greenBluePaint;
            case 1:
                return bluePaint;
            default:
                return bluePaint;
        }
    }

    public void drawAllPixels(){
        int iMax = pixelArray.length;
        for (int i = 0; i < iMax; i++){
            int jMax = pixelArray[i].length;
            for (int j = 0; j < jMax; j++){
                HeatmapPixel curPixel = pixelArray[i][j];
                int level = curPixel.getLevel();
                if (level == Integer.MIN_VALUE){
                    continue;
                } else {
                    float x = curPixel.getX()*density;
                    float y = curPixel.getY()*density;
                    currentCanvas.drawRect(x, y, x+density, y+density, getPaint(level));
                }
            }
        }
    }


    public void drawPixels(int touchX, int touchY, int centerLevel){
        drawCircleOne(touchX, touchY, centerLevel, centerLevel);
        drawCircleTwo(touchX, touchY, centerLevel, centerLevel-1);
        drawCircleThree(touchX, touchY, centerLevel, centerLevel-2);
    }

    private void drawCircleOne(int touchX, int touchY, int centerLevel, int level){
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_ONE, false, false);
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_ONE, false, true);
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_ONE, true, false);
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_ONE, true, true);
    }

    private void drawCircleTwo(int touchX, int touchY, int centerLevel, int level){
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_TWO, false, false);
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_TWO, false, true);
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_TWO, true, false);
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_TWO, true, true);
    }

    private void drawCircleThree(int touchX, int touchY, int centerLevel, int level){
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_THREE, false, false);
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_THREE, false, true);
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_THREE, true, false);
        drawCircleQuadrants(touchX, touchY, centerLevel, level, DrawingInstructions.CIRCLE_THREE, true, true);
    }

    private void drawCircleQuadrants(int touchX, int touchY, int centerLevel, int level, int[][] currentInstructions, boolean top, boolean left){
        int length = currentInstructions.length;
        int yOffset = currentInstructions[0][0];

        for (int i = 1; i < length; i++){
            int posFromCenter = currentInstructions[i][0];
            int width = currentInstructions[i][1];
            for (int j = 0; j < width; j++){
                //use different coordinates based off what quadrant we are drawing
                int curX = left ? touchX - posFromCenter + j : touchX + (posFromCenter-1) - j;
                int curY = top ? touchY - yOffset : touchY + (yOffset-1);

                //if the current pixel is outside of the view bounds, ignore it
                if (outOfBounds(curX, curY)){
                    continue;
                }
                workSinglePixel(level, centerLevel, pixelArray[curX][curY]);
            }
            yOffset--;
        }
    }

    private boolean outOfBounds(int x, int y){
        return x < 0 || x > (width-1) || y < 0 || y > (height-1);
    }

    private void workSinglePixel(int ringLevel, int centerLevel, HeatmapPixel pixel){
        int assignedLevel = pixel.getLevel();
        float x = pixel.getX()*density;
        float y = pixel.getY()*density;
        if (assignedLevel == 0 || assignedLevel < ringLevel){
            currentCanvas.drawRect(x, y, x+density, y+density, getPaint(ringLevel));
            pixel.setLevel(ringLevel);
        } else if (centerLevel == ringLevel){
            currentCanvas.drawRect(x, y, x+density, y+density, getPaint(ringLevel));
            pixel.setLevel(ringLevel);
        }
    }

    public HeatmapPixel[][] getPixelArray(){
        return pixelArray;
    }
}
