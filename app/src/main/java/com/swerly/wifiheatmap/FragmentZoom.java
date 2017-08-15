package com.swerly.wifiheatmap;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alexvasilkov.gestures.views.GestureImageView;

/**
 * Created by Seth on 8/10/2017.
 */

public class FragmentZoom extends FragmentBase implements
        SnapshotWaiter.SnapshotReadyCallback{

    private GestureImageView bkgView;
    private boolean bkgSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_zoom, container, false);
        bkgView = view.findViewById(R.id.gesture_view);
        bkgView.getController().getSettings()
                .setRotationEnabled(true);

        if (app.isBackgroundReady()){
            setBackground();
        } else {
            new SnapshotWaiter(app, this).startWaiting();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        actionBarHelper.setupForFragment(this, menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public boolean onBackPressed() {
        activityMain.setFragTransitionFade(false);
        return false;
    }

    @Override
    public void onFabPressed() {
        activityMain.setFragTransitionFade(false);
        Bitmap bkg = StaticUtils.getScreenShot(bkgView);
        app.setBackgroundInProgress(bkg);
        app.setBackgroundReady();
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubTitle(R.string.zoom_subtitle);
    }

    @Override
    public void snapshotReady() {
        setBackground();
    }

    private void setBackground(){
        if (!bkgSet) {
            Bitmap bkgToSet = app.getCurrentInProgress().getBackgroundImage();
            bkgView.setImageBitmap(bkgToSet);
        }
    }
}
