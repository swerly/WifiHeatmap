package com.swerly.wifiheatmap;

import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

/**
 * Created by Seth on 7/6/2017.
 */

public class FabHelper{
    private MainActivity context;
    private FloatingActionButton fab;

    public FabHelper(MainActivity context, FloatingActionButton fab){
        this.context = context;
        this.fab = fab;
    }

    public void setupFab(FragmentBase frag){
        FragmentBase toSet = null;
        String tag = null;
        int iconResId = 0;

        if (frag instanceof FragmentHome){
            toSet = new FragmentMap();
            //tag = FragmentBase.MAP_FRAGMENT;
            iconResId = R.drawable.ic_add_black_24dp;
        } else if (frag instanceof FragmentMap){
            toSet = new FragmentBoundry();
            //tag = FragmentBase.BOUNDRY_FRAGMENT;
            iconResId = R.drawable.ic_navigate_double_next_black_24px;
        } else if (frag instanceof FragmentBoundry){
            toSet = new FragmentHeatmap();
            //tag = FragmentBase.MAP_FRAGMENT;
            iconResId = R.drawable.ic_navigate_double_next_black_24px;
        } else if (frag instanceof FragmentHeatmap){
            toSet = new FragmentName();
            //tag = FragmentBase.NAME_FRAGMENT;
            iconResId = R.drawable.ic_navigate_double_next_black_24px;
        } else if (frag instanceof FragmentName){
            toSet = new FragmentHome();
            //tag = FragmentBase.HOME_FRAGMENT;
            iconResId = R.drawable.ic_navigate_double_next_black_24px;
        }

        tag = frag.getClass().getSimpleName();

        if (toSet != null && iconResId != 0) {
            fab.setImageResource(iconResId);
            fab.setOnClickListener(new FabClickListener(toSet, tag));
        } else {
            Log.d("HeatMapDebug", "Error in setting up fab");
        }

    }

    private class FabClickListener implements View.OnClickListener {
        FragmentBase toSet;
        String tag;
        protected FabClickListener(FragmentBase toSet, String tag){
            this.toSet = toSet;
            this.tag = tag;
        }

        @Override
        public void onClick(View view) {
            if (toSet != null) {
                context.goToFragment(toSet);
            }

        }
    }
}
