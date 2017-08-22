package com.swerly.wifiheatmap;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

/**
 * Created by Seth on 7/6/2017.
 */

public class FragmentHome extends FragmentBase{
    private RecyclerView rv;
    private HomeAdapter adapter;
    private View noHeatmapView;
    private ArrayList<HeatmapData> heatmapList;

    public static FragmentHome newInstance(){
        return new FragmentHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        noHeatmapView = view.findViewById(R.id.no_heatmap_view);
        rv = view.findViewById(R.id.home_recycler_view);
        setupRecyclerView();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        actionBarHelper.setupForFragment(this, menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_info:
                activityMain.goToFragment(new FragmentInfo());
                break;
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onFabPressed() {
    }

    @Override
    public void onResume(){
        super.onResume();
        hideSubtitle();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        heatmapList = app.getHeatmaps();
        if (heatmapList == null || heatmapList.isEmpty()){
            rv.setVisibility(View.GONE);
            noHeatmapView.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            noHeatmapView.setVisibility(View.GONE);
            adapter.setNewData(heatmapList);
        }
    }

    private void setupRecyclerView(){
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        adapter = new HomeAdapter();
        rv.setAdapter(adapter);
    }
}
