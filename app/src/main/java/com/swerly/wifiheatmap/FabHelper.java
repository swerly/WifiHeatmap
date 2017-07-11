package com.swerly.wifiheatmap;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

/**
 * Created by Seth on 7/6/2017.
 */

public class FabHelper{
    private ActivityMain context;
    private FloatingActionButton fab;
    private int prevFabIcon;

    public FabHelper(ActivityMain context, FloatingActionButton fab){
        this.context = context;
        this.fab = fab;
        homeInitialSetup();
    }

    private void homeInitialSetup(){
        prevFabIcon = R.drawable.ic_add_black_24dp;
        fab.setImageResource(prevFabIcon);
        fab.setOnClickListener(new FabClickListener(new FragmentMap(), FragmentBase.HOME_FRAGMENT));
    }

    public void goHome(){
        fab.setOnClickListener(new FabClickListener(new FragmentMap(), FragmentBase.HOME_FRAGMENT));
        setAndPlay(R.drawable.check_to_plus_avd);
    }

    public void setupFab(FragmentBase frag, boolean reverse){
        FragmentBase toSet = null;
        String tag = null;
        int iconResId = 0;

        if (frag instanceof FragmentHome){
            toSet = new FragmentMap();
            iconResId = reverse ? R.drawable.arrow_to_plus_avd : R.drawable.check_to_plus_avd;
        } else if (frag instanceof FragmentMap){
            toSet = new FragmentBoundry();
            iconResId = reverse ? R.drawable.ic_navigate_double_next_black_24px : R.drawable.plus_to_arrow_avd;
        } else if (frag instanceof FragmentBoundry){
            toSet = new FragmentHeatmap();
            iconResId = R.drawable.ic_navigate_double_next_black_24px;
        } else if (frag instanceof FragmentHeatmap){
            toSet = new FragmentName();
            iconResId = reverse ? R.drawable.check_to_arrow_avd : R.drawable.ic_navigate_double_next_black_24px;
        } else if (frag instanceof FragmentName){
            toSet = new FragmentHome();
            iconResId = R.drawable.arrow_to_check_avd;
        }

        tag = frag.getClass().getSimpleName();

        if (toSet != null && iconResId != 0) {
            setAndPlay(iconResId);
            fab.setOnClickListener(new FabClickListener(toSet, tag));

        } else {
            Log.d("HeatMapDebug", "Error in setting up fab");
        }
    }

    public void setAndPlay(int iconResId){
        fab.setImageResource(iconResId);

        Drawable iconDrawable = fab.getDrawable();

        if (iconDrawable instanceof Animatable) {
            ((Animatable) iconDrawable).start();
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
