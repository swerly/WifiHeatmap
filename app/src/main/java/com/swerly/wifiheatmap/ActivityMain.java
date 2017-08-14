package com.swerly.wifiheatmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import okhttp3.Cache;

public class ActivityMain extends ActivityBase{

    private FabHelper fabHelper;
    private FloatingActionButton mainFab;
    private FragmentManager fragmentManager;
    private HeatmapData currentInProgress;
    private int currentCount;
    private CacheHelper cacheHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentCount = 0;

        mainFab = findViewById(R.id.fab);
        fabHelper = new FabHelper(this, mainFab);
        cacheHelper = new CacheHelper(this, null);
        cacheHelper.startupLoad();

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
        boolean popped = getSupportFragmentManager().popBackStackImmediate();
        if (popped){
            FragmentBase curFrag = (FragmentBase) fragmentManager.findFragmentById(R.id.fragment_container);
            fabHelper.setupFab(curFrag, true);
        } else {
            finish();
        }
    }

    private boolean notifyFragmentBackPressed(){
        FragmentBase curFrag = (FragmentBase) fragmentManager.findFragmentById(R.id.fragment_container);
        return curFrag.onBackPressed();
    }

    public boolean notifyFragmentFabClick(){
        FragmentBase curFrag = (FragmentBase) fragmentManager.findFragmentById(R.id.fragment_container);
        curFrag.onFabPressed();
        return false;
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

            fabHelper.setupFab(frag, false);
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

    public void storeCount(){
        currentCount++;
        app.setCurrentCount(currentCount);

        //TODO: maybe move this to base application class?
        cacheHelper.saveCount(currentCount);
    }
}
