package com.swerly.wifiheatmap.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.data.HeatmapPixel;
import com.swerly.wifiheatmap.data.HeatmapPixelCacheObject;
import com.swerly.wifiheatmap.utils.CacheHelper;
import com.swerly.wifiheatmap.utils.HeatmapPixelDrawer;
import com.swerly.wifiheatmap.utils.LoadCacheTask;
import com.swerly.wifiheatmap.utils.WifiHelper;

import java.util.ArrayList;

/**
 * Created by Seth on 8/17/2017.
 */

public class HeatmapView extends View implements WifiHelper.SignalChangedCallback{
    private Context context;
    private float density, viewTop, viewLeft;
    private int dpViewWidth, dpViewHeight, wifiSignalLevel;
    private boolean drawnOn;
    private HeatmapPixelDrawer pixelDrawer;
    private Bitmap bitmapBuffer;
    private Canvas canvasBuffer;
    private Paint canvasPaint;
    private WifiHelper wifiHelper;
    private HeatmapLoadingDone callback;

    private String toLoad;

    public HeatmapView(Context context) {
        this(context, null);
    }

    public HeatmapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.wifiHelper = new WifiHelper(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        initialize(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas){
        if (drawnOn){
            canvas.drawBitmap(bitmapBuffer, 0, 0, canvasPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        int[] touchPoints;
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchPoints = getTouchDp(event);
                pixelDrawer.drawPixels(touchPoints[0], touchPoints[1], wifiSignalLevel);
                drawnOn = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchPoints = getTouchDp(event);
                pixelDrawer.drawPixels(touchPoints[0], touchPoints[1], wifiSignalLevel);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    private int[] getTouchDp(MotionEvent e){
        float screenX = e.getX();
        float screenY = e.getY();
        float viewX = screenX - viewLeft;
        float viewY = screenY - viewTop;
        double dpX = Math.floor(viewX/density);
        double dpY = Math.floor(viewY/density);
        return new int[]{(int) dpX, (int) dpY};
    }

    @Override
    public void signalChanged(WifiHelper.WifiSignalLevel signalLevel) {
        wifiSignalLevel = signalLevel.getLevel();
    }

    public void setToLoad(String toLoad, HeatmapLoadingDone callback){
        this.toLoad = toLoad;
        this.callback = callback;
    }

    public void initialize(int w, int h){
        density = getResources().getDisplayMetrics().density;
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        canvasPaint.setAlpha(175);
        drawnOn = false;

        viewLeft = getLeft();
        viewTop = getTop();
        dpViewWidth = (int) Math.floor(w/density);
        dpViewHeight = (int) Math.floor(h/density);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types;
        bitmapBuffer = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        canvasBuffer = new Canvas(bitmapBuffer);

        if (toLoad == null) {
            callback.heatmapLoadingDone(true);
            pixelDrawer = new HeatmapPixelDrawer(context, canvasBuffer, dpViewWidth, dpViewHeight, density, null);
        } else {
            startPixelLoad();
        }
    }

    public void startListeningForLevelChanges(){
        wifiHelper.listenForLevelChanges(this);
    }

    public void stopListeningForLevelChanges(){
        wifiHelper.stopListeningForLevelChanges();
    }

    public HeatmapPixel[][] getHeatmapPixels(){
        return pixelDrawer.getPixelArray();
    }


    private void startPixelLoad(){
        new PixelLoad(context, null).execute(toLoad);
    }

    private class PixelLoad extends LoadCacheTask {
        public PixelLoad(Context context, CacheLoadCallbacks callbacks) {
            super(context, callbacks);
        }

        @Override
        protected Object doInBackground(String... strings) {
            HeatmapPixelCacheObject cacheObject = (HeatmapPixelCacheObject) super.doInBackground(strings);
            if (cacheObject == null){
                return false;
            }
            HeatmapPixel[][] pixels =  cacheObject.pixels;
            pixelDrawer = new HeatmapPixelDrawer(context, canvasBuffer, dpViewWidth, dpViewHeight, density, pixels);
            pixelDrawer.drawAllPixels();
            return true;
        }

        @Override
        protected void onPostExecute(Object object){
            boolean finishedDrawing = (boolean) object;
            callback.heatmapLoadingDone(finishedDrawing);
            drawnOn = true;
            invalidate();
        }
    }

    public interface HeatmapLoadingDone{
        void heatmapLoadingDone(boolean status);
    }
}