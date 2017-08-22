package com.swerly.wifiheatmap.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.swerly.wifiheatmap.utils.ActionBarHelper;
import com.swerly.wifiheatmap.activities.ActivityMain;
import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.R;

public abstract class FragmentBase extends Fragment {
    public static final String HOME_FRAGMENT = "FragmentHome";
    public static final String MAP_FRAGMENT = "FragmentMap";
    public static final String HEATMAP_FRAGMENT = "FragmentHeatmap";
    public static final String ZOOM_FRAGMENT = "FragmentZoom";
    public static final String IFNO_FRAGMENT = "FragmentInfo";

    protected ActivityMain activityMain;
    protected ActionBarHelper actionBarHelper;
    protected BaseApplication app;

    private String subTitleToSet;
    private TextView subTitle;
    private AlphaAnimation fadeIn;
    private AlphaAnimation fadeOut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMain = (ActivityMain) getActivity();
        app = activityMain.getApp();
        actionBarHelper = new ActionBarHelper();

        setupSubTitle();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return null;
    }

    private void setupSubTitle(){
        subTitle = getActivity().findViewById(R.id.subtitle);
        fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(300);
        fadeIn.setDuration(300);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                subTitle.setText(subTitleToSet);
                subTitle.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    protected void hideSubtitle(){
        subTitle.setText("");
        subTitle.setVisibility(View.GONE);
    }

    protected void setSubTitle(int resId){
        subTitleToSet = getString(resId);
        subTitle.setVisibility(View.VISIBLE);
        subTitle.startAnimation(fadeOut);
    }

    public abstract void onCreateOptionsMenu(Menu menu, MenuInflater inflater);
    public abstract boolean onOptionsItemSelected(MenuItem item);
    public abstract boolean onBackPressed();
    public abstract void onFabPressed();

}
