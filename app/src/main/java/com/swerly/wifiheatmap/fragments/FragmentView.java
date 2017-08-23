package com.swerly.wifiheatmap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.utils.ShareBitmap;
import com.swerly.wifiheatmap.utils.StaticUtils;

/**
 * Created by Seth on 8/22/2017.
 */

public class FragmentView extends FragmentBase {
    private String subtitle;
    private HeatmapData toView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);

        int indexToView = app.getIndexToView();
        toView = app.getHeatmaps().get(indexToView);
        subtitle = toView.getName();

        GestureImageView bkgView = view.findViewById(R.id.view_view);
        bkgView.setImageBitmap(toView.getFinishedImage());

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubTitle(subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_share:
                new ShareBitmap(activityMain).execute(toView.getFinishedImage());
                break;
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onFabPressed() {

    }
}
