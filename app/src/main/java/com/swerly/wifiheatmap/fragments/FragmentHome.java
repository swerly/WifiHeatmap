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

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.adapters.HomeAdapter;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.utils.CacheHelper;
import com.swerly.wifiheatmap.utils.LoadCacheTask;
import com.swerly.wifiheatmap.utils.ShareBitmap;
import com.swerly.wifiheatmap.utils.StaticUtils;
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
        HeatmapDataViewHolder.HeatmapCardListener, LoadCacheTask.CacheLoadCallback {
    private RecyclerView rv;
    private HomeAdapter adapter;
    private View noHeatmapView;
    private ArrayList<HeatmapData> heatmapList;

    public static FragmentHome newInstance(){
        return new FragmentHome();
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
        ImageView noHeatmapsSpinner = noHeatmapView.findViewById(R.id.spinning_logo);
        StaticUtils.playAnimatedVectorDrawable(noHeatmapsSpinner);
        startLoadingSpinner(view);

        handleRatingPopup();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        hideSubtitle();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activityMain.getApp().resetDataToEdit();

        setupList();

        activityMain.getApp().deleteInProgressBkg();
        //TODO: rating popup
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
            case R.id.action_feedback:
                StaticUtils.sendFeedbackEmail(activityMain);
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
        bundle.putString("name",  item.getName());

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

        BaseApplication app = activityMain.getApp();
        app.setDataToEdit(item);

        //go to the heatmap fragment to edit
        FragmentHeatmap goTo = new FragmentHeatmap();
        goTo.setArguments(bundle);

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
                        heatmapList.remove(item);
                        adapter.updateItems(heatmapList, null);
                        activityMain.getApp().setModifiedList(heatmapList);
                        updateList();
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
     *
     * @param type type of data that was loaded
     * @param data data that was loaded
     */
    @Override
    public void dataLoaded(String type, Object data) {
        heatmapList = data == null ? new ArrayList<HeatmapData>() : (ArrayList<HeatmapData>) data;
        updateList();
    }

    /**
     * sets up the list of heatmaps that have been created by the user
     */
    public void setupList(){
        //get the application (should have heatmap data)
        BaseApplication app = activityMain.getApp();
        //if the app is null (it shouldnt be), the loaded data is null, else its the loaded data
        ArrayList<HeatmapData> loadedHeatmaps = app == null ? null : app.getHeatmaps();

        //if the loaded heatmaps are null and current list is null, start loading
        if (loadedHeatmaps == null && heatmapList == null){
            showLoading();
            new LoadCacheTask(getActivity(), this).execute(CacheHelper.HEATMAP_LIST);
        } else {
            //if loaded heatmaps arent null, set them as the current list
            if (loadedHeatmaps != null){
                heatmapList = loadedHeatmaps;
            }
            updateList();
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

    private void updateList(){
        hideLoading();
        //if list is null or empty, display the empty graphic
        if (heatmapList == null || heatmapList.isEmpty()){
            rv.setVisibility(View.GONE);
            noHeatmapView.setVisibility(View.VISIBLE);
        }
        //else hide the empty graphic and update the list
        else {
            rv.setVisibility(View.VISIBLE);
            noHeatmapView.setVisibility(View.GONE);
            adapter.updateItems(heatmapList, null);
        }
    }

    private void handleRatingPopup(){
        SharedPreferences prefs = activityMain.getSharedPreferences(BaseApplication.PREFS, 0);
        final SharedPreferences.Editor prefEditor = prefs.edit();
        boolean isFirstRun = prefs.getBoolean(BaseApplication.IS_FIRST_RUN, true);
        boolean dontAskRating = prefs.getBoolean(BaseApplication.DONT_ASK_RATING, false);

        if (isFirstRun || dontAskRating){
            //don't show popup
            //set not first run
            if (isFirstRun) {
                prefEditor.putBoolean(BaseApplication.IS_FIRST_RUN, false);
                prefEditor.commit();
            }
        } else {
            new MaterialDialog.Builder(activityMain)
                    .title(R.string.rating_title)
                    .content(R.string.rating_content)
                    .positiveText(R.string.rate)
                    .negativeText(R.string.feedback)
                    .neutralText(R.string.close)
                    .checkBoxPromptRes(R.string.dont_show_again, false, new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            prefEditor.putBoolean(BaseApplication.DONT_ASK_RATING, b);
                            prefEditor.commit();
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            activityMain.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.swerly.wifiheatmap")));
                            dialog.dismiss();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            StaticUtils.sendFeedbackEmail(activityMain);
                        }
                    })
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
}
