package com.swerly.wifiheatmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private FragmentBase curFrag;

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
        curFrag = frag;
        FragmentBase toSet = null;
        String tag = null;
        int iconResId = 0;

        if (frag instanceof FragmentHome){
            toSet = new FragmentMap();
            iconResId = reverse ? R.drawable.arrow_to_plus_avd : R.drawable.check_to_plus_avd;
        } else if (frag instanceof FragmentMap){
            toSet = new FragmentHeatmap();
            iconResId = reverse ? R.drawable.arrow_back : R.drawable.arrow_forward;
        } else if (frag instanceof FragmentHeatmap){
            toSet = new FragmentName();
            iconResId = reverse ? R.drawable.check_to_arrow_avd : R.drawable.arrow_forward;
        } else if (frag instanceof FragmentName){
            toSet = new FragmentHome();
            iconResId = R.drawable.arrow_to_check_avd;
        } else if (frag instanceof FragmentZoom){
            toSet = new FragmentHeatmap();
            iconResId = reverse ? R.drawable.arrow_back : R.drawable.arrow_forward;
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
            if (curFrag instanceof FragmentMap){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.zoom_dialog_title);
                builder.setMessage(R.string.zoom_dialog_content);
                builder.setPositiveButton(R.string.zoom, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toSet = new FragmentZoom();
                        dialogInterface.dismiss();
                        set();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toSet = new FragmentHeatmap();
                        dialogInterface.dismiss();
                        set();
                    }
                });
                builder.show();
            } else {
                set();
            }
        }
        private void set(){
            if (toSet != null) {
                if (!context.notifyFragmentFabClick()) {
                    context.goToFragment(toSet);
                }
            }
        }
    }


}
