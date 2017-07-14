package com.swerly.wifiheatmap;

import android.location.Location;

/**
 * Created by Seth on 7/12/2017.
 */

public interface LocationHelperCallback {
    void gotLocation(Location location);
    void returnFromSettings();
}
