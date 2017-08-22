package com.swerly.wifiheatmap.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.swerly.wifiheatmap.BaseApplication;

/**
 * Created by Seth on 7/3/2017.
 *
 * Base Activity that all other activities will inherit from
 * (right now only one activity exists, but for future reference...)
 */

public class ActivityBase extends AppCompatActivity {
    protected BaseApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (BaseApplication) getApplication();
    }
}
