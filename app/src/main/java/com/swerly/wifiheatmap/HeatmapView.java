package com.swerly.wifiheatmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Seth on 8/17/2017.
 */

public class HeatmapView extends View implements WifiHelper.SignalChangedCallback {
    private Context context;
    private float density, viewTop, viewLeft;
    private int dpViewWidth, dpViewHeight, wifiSignalLevel;
    private boolean drawnOn;
    private HeatmapPixelDrawer pixelDrawer;
    private Bitmap bitmapBuffer;
    private Canvas canvasBuffer;
    private Paint canvasPaint;
    private WifiHelper wifiHelper;

    private TextView signalText;

    public HeatmapView(Context context) {
        this(context, null);
    }

    public HeatmapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setup();
    }

    private void setup(){
        this.wifiHelper = new WifiHelper(context);
        density = getResources().getDisplayMetrics().density;
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        canvasPaint.setAlpha(175);
        drawnOn = false;
    }

    public void setLevelText(TextView lt){
        this.signalText = lt;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        viewLeft = getLeft();
        viewTop = getTop();
        dpViewWidth = (int) Math.floor(w/density);
        dpViewHeight = (int) Math.floor(h/density);

        Log.d("dpDebug", "width x height: " + Integer.toString(dpViewWidth) + " x " + Integer.toString(dpViewHeight));

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types;
        bitmapBuffer = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        canvasBuffer = new Canvas(bitmapBuffer);

        pixelDrawer = new HeatmapPixelDrawer(context, canvasBuffer, dpViewWidth, dpViewHeight, density );
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
        if (signalText != null) {
            signalText.setText(Integer.toString(signalLevel.rssi));
        }
        wifiSignalLevel = signalLevel.level;
    }
    public void startListeningForLevelChanges(){
        wifiHelper.listenForLevelChanges(this);

    }

    public void stopListeningForLevelChanges(){
        wifiHelper.stopListeningForLevelChanges();
    }
}
