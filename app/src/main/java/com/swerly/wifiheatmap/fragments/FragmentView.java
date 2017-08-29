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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.utils.ShareBitmap;
import com.swerly.wifiheatmap.utils.StaticUtils;

/**
 * Created by Seth on 8/22/2017.
 */

public class FragmentView extends FragmentBase {
    private String subtitle;
    private HeatmapData toView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_view, container, false);

        int indexToView = getArguments().getInt("position");
        toView = app.getHeatmaps().get(indexToView);
        subtitle = toView.getName();

        GestureImageView bkgView = view.findViewById(R.id.view_view);
        bkgView.setImageBitmap(toView.getFinishedImage());

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubTitle(subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_share:
                new ShareBitmap(activityMain).execute(toView.getFinishedImage());
                break;
            case R.id.action_help:
                activityMain.showHelp();
                break;
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onFabPressed() {

    }
}
