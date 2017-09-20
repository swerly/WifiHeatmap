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

package com.swerly.wifiheatmap.activities;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.GoogleMap;
import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.utils.CacheHelper;
import com.swerly.wifiheatmap.utils.FabHelper;
import com.swerly.wifiheatmap.utils.HelpScreenHelper;
import com.swerly.wifiheatmap.utils.LocationHelper;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.fragments.FragmentBase;
import com.swerly.wifiheatmap.fragments.FragmentHeatmap;
import com.swerly.wifiheatmap.fragments.FragmentHome;
import com.swerly.wifiheatmap.fragments.FragmentMap;
import com.swerly.wifiheatmap.utils.StaticUtils;

public class ActivityMain extends ActivityBase implements GoogleMap.SnapshotReadyCallback {

    private FabHelper fabHelper;
    private FloatingActionButton mainFab;
    private FragmentManager fragmentManager;
    private HelpScreenHelper helpScreen;
    private CacheHelper cacheHelper;
    private boolean savingBkg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup components that will be used in each activity
        mainFab = findViewById(R.id.fab);
        fabHelper = new FabHelper(this, mainFab);
        helpScreen = new HelpScreenHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragmentManager = getSupportFragmentManager();

        //load the home fragment
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, FragmentHome.newInstance(), FragmentBase.HOME_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if (cacheHelper == null){
            cacheHelper = new CacheHelper(this);
        }

        if (mainFab == null){
            mainFab = findViewById(R.id.fab);
        }

        if (fabHelper == null){
            fabHelper = new FabHelper(this, mainFab);
        }

        if (helpScreen == null){
            helpScreen = new HelpScreenHelper(this);
        }
    }

    @Override
    public void onBackPressed() {
        //if the fragment consumes the event, we are done
        if(notifyFragmentBackPressed()){
            return;
        }
        //else figure out what to do instead
        else {
            backNavigation();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //when back button is pressed, decide what needs to happen
        backNavigation();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            //if we are returning from location settings, notify the map fragment
            case LocationHelper.LOCATION_ENABLER_ID:
                FragmentBase mapFrag = (FragmentBase) fragmentManager.findFragmentByTag(FragmentBase.MAP_FRAGMENT);
                ((FragmentMap)mapFrag).returnFromSettings();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void backNavigation(){
        //if the help screen is visible we want to close it
        if (helpScreen.isHelpVisible()){
            helpScreen.hideHelp();
            return;
        }
        //get the last fragment in the stack
        FragmentBase toPop = (FragmentBase) fragmentManager.findFragmentById(R.id.fragment_container);
        boolean fromHeatmap = toPop instanceof FragmentHeatmap;
        //check to see if a fragment was popped
        boolean popped = getSupportFragmentManager().popBackStackImmediate();
        //if a fragment was popped
        if (popped){
            //check the currently active fragment
            FragmentBase curFrag = (FragmentBase) fragmentManager.findFragmentById(R.id.fragment_container);
            //setup the fab for the current fragment
            fabHelper.setupFab(curFrag, true, fromHeatmap);
            //setup the help screen for the current fragment and hide it
            //(since it was a back press, we dont want to show help by default)
            setupHelpForFragmentAndHide(curFrag);
        }
        //else no fragment was popped and we can close the application
        else {
            finish();
        }
    }

    /**
     * Notify the current fragment that the back button was pressed
     * @return boolean if the fragment consumed the event
     */
    private boolean notifyFragmentBackPressed(){
        //get the current fragment and notify it that back was pressed
        FragmentBase curFrag = (FragmentBase) fragmentManager.findFragmentById(R.id.fragment_container);
        return curFrag.onBackPressed();
    }

    /**
     * Notify the current fragment that the fab was pressed so it can do any functions before moving fragments
     */
    public void notifyFragmentFabClick(){
        FragmentBase curFrag = (FragmentBase) fragmentManager.findFragmentById(R.id.fragment_container);
        curFrag.onFabPressed();
    }

    /**
     * Replaces the current fragment in the main activity with the new fragment
     * @param frag fragment to replace the old fragment
     */
    public void goToFragment(FragmentBase frag){
        String tag = frag.getClass().getSimpleName();

        //if we are at the last sequence in the heatmap drawing, go home
        //this will pop the entire backstack instead of making a new fragment
        if (tag.equals(FragmentBase.HOME_FRAGMENT)){
            goHome();
        } else {
            //begin replacement transaction
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, frag, tag)
                    .addToBackStack(null)
                    .commit();

            //setup the fab for the fragment
            fabHelper.setupFab(frag, false, false);
        }
    }

    /**
     * Pops all the backstack entries so we are left with the first fragment (home)
     */
    public void goHome(){
        //if the backstack has at least on entry
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fabHelper.goHome();
        }
    }

    public BaseApplication getApp(){
        return app;
    }

    public void hideFab(){
        fabHelper.hideFab();
    }

    public void showFab(){
        fabHelper.showFab();
    }

    public void showHelp(){
        helpScreen.showHelp();
    }

    public void hideHelp(){
        helpScreen.hideHelp();
    }


    /**
     * setup the help screen for a specific fragment (step in creating a heatmap)
     * @param frag the fragment to setup the help screen for
     */
    public void setupHelpForFragmentAndHide(FragmentBase frag){
        helpScreen.setupForFragmentAndHide(frag);
    }

    public void setupHelpForFragment(FragmentBase frag){
        helpScreen.setupForFragment(frag, false);
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        app.saveBkgInProgress(bitmap);
        savingBkg = false;
    }

    public void setSavingBkg(){
        savingBkg = true;
    }

    public boolean isSavingBkg(){
        return savingBkg;
    }

    public void showErrorPopup(){
        new MaterialDialog.Builder(this)
                .title(R.string.no_internet_title)
                .content(R.string.error_data_load)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .negativeText(R.string.feedback)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        StaticUtils.sendFeedbackEmail(ActivityMain.this);
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        goHome();
                    }
                })
                .show();
    }
}
