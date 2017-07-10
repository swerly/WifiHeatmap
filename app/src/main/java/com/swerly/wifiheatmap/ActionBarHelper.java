package com.swerly.wifiheatmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;

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
    
    private Context context;

    public void setupForFragment(Fragment frag, Menu menu){
        context = frag.getActivity();
        
        if (frag instanceof FragmentHome){
            hideIcon(menu, UNDO);
            hideIcon(menu, REDO);
            hideIcon(menu, LOCATION);
            hideIcon(menu, SEARCH);
        } else if (frag instanceof FragmentMap){
            hideIcon(menu, UNDO);
            hideIcon(menu, REDO);
            hideIcon(menu, FILTER);
        } else if (frag instanceof FragmentBoundry){
            hideIcon(menu, LOCATION);
            hideIcon(menu, SEARCH);
            hideIcon(menu, FILTER);
        } else if (frag instanceof FragmentHeatmap){
            hideIcon(menu, LOCATION);
            hideIcon(menu, SEARCH);
            hideIcon(menu, FILTER);
        } else if (frag instanceof FragmentName){
            hideIcon(menu, UNDO);
            hideIcon(menu, REDO);
            hideIcon(menu, LOCATION);
            hideIcon(menu, SEARCH);
            hideIcon(menu, FILTER);
        } else {
            Log.d(BaseApplication.DEBUG_MESSAGE, "action bar helper fragment type unknown");
        }
    }

    private void hideIcon(Menu menu, int toHide){
        MenuItem itemToHide = menu.findItem(toHide);
        itemToHide.setVisible(false);
    }

}
