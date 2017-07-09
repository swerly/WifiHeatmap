package com.swerly.wifiheatmap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swerly.wifiheatmap.R;


public abstract class FragmentBase extends Fragment {
    public static final String HOME_FRAGMENT = "FragmentHome";
    public static final String MAP_FRAGMENT = "FragmentMap";
    public static final String BOUNDRY_FRAGMENT = "FragmentBoundry";
    public static final String HEATMAP_FRAGMENT = "FragmentHeatmap";
    public static final String NAME_FRAGMENT = "FragmentName";

    public static final String SEQUENCE_ENDING_FRAGMENT = NAME_FRAGMENT;


    protected FloatingActionButton mainFab;
    protected MainActivity mainActivity;

    private FabHelper fabHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mainFab = mainActivity.findViewById(R.id.fab);
        fabHelper = new FabHelper(mainActivity, mainFab);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fabHelper.setupFab(this);
        return null;
    }

}
