package com.swerly.wifiheatmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Seth on 7/6/2017.
 */

public class FragmentHome extends FragmentBase implements
        CacheHelper.CacheLoadCallbacks{

    private CacheHelper cacheHelper;

    public static FragmentHome newInstance(){
        return new FragmentHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheHelper = new CacheHelper(getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        actionBarHelper.setupForFragment(this, menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onFabPressed() {
        return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        cacheHelper.startCountLoad();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void heatmapListLoaded(ArrayList<HeatmapData> data) {

    }

    @Override
    public void heatmapCountLoaded(int count) {
        ((TextView)getActivity().findViewById(R.id.home_text)).setText(count);
    }

    @Override
    public void heatmapInProgressLoaded(HeatmapData inProgress) {

    }
}
