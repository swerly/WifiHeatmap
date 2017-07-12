package com.swerly.wifiheatmap;

import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(activityMain, "Search Pressed", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.action_location:
                Toast.makeText(activityMain, "Location Pressed", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.action_help:
                Toast.makeText(activityMain, "Help Pressed", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return false;
    }
}
