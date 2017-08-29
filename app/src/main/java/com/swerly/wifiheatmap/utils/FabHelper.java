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
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.activities.ActivityMain;
import com.swerly.wifiheatmap.fragments.FragmentBase;
import com.swerly.wifiheatmap.fragments.FragmentHeatmap;
import com.swerly.wifiheatmap.fragments.FragmentHome;
import com.swerly.wifiheatmap.fragments.FragmentInfo;
import com.swerly.wifiheatmap.fragments.FragmentMap;
import com.swerly.wifiheatmap.fragments.FragmentView;
import com.swerly.wifiheatmap.fragments.FragmentZoom;

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

    public void setupFab(Fragment frag, boolean reverse, boolean fromHeatmap){
        curFrag = (FragmentBase) frag;
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
            iconResId = reverse ? R.drawable.save_to_arrow : R.drawable.arrow_forward;
        } else if (frag instanceof FragmentInfo){
            hideFab();
            return;
        } else if (frag instanceof FragmentView){
            hideFab();
            return;
        }

        tag = frag.getClass().getSimpleName();
        showFab();

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
            //notify the fragment so that it can do things it needs to before we go to the next fragment
            context.notifyFragmentFabClick();

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
                saveClicked();
            } else {
                set();
            }
        }
        private void set(){
            if (toSet != null) {
                context.goToFragment(toSet);
            }
        }

        private void saveClicked(){
            if (context.getApp().isEditing()){
                saveHeatmap(null);
                set();
                return;
            }
            final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            //this is a little messy...
            new MaterialDialog.Builder(context)
                    .title(R.string.save_heatmap)
                    .autoDismiss(false)
                    .positiveText(R.string.save)
                    .negativeText(R.string.cancel)
                    .customView(R.layout.view_naming, false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            View customView = dialog.getCustomView();
                            EditText nameTextView = customView.findViewById(R.id.naming_edittext);
                            String nameText = nameTextView.getText().toString();
                            if ("".equals(nameText)){
                                Toast.makeText(context, context.getString(R.string.enter_name), Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                saveHeatmap(nameText);
                                imm.hideSoftInputFromWindow(nameTextView.getWindowToken(), 0);
                                dialog.dismiss();
                                set();
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void saveHeatmap(String nameText){
        BaseApplication app = context.getApp();
        View viewToScreenshot = context.findViewById(R.id.fragment_container);
        app.setCurrentInProgressFinished(StaticUtils.getScreenShot(viewToScreenshot));
        if (nameText != null) {
            app.setCurrentInProgressName(nameText);
        }
        app.finishCurrentInProgress();
    }

    public void hideFab(){
        fab.setVisibility(View.GONE);
    }

    public void showFab(){
        fab.setVisibility(View.VISIBLE);
    }

}
