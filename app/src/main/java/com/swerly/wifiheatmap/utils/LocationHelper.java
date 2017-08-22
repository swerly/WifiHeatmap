package com.swerly.wifiheatmap.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.R;

/**
 * Created by Seth on 7/13/2017.
 */

public class LocationHelper implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int LOCATION_ENABLER_ID = 9100;
    private static final int MAX_SAMPLES = 5;
    private static final int EXPIRATION_DURATION = 10000;

    public static final int STATUS_OK = 0;
    public static final int STATUS_INACCURATE = 1;
    public static final int STATUS_API_ERROR = 2;
    public static final int STATUS_TIMEOUT = 3;

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Activity context;
    private LocationHelperCallback callback;
    private Location bestAccuracy;
    private Handler expirationTimer;
    private boolean started;

    private int sampleCount = 0;

    public LocationHelper(Activity context){
        this.context = context;
        reset();
    }

    public boolean requestLocation(LocationHelperCallback callback){
        this.callback = callback;

        if (!checkPlayServices()){
            Toast.makeText(context, context.getString(R.string.play_services_unavailable), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        //setup the google api client
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        //setup the location request
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(MAX_SAMPLES)
                .setInterval(200);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(this);
        return true;
    }
    private boolean gpsEnabled(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps, network;
        gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps && network;
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(context, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(BaseApplication.DEBUG_MESSAGE, "google services connected");
    }

    @Override
    public void onLocationChanged(Location location) {
        sampleCount++;
        if (bestAccuracy == null || location.getAccuracy() < bestAccuracy.getAccuracy()){
            bestAccuracy = location;
        }

        if (bestAccuracy.getAccuracy() < 10){
            sendBest(STATUS_OK);
        }

        if (sampleCount >= MAX_SAMPLES){
            sendBest(STATUS_INACCURATE);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(BaseApplication.DEBUG_MESSAGE, "GoogleApiClient connection has been suspended");
        displayStatus(STATUS_API_ERROR);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(BaseApplication.DEBUG_MESSAGE, "GoogleApiClient connection failed");
        displayStatus(STATUS_API_ERROR);
    }

    /**
     * Callback when done checking to see if location settings are enabled
     * @param locationSettingsResult
     */
    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        final LocationSettingsStates state = locationSettingsResult.getLocationSettingsStates();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // All location settings are satisfied. The client can initialize location
                startRequestingUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied. But could be fixed by showing the user
                // a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            context, LOCATION_ENABLER_ID);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Toast.makeText(context, context.getString(R.string.change_location_settings), Toast.LENGTH_SHORT)
                        .show();
                reset();
                break;
        }
    }

    private void sendBest(int status){
        callback.gotLocation(bestAccuracy);
        displayStatus(status);
    }

    public void returnFromSettings(){
        if (gpsEnabled()){
            startRequestingUpdates();
        } else {
            Toast.makeText(context, context.getString(R.string.location_not_enabled), Toast.LENGTH_SHORT)
                    .show();
            reset();
        }
    }

    @SuppressLint("MissingPermission")
    private void startRequestingUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        startExpirationTimer();
        started = true;
    }

    private void startExpirationTimer(){
        expirationTimer = new Handler();
        expirationTimer.postDelayed(new Runnable() {
            @Override
            public void run() {
                timerExpired();
            }
        }, EXPIRATION_DURATION);
    }

    private void timerExpired(){
        if (bestAccuracy == null){
            displayStatus(STATUS_TIMEOUT);
        } else {
            sendBest(STATUS_INACCURATE);
        }
    }

    public void stopLocationUpdates(){
        if (started) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            expirationTimer.removeCallbacksAndMessages(null);
            reset();
        }
    }

    private void reset(){
        bestAccuracy = null;
        sampleCount = 0;
        started = false;
    }

    private void displayStatus(int status){
        stopLocationUpdates();
        switch(status){
            case STATUS_OK:
                Toast.makeText(context, context.getString(R.string.location_status_ok), Toast.LENGTH_LONG)
                        .show();
                break;
            case STATUS_API_ERROR:
                Toast.makeText(context, context.getString(R.string.location_status_api_error), Toast.LENGTH_LONG)
                        .show();
                break;
            case STATUS_INACCURATE:
                Toast.makeText(context, context.getString(R.string.location_status_inaccurate), Toast.LENGTH_LONG)
                        .show();
                break;
            case STATUS_TIMEOUT:
                Toast.makeText(context, context.getString(R.string.location_status_timeout), Toast.LENGTH_LONG)
                        .show();
                break;
        }
    }

    public interface LocationHelperCallback {
        void gotLocation(Location location);
        void returnFromSettings();
    }
}
