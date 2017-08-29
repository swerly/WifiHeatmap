/*
 * Copyright (c) 2017 Seth Werly.
 *
 * This file is part of WifiHeatmap.
 *
 *     WifiHeatmap is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     WifiHeatmap is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with WifiHeatmap.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.swerly.wifiheatmap.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.activities.ActivityMain;
import com.swerly.wifiheatmap.fragments.FragmentBase;
import com.swerly.wifiheatmap.fragments.FragmentHeatmap;
import com.swerly.wifiheatmap.fragments.FragmentHome;
import com.swerly.wifiheatmap.fragments.FragmentInfo;
import com.swerly.wifiheatmap.fragments.FragmentMap;
import com.swerly.wifiheatmap.fragments.FragmentView;
import com.swerly.wifiheatmap.fragments.FragmentZoom;

/**
 * Created by Seth on 8/26/2017.
 */

public class HelpScreenHelper {
    private ActivityMain activityMain;
    private View helpView, signalInfoView, signalInfoExtendedView;
    private TextView titleText, contentText, whenDoneText;
    private Button closeButton, moreSignalInfoButton, signalInfoBackButton;
    private CheckBox showHelpCheckbox;
    private SharedPreferences prefs;
    private String currentClassName;

    public HelpScreenHelper(ActivityMain activityMain){
        this.activityMain = activityMain;
        helpView = activityMain.findViewById(R.id.help_view);
        titleText = helpView.findViewById(R.id.help_title);
        contentText = helpView.findViewById(R.id.help_content);
        whenDoneText = helpView.findViewById(R.id.when_done_text);
        closeButton = helpView.findViewById(R.id.help_close_button);
        showHelpCheckbox = helpView.findViewById(R.id.default_show_checkbox);
        signalInfoView = helpView.findViewById(R.id.signal_info_condensed);
        signalInfoExtendedView = helpView.findViewById(R.id.info_scroll_view);
        moreSignalInfoButton = helpView.findViewById(R.id.signal_info_button);
        signalInfoBackButton = helpView.findViewById(R.id.signal_info_back_button);

        prefs = activityMain.getPreferences(Context.MODE_PRIVATE);
        setButtonClose();
        setMoreInfoButton();
        setCheckboxListener();
    }

    public void setupForFragmentAndHide(FragmentBase frag){
        setupForFragment(frag, true);
        hideHelp();
    }
    public void setupForFragment(FragmentBase setupFor, boolean hide){
        if (setupFor instanceof FragmentHome || setupFor instanceof FragmentInfo){
            hideHelp();
            return;
        }

        currentClassName = setupFor.getClass().getSimpleName();
        boolean curPref = getCheckboxPreference(currentClassName);

        if (setupFor instanceof FragmentMap){
            titleText.setText(R.string.help_map_title);
            contentText.setText(R.string.help_map_content);
            contentText.setVisibility(View.VISIBLE);
            whenDoneText.setText(R.string.completed_to_next);
            whenDoneText.setVisibility(View.VISIBLE);
            signalInfoView.setVisibility(View.GONE);
            signalInfoExtendedView.setVisibility(View.GONE);
            signalInfoBackButton.setVisibility(View.GONE);
        } else if (setupFor instanceof FragmentZoom){
            titleText.setText(R.string.help_zoom_title);
            contentText.setText(R.string.help_zoom_content);
            contentText.setVisibility(View.VISIBLE);
            whenDoneText.setText(R.string.completed_to_next);
            whenDoneText.setVisibility(View.VISIBLE);
            signalInfoView.setVisibility(View.GONE);
            signalInfoExtendedView.setVisibility(View.GONE);
            signalInfoView.setVisibility(View.GONE);
            signalInfoBackButton.setVisibility(View.GONE);
        } else if (setupFor instanceof FragmentHeatmap){
            titleText.setText(R.string.help_heatmap_title);
            contentText.setText(R.string.help_heatmap_content);
            contentText.setVisibility(View.VISIBLE);
            whenDoneText.setText(R.string.completed_to_save);
            whenDoneText.setVisibility(View.VISIBLE);
            signalInfoView.setVisibility(View.VISIBLE);
            signalInfoExtendedView.setVisibility(View.GONE);
            moreSignalInfoButton.setVisibility(View.VISIBLE);
            signalInfoBackButton.setVisibility(View.GONE);
        } else if (setupFor instanceof FragmentView){
            titleText.setText(R.string.more_signal_info_title);
            contentText.setVisibility(View.GONE);
            whenDoneText.setVisibility(View.GONE);
            signalInfoView.setVisibility(View.VISIBLE);
            moreSignalInfoButton.setVisibility(View.GONE);
            signalInfoExtendedView.setVisibility(View.VISIBLE);
            signalInfoBackButton.setVisibility(View.GONE);
        }

        if (curPref || hide){
            hideHelp();
        } else {
            showHelp();
        }
    }

    public boolean isHelpVisible(){
        return helpView.getVisibility() == View.VISIBLE;
    }

    public void showHelp(){
        helpView.setVisibility(View.VISIBLE);
        setCheckbox();
    }

    public void hideHelp(){
        helpView.setVisibility(View.GONE);
    }

    private boolean getCheckboxPreference(String className){
        return prefs.getBoolean(className, false);
    }

    private void setCheckboxPreference(boolean toSet){
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean(currentClassName, toSet);
        prefEditor.commit();
    }

    private void setCheckbox(){
        boolean curPref = getCheckboxPreference(currentClassName);
        showHelpCheckbox.setChecked(curPref);
    }

    private void setButtonClose(){
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpView.setVisibility(View.GONE);
            }
        });
    }

    private void setMoreInfoButton(){
        moreSignalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleText.setText(R.string.more_signal_info_title);
                contentText.setVisibility(View.GONE);
                whenDoneText.setVisibility(View.GONE);
                moreSignalInfoButton.setVisibility(View.GONE);
                signalInfoExtendedView.setVisibility(View.VISIBLE);
                signalInfoBackButton.setVisibility(View.VISIBLE);
            }
        });

        signalInfoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleText.setText(R.string.help_heatmap_title);
                contentText.setText(R.string.help_heatmap_content);
                contentText.setVisibility(View.VISIBLE);
                whenDoneText.setText(R.string.completed_to_save);
                whenDoneText.setVisibility(View.VISIBLE);
                signalInfoView.setVisibility(View.VISIBLE);
                signalInfoExtendedView.setVisibility(View.GONE);
                moreSignalInfoButton.setVisibility(View.VISIBLE);
                signalInfoBackButton.setVisibility(View.GONE);
            }
        });
    }

    private void setCheckboxListener(){
        showHelpCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setCheckboxPreference(isChecked);
            }
        });
    }
}
