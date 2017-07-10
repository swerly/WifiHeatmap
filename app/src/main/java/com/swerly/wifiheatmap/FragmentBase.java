package com.swerly.wifiheatmap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public abstract class FragmentBase extends Fragment {
    public static final String HOME_FRAGMENT = "FragmentHome";
    public static final String MAP_FRAGMENT = "FragmentMap";
    public static final String BOUNDRY_FRAGMENT = "FragmentBoundry";
    public static final String HEATMAP_FRAGMENT = "FragmentHeatmap";
    public static final String NAME_FRAGMENT = "FragmentName";

    public static final String SEQUENCE_ENDING_FRAGMENT = NAME_FRAGMENT;

    protected ActivityMain activityMain;
    protected ActionBarHelper actionBarHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMain = (ActivityMain) getActivity();
        actionBarHelper = new ActionBarHelper();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        actionBarHelper.setupForFragment(this, menu, inflater);
    }

    public abstract boolean onOptionsItemSelected(MenuItem item);
}
