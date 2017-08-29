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

package com.swerly.wifiheatmap.utils;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.fragments.FragmentHeatmap;
import com.swerly.wifiheatmap.fragments.FragmentHome;
import com.swerly.wifiheatmap.fragments.FragmentInfo;
import com.swerly.wifiheatmap.fragments.FragmentMap;
import com.swerly.wifiheatmap.fragments.FragmentView;
import com.swerly.wifiheatmap.fragments.FragmentZoom;

/**
 * Created by Seth on 7/9/2017.
 */

public class ActionBarHelper {

    private Fragment fragment;
    private Menu menu;

    public void setupForFragment(Fragment fragment, Menu menu, MenuInflater inflater){
        this.menu = menu;
        this.fragment = fragment;

        int idToInflate;

        //TODO: set fade animations for icons
        if (fragment instanceof FragmentHome){
            idToInflate = R.menu.toolbar_home;
        } else if (fragment instanceof FragmentMap){
            idToInflate = R.menu.toolbar_map;
        } else if (fragment instanceof FragmentHeatmap){
            idToInflate = R.menu.toolbar_heatmap;
        } else if (fragment instanceof FragmentZoom){
            idToInflate = R.menu.toolbar_zoom;
        } else if (fragment instanceof FragmentInfo){
            idToInflate = R.menu.toolbar_info;
        } else if (fragment instanceof FragmentView){
            idToInflate = R.menu.toolbar_view;
        }
        else {
            idToInflate = 0;
            Log.d(BaseApplication.DEBUG_MESSAGE, "action bar helper fragment type unknown");
        }

        inflater.inflate(idToInflate, menu);

        setColorWhite(menu);
    }

    private void setColorWhite(Menu menu){
        int menLen = menu.size();
        for(int i = 0; i < menLen; i++){
            MenuItem item = menu.getItem(i);
            Drawable drawable = item.getIcon();
            if (drawable != null) {
                final Drawable wrapped = DrawableCompat.wrap(drawable);
                drawable.mutate();
                DrawableCompat.setTint(wrapped, fragment.getResources().getColor(R.color.white));
                item.setIcon(drawable);
            }
        }
    }
}