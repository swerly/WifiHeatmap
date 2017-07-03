package com.swerly.wifiheatmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Seth on 7/3/2017.
 */

public class BaseActivity extends AppCompatActivity {
    private BaseApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (BaseApplication) getApplication();
    }
}
