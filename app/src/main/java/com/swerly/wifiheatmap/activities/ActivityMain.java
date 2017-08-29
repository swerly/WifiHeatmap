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
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.utils.FabHelper;
import com.swerly.wifiheatmap.utils.HelpScreenHelper;
import com.swerly.wifiheatmap.utils.LocationHelper;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.fragments.FragmentBase;
import com.swerly.wifiheatmap.fragments.FragmentHeatmap;
import com.swerly.wifiheatmap.fragments.FragmentHome;
import com.swerly.wifiheatmap.fragments.FragmentMap;

public class ActivityMain extends ActivityBase{

    private FabHelper fabHelper;
    private FloatingActionButton mainFab;
    private FragmentManager fragmentManager;
    private HelpScreenHelper helpScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    public void onBackPressed() {
        if(notifyFragmentBackPressed()){
            return;
        }else {
            backNavigation();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        backNavigation();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case LocationHelper.LOCATION_ENABLER_ID:
                FragmentBase mapFrag = (FragmentBase) fragmentManager.findFragmentByTag(FragmentBase.MAP_FRAGMENT);
                ((FragmentMap)mapFrag).returnFromSettings();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void backNavigation(){
        if (helpScreen.isHelpVisible()){
            helpScreen.hideHelp();
            return;
        }
        FragmentBase toPop = (FragmentBase) fragmentManager.findFragmentById(R.id.fragment_container);
        boolean fromHeatmap = toPop instanceof FragmentHeatmap;
        boolean popped = getSupportFragmentManager().popBackStackImmediate();
        if (popped){
            FragmentBase curFrag = (FragmentBase) fragmentManager.findFragmentById(R.id.fragment_container);
            if (curFrag instanceof FragmentMap){
                app.resetCurrent();
            }
            fabHelper.setupFab(curFrag, true, fromHeatmap);
            setupHelpForFragmentAndHide(curFrag);
        } else {
            finish();
        }
    }

    private boolean notifyFragmentBackPressed(){
        FragmentBase curFrag = (FragmentBase) fragmentManager.findFragmentById(R.id.fragment_container);
        return curFrag.onBackPressed();
    }

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
        helpScreen.setupForFragment(frag, false);

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

            fabHelper.setupFab(frag, false, false);
        }
    }

    /**
     * Pops all the backstack entries so we are left with the first fragment (home)
     */
    private void goHome(){
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

    public void setupHelpForFragmentAndHide(FragmentBase frag){
        helpScreen.setupForFragmentAndHide(frag);
    }
}
