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

        noHeatmapView = view.findViewById(R.id.no_heatmap_view);
        rv = view.findViewById(R.id.home_recycler_view);
        setupRecyclerView();

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
            case R.id.action_info:
                activityMain.goToFragment(new FragmentInfo());
                break;
            case R.id.action_sort:
                View sortButton = activityMain.findViewById(R.id.action_sort);
                createSortPopup(sortButton);
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
        app.setNotEditing();

        checkHeatmapsExist();
    }

    private void setupRecyclerView(){
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        adapter = new HomeAdapter(this);
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
        Bundle bundle = new Bundle();
        bundle.putString("toLoad", item.getPixelsFileName());

        FragmentHeatmap goTo = new FragmentHeatmap();
        goTo.setArguments(bundle);
        app.setBackgroundInProgress(item.getBackgroundImage());
        app.setBackgroundReady();
        app.setCurrentInProgress(item);
        app.setIsEditing();

        activityMain.goToFragment(goTo);
    }

    @Override
    public void onDeletePressed(final HeatmapData item) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.delete_heatmap_title)
                .content(R.string.delete_heatmap_content)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String snackbarMessage = item.getName() + " " + getString(R.string.removed);
                        Snackbar snackbar = Snackbar.make(activityMain.findViewById(R.id.main_layout), snackbarMessage, Snackbar.LENGTH_LONG);
                        snackbar.show();
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

    public void checkHeatmapsExist(){
        heatmapList = app.getHeatmaps();
        if (heatmapList == null || heatmapList.isEmpty()){
            rv.setVisibility(View.GONE);
            noHeatmapView.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            noHeatmapView.setVisibility(View.GONE);
            adapter.updateItems(heatmapList, null);
        }
    }

    private void createSortPopup(View sortView){
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
