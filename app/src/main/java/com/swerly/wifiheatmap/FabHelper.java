package com.swerly.wifiheatmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

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
        setAndPlay(R.drawable.save_to_plus);
    }

    public void setupFab(FragmentBase frag, boolean reverse, boolean fromHeatmap){
        curFrag = frag;
        FragmentBase toSet = null;
        String tag = null;
        int iconResId = 0;

        if (frag instanceof FragmentHome){
            toSet = new FragmentMap();
            iconResId = reverse ? R.drawable.arrow_to_plus_avd : R.drawable.save_to_plus;
        } else if (frag instanceof FragmentMap){
            toSet = new FragmentHeatmap();
            iconResId = reverse ? ( fromHeatmap ? R.drawable.save_to_arrow: R.drawable.arrow_back) : R.drawable.arrow_forward;
        } else if (frag instanceof FragmentHeatmap){
            toSet = new FragmentHome();
            iconResId = R.drawable.arrow_to_save;
        } else if (frag instanceof FragmentZoom){
            toSet = new FragmentHeatmap();
            iconResId = reverse ? R.drawable.save_to_arrow: R.drawable.arrow_forward;
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
        Drawable db = context.getDrawable(iconResId);
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
            if (tag.equals(FragmentBase.MAP_FRAGMENT)){
                new MaterialDialog.Builder(context)
                        .title(R.string.zoom_dialog_title)
                        .content(R.string.zoom_dialog_content)
                        .positiveText(R.string.zoom)
                        .negativeText(R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                toSet = new FragmentZoom();
                                dialog.dismiss();
                                set();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                toSet = new FragmentHeatmap();
                                dialog.dismiss();
                                set();
                            }
                        })
                        .show();
            } else if (tag.equals(FragmentBase.HEATMAP_FRAGMENT)){
                new MaterialDialog.Builder(context)
                        .title(R.string.save_heatmap)
                        .content(R.string.zoom_dialog_content)
                        .positiveText(R.string.save)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                set();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
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
