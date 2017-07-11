package com.swerly.wifiheatmap;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Seth on 7/3/2017.
 *
 * Base Activity that all other activities will inherit from
 * (right now only one activity exists, but for future reference...)
 */

public class ActivityBase extends AppCompatActivity {
    private BaseApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (BaseApplication) getApplication();
    }
}
