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
    private View helpView;
    private TextView titleText, contentText, whenDoneText;
    private Button closeButton;
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

        prefs = activityMain.getPreferences(Context.MODE_PRIVATE);
        setButtonClose();
        setCheckboxListener();
    }

    public void setupForFragment(FragmentBase setupFor){
        if (setupFor instanceof FragmentHome || setupFor instanceof FragmentInfo){
            return;
        }

        currentClassName = setupFor.getClass().getSimpleName();
        boolean curPref = getCheckboxPreference(currentClassName);

        if (setupFor instanceof FragmentMap){
            titleText.setText(R.string.help_map_title);
            contentText.setText(R.string.help_map_content);
            whenDoneText.setText(R.string.completed_to_next);
        } else if (setupFor instanceof FragmentZoom){
            titleText.setText(R.string.help_zoom_title);
            contentText.setText(R.string.help_zoom_content);
            whenDoneText.setText(R.string.completed_to_next);
        } else if (setupFor instanceof FragmentHeatmap){
            titleText.setText(R.string.help_heatmap_title);
            contentText.setText(R.string.help_heatmap_content);
            whenDoneText.setText(R.string.completed_to_save);
        } else if (setupFor instanceof FragmentView){

        }

        if (curPref){
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

    private void setButtonBack(){
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpView.setVisibility(View.GONE);
            }
        });
    }

    private void setButtonClose(){
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpView.setVisibility(View.GONE);
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
