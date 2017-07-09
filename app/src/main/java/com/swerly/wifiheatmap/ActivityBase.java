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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);

        int menLen = menu.size();
        for(int i = 0; i < menLen; i++){
            MenuItem item = menu.getItem(i);
            Drawable drawable = item.getIcon();
            if (drawable != null) {
                final Drawable wrapped = DrawableCompat.wrap(drawable);
                drawable.mutate();
                DrawableCompat.setTint(wrapped, getResources().getColor(R.color.white));
                item.setIcon(drawable);
            }
        }
        return true;
    }
}
