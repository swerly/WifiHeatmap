package com.swerly.wifiheatmap.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.swerly.wifiheatmap.BuildConfig;
import com.swerly.wifiheatmap.R;

/**
 * Created by Seth on 8/20/2017.
 */

public class FragmentInfo extends FragmentBase {
    private Button feedbackButton;
    private TextView versionName;
    private Button licenseButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        feedbackButton = view.findViewById(R.id.feedback_button);
        versionName = view.findViewById(R.id.version_text_view);
        licenseButton = view.findViewById(R.id.licenses_button);
        setupFeedback();
        setupVersion();
        setupLicenses();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubTitle(R.string.info_subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onFabPressed() {

    }

    private void setupFeedback(){
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","seth.werly@gmail.com", null));

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WifiHeatmap Feedback");
                startActivity(Intent.createChooser(emailIntent, "Send feedback..."));
            }
        });
    }

    private void setupVersion(){
        String versionNameText = BuildConfig.VERSION_NAME;
        versionName.setText("Version: " + versionNameText);
    }

    private void setupLicenses(){
        licenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebView licenseView = (WebView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_licenses, null);
                licenseView.loadUrl("file:///android_asset/open_source_licenses.html");
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.open_source_licenses))
                        .setView(licenseView)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });
    }
}
