package com.swerly.wifiheatmap.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.swerly.wifiheatmap.BuildConfig;
import com.swerly.wifiheatmap.R;

/**
 * Created by Seth on 8/20/2017.
 */

public class FragmentInfo extends FragmentBase {
    private Button feedbackButton;
    private TextView versionName;

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
        setupFeedback();
        setupVersion();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        activityMain.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubTitle(R.string.info_subtitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

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
}
