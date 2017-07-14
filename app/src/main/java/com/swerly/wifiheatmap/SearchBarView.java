package com.swerly.wifiheatmap;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Seth on 7/13/2017.
 */

public class SearchBarView extends LinearLayout {
    private Context context;
    private View rootView;
    private ImageButton backArrow;
    private ImageButton clearText;
    private EditText searchText;

    private int toolbarId, thisId;

    private SearchBarCallback searchBarCallback;

    public SearchBarView(Context context) {
        super(context);
        init(context);
    }

    public SearchBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        this.setLayoutTransition(new LayoutTransition());

        rootView = inflate(context, R.layout.map_searchbar, this);
        backArrow = rootView.findViewById(R.id.back_arrow);
        clearText = rootView.findViewById(R.id.clear_text);
        searchText = rootView.findViewById(R.id.search_edittext);
        thisId = this.getId();

        initButtons();
        initEditText();
    }

    private void initButtons(){
        backArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                animateClose();
                hideKeyboard();
            }
        });

        clearText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
            }
        });
    }

    private void initEditText(){
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchBarCallback.performSearch(searchText.getText().toString());
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    clearText.setVisibility(View.VISIBLE);
                } else {
                    clearText.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void setSearchBarCallback(SearchBarCallback callback){
        this.searchBarCallback = callback;
    }

    public void setToolbarId(int id){
        this.toolbarId = id;
    }

    public void animateOpenFrom(View v){
        setViewVisibile(thisId);
        //TODO: animate open
        setViewGone(toolbarId);
        if(searchText.requestFocus()){
            showKeyboard();
        }
    }

    public void animateClose(){
        if(rootView.getVisibility() == View.VISIBLE){
            setViewGone(thisId);
            setViewVisibile(toolbarId);
        }
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }

    private void setViewGone(int id){
        ((Activity)context).findViewById(id).setVisibility(View.GONE);
    }

    private void setViewVisibile(int id){
        ((Activity)context).findViewById(id).setVisibility(View.VISIBLE);
    }

    public interface SearchBarCallback {
        void performSearch(String searchText);
    }
}
