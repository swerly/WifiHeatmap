package com.swerly.wifiheatmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Seth on 7/6/2017.
 *
 * Allows the user to use google maps to find the location of the building they want to
 * create a wifi heatmap in
 */

public class FragmentMap extends FragmentBase{
    private MapController mapController;
    private SupportMapFragment mapFragment;

    public static FragmentHome newInstance(){
        return new FragmentHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapController = new MapController();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.map_main_layout, mapFragment)
                .commit();
        mapFragment.getMapAsync(mapController);

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
