package com.swerly.wifiheatmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Seth on 8/17/2017.
 */

public class HeatmapPixelDrawer {
    private Context context;
    private Canvas canvas;
    private int width, height;
    private float density;
    private HeatmapPixel[][] pixelArray;
    private Paint redPaint, orangePaint, yellowPaint, greenPaint, bluePaint;

    public HeatmapPixelDrawer(Context context, Canvas canvas, int width, int height, float density){
        this.context = context;
        this.canvas = canvas;
        this.width = width;
        this.height = height;
        this.density = density;
        pixelArray = new HeatmapPixel[width][height];

        for(int i = 0; i < width; i++){
            for(int j=0; j < height; j++){
                pixelArray[i][j] = new HeatmapPixel(i,j);
            }
        }

        setupPaints();
    }

    private void setupPaints(){
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setColor(context.getResources().getColor(R.color.red));

        orangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        orangePaint.setStyle(Paint.Style.FILL);
        orangePaint.setColor(context.getResources().getColor(R.color.orange));

        yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowPaint.setStyle(Paint.Style.FILL);
        yellowPaint.setColor(context.getResources().getColor(R.color.yellow));

        greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setStyle(Paint.Style.FILL);
        greenPaint.setColor(context.getResources().getColor(R.color.green));

        bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint.setStyle(Paint.Style.FILL);
        bluePaint.setColor(context.getResources().getColor(R.color.blue));
    }

    private Paint getPaint(int level){
        switch (level){
            case 5:
                return redPaint;
            case 4:
                return orangePaint;
            case 3:
                return yellowPaint;
            case 2:
                return greenPaint;
            case 1:
                return bluePaint;
            default:
                return bluePaint;
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

        /*int[] currentInstructions = DrawingInstructions.CIRCLE_ONE;
        int length = currentInstructions.length;
        int top = currentInstructions[0];
        //top half
        for (int i = 1; i < length; i++, top--){
            int halfWidth = currentInstructions[i];
            int fullWidth = halfWidth*2;
            for (int j = 0; j < fullWidth; j++){
                int curX = touchX - halfWidth + j;
                int curY = touchY - top;
                //if the current pixel is outside of the view bounds, ignore it
                if (outOfBounds(curX, curY)){
                    continue;
                }
                workSinglePixel(level, pixelArray[curX][curY]);
            }
        }

        for ( ; top < length-1; top++){
            int halfWidth = currentInstructions[length-top-1];
            int fullWidth = halfWidth*2;
            for (int j = 0; j < fullWidth; j++){
                int curX = touchX - halfWidth + j;
                int curY = touchY + top;
                //if the current pixel is outside of the view bounds, ignore it
                if (outOfBounds(curX, curY)){
                    continue;
                }
                workSinglePixel(level, pixelArray[curX][curY]);
            }
        }*/
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
            canvas.drawRect(x, y, x+density, y+density, getPaint(ringLevel));
            pixel.setLevel(ringLevel);
        } else if (assignedLevel > centerLevel && (ringLevel == 5)){
            canvas.drawRect(x, y, x+density, y+density, getPaint(centerLevel));
            pixel.setLevel(ringLevel);
        } else if (assignedLevel < centerLevel && centerLevel == ringLevel){
            canvas.drawRect(x, y, x+density, y+density, getPaint(ringLevel));
            pixel.setLevel(ringLevel);
        }

    }
}
