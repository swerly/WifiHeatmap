package com.swerly.wifiheatmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Seth on 7/6/2017.
 */

public class FragmentBoundry extends FragmentBase {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_boundry, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
