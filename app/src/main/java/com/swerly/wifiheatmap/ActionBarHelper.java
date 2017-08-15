package com.swerly.wifiheatmap;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Seth on 7/9/2017.
 */

public class ActionBarHelper {
    public static final int UNDO = R.id.action_undo;
    public static final int REDO = R.id.action_redo;
    public static final int LOCATION = R.id.action_location;
    public static final int SEARCH = R.id.action_search;
    public static final int FILTER = R.id.action_filter;
    public static final int HELP = R.id.action_help;

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
        } else if (fragment instanceof FragmentName){
            idToInflate = R.menu.toolbar_name;
        } else if (fragment instanceof FragmentZoom){
            idToInflate = R.menu.toolbar_zoom;
        } else {
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