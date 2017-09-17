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

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.adapters.HomeAdapter;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.utils.ShareBitmap;
import com.swerly.wifiheatmap.views.HeatmapDataViewHolder;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Seth on 7/6/2017.
 *
 * First fragment to display
 * Will show an animation when no heatmaps have been created
 * Will show a list of cards for each individual heatmap when there is > 0 created
 */

public class FragmentHome extends FragmentBase implements
        HeatmapDataViewHolder.HeatmapCardListener{
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

        //setup the view that will be shown if no heatmaps exist
        noHeatmapView = view.findViewById(R.id.no_heatmap_view);
        //setup the recycler view
        rv = view.findViewById(R.id.home_recycler_view);
        setupRecyclerView();

        //start the loading icon animation (repeats, set in the xml of the icon)
        ImageView loadingIcon = noHeatmapView.findViewById(R.id.spinning_logo);
        Drawable spinner = loadingIcon.getDrawable();
        if (spinner instanceof Animatable){
            ((Animatable) spinner).start();
        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //if info was selected, go to the info fragment
            case R.id.action_info:
                activityMain.goToFragment(new FragmentInfo());
                break;
            //if sort was selected, create the sort popup
            case R.id.action_sort:
                View sortButton = activityMain.findViewById(R.id.action_sort);
                createSortPopup(sortButton);
                break;
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        //this fragment has nothing to do when back is pressed
        return false;
    }

    @Override
    public void onFabPressed() {
        //no actions need to be taken before we advance to the next fragment
    }

    @Override
    public void onResume(){
        super.onResume();
        hideSubtitle();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        app.setNotEditing();

        checkHeatmapsExist();
    }

    /**
     * setup the recycler view and adapter for showing heatmap cards
     */
    private void setupRecyclerView(){
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        adapter = new HomeAdapter(activityMain, this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onViewPressed(HeatmapData item) {
        //setup the bundle
        Bundle bundle = new Bundle();
        bundle.putInt("position",  app.getHeatmaps().indexOf(item));

        //set and start the fragment
        FragmentBase viewFrag = new FragmentView();
        viewFrag.setArguments(bundle);
        activityMain.goToFragment(viewFrag);
    }

    @Override
    public void onSharePressed(HeatmapData item) {
        new ShareBitmap(activityMain).execute(item.getFinishedImage());
    }

    @Override
    public void onEditPressed(HeatmapData item) {
        //set the string of the pixel data to load
        Bundle bundle = new Bundle();
        bundle.putString("toLoad", item.getPixelsFileName());

        //go to the heatmap fragment to edit
        FragmentHeatmap goTo = new FragmentHeatmap();
        goTo.setArguments(bundle);
        //setup the app for editing
        app.setBackgroundInProgress(item.getBackgroundImage());
        app.setBackgroundReady();
        app.setCurrentInProgress(item);
        app.setIsEditing();

        activityMain.goToFragment(goTo);
    }

    @Override
    public void onDeletePressed(final HeatmapData item) {
        //create new dialog to confirm deletion
        new MaterialDialog.Builder(getContext())
                .title(R.string.delete_heatmap_title)
                .content(R.string.delete_heatmap_content)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //create a snackbar
                        String snackbarMessage = item.getName() + " " + getString(R.string.removed);
                        Snackbar snackbar = Snackbar.make(activityMain.findViewById(R.id.main_layout), snackbarMessage, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        //delete the item and update the adapter
                        app.deleteFromList(item);
                        adapter.updateItems(app.getHeatmaps(), null);
                        checkHeatmapsExist();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    /**
     * checks to see if heatmaps exist in the application and handles showing the empty graphic
     */
    public void checkHeatmapsExist(){
        heatmapList = app.getHeatmaps();
        //if list is null or empty, display the empty graphic
        if (heatmapList == null || heatmapList.isEmpty()){
            rv.setVisibility(View.GONE);
            noHeatmapView.setVisibility(View.VISIBLE);
        }
        //else hid the empty graphic and update the list
        else {
            rv.setVisibility(View.VISIBLE);
            noHeatmapView.setVisibility(View.GONE);
            adapter.updateItems(heatmapList, null);
        }
    }

    /**
     * Creates a popup asking the user how they want to sort the list
     * @param sortView parent view to expand popup from
     */
    private void createSortPopup(View sortView){
        //create the popup menu with specific click listeners
        PopupMenu sortPopupMenu = new PopupMenu(getActivity(), sortView);
        sortPopupMenu.inflate(R.menu.sort_popup);
        sortPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ArrayList<HeatmapData> heatmapList = app.getHeatmaps();
                switch (item.getItemId()){
                    case R.id.sort_name:
                        adapter.updateItems(heatmapList, HeatmapData.getComparator(HeatmapData.HeatmapDataComparator.NAME_SORT));
                        break;
                    case R.id.sort_date:
                        adapter.updateItems(heatmapList, HeatmapData.getComparator(HeatmapData.HeatmapDataComparator.DATE_SORT));
                        break;
                }
                return true;
            }
        });
        sortPopupMenu.show();
    }
}
