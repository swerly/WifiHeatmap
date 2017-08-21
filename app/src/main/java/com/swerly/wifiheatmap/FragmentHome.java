package com.swerly.wifiheatmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Seth on 7/6/2017.
 */

public class FragmentHome extends FragmentBase{
    private TextView text;

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
        text = view.findViewById(R.id.home_text);
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
        text.setText(Integer.toString(app.getHeatmaps().size()));
        //TODO: display list of heatmaps

        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
