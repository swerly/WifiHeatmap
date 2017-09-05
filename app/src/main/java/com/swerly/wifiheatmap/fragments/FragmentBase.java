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

package com.swerly.wifiheatmap.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.swerly.wifiheatmap.utils.ActionBarHelper;
import com.swerly.wifiheatmap.activities.ActivityMain;
import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.R;

/**
 * Base fragment that all other fragments will extend
 */

public abstract class FragmentBase extends Fragment {
    public static final String HOME_FRAGMENT = "FragmentHome";
    public static final String MAP_FRAGMENT = "FragmentMap";
    public static final String HEATMAP_FRAGMENT = "FragmentHeatmap";
    public static final String ZOOM_FRAGMENT = "FragmentZoom";
    public static final String IFNO_FRAGMENT = "FragmentInfo";

    protected ActivityMain activityMain;
    protected ActionBarHelper actionBarHelper;
    protected BaseApplication app;

    private String subTitleToSet;
    private TextView subTitle;
    private AlphaAnimation fadeIn;
    private AlphaAnimation fadeOut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the main activity
        activityMain = (ActivityMain) getActivity();
        //set the application
        app = activityMain.getApp();
        actionBarHelper = new ActionBarHelper();

        //setup the subtitle text
        setupSubTitle();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //each fragment will have an options menu
        setHasOptionsMenu(true);
        return null;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //setup the actionbar for each fragment
        actionBarHelper.setupForFragment(this, menu, inflater);
    }

    /**
     * setup the subtitle and the fade animations
     */
    private void setupSubTitle(){
        subTitle = getActivity().findViewById(R.id.subtitle);
        fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(300);
        fadeIn.setDuration(300);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                subTitle.setText(subTitleToSet);
                subTitle.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * hide the subtitle
     */
    protected void hideSubtitle(){
        if (subtitleOK()) {
            subTitle.setText("");
            subTitle.setVisibility(View.GONE);
        }
    }

    /**
     * set the subtitle
     * @param resId resource id to set as the subtitle
     */
    protected void setSubTitle(int resId){
        if (subtitleOK()) {
            subTitleToSet = getString(resId);
            subTitle.setVisibility(View.VISIBLE);
            subTitle.startAnimation(fadeOut);
        }
    }

    /**
     * set the subtitle
     * @param subtitle string to set as the subtitle
     */
    protected void setSubTitle(String subtitle){
        if (subtitleOK()) {
            subTitleToSet = subtitle;
            subTitle.setVisibility(View.VISIBLE);
            subTitle.startAnimation(fadeOut);
        }
    }

    /**
     * make sure the subtitle is ok for presenting
     * @return if the subtitle can be set
     */
    private boolean subtitleOK(){
        //if the subtitle view is null, find it again
        if (subTitle == null){
            subTitle = getActivity().findViewById(R.id.subtitle);
        }
        //return whether the subtitle view could be found
        if (subTitle == null){
            return false;
        } else{
            return true;
        }
    }

    //methods that each fragment should be able to handle
    public abstract boolean onOptionsItemSelected(MenuItem item);
    public abstract boolean onBackPressed();
    public abstract void onFabPressed();

}
