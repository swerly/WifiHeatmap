package com.swerly.wifiheatmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Seth on 7/13/2017.
 */

public class SearchBarView extends LinearLayout {
    private Context context;
    private View rootView;
    private ImageButton backArrow;
    private ImageButton clearText;
    private EditText searchText;
    private int radius, cx, cy;
    private View toolbar;

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
                    String text = searchText.getText().toString();
                    if (!text.equals("")) {
                        searchBarCallback.performSearch(text);
                        hideKeyboard();
                        return true;
                    } else {
                        Toast.makeText(context, context.getString(R.string.empty_search_string), Toast.LENGTH_SHORT)
                                .show();
                    }
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
        toolbar = ((Activity)context).findViewById(id);
        setRadius();
    }

    public void animateOpenFrom(View v){
        rootView.setVisibility(INVISIBLE);
        int[] revealLocation = new int[2];
        v.getLocationOnScreen(revealLocation);
        // get the center for the clipping circle
        cx = revealLocation[0] + v.getWidth()/2;
        cy = revealLocation[1];

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(rootView, cx, cy, 0, radius);

        anim.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animator){
                super.onAnimationEnd(animator);
                toolbar.setVisibility(GONE);
            }
        });

        // make the view visible and start the animation
        rootView.setVisibility(VISIBLE);
        anim.start();

        if(searchText.requestFocus()){
            showKeyboard();
        }
    }

    public boolean animateClose(){
        if(rootView.getVisibility() == View.VISIBLE){
            // create the animation (the final radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(rootView, cx, cy, radius, 0);

            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animator){
                    super.onAnimationStart(animator);
                    hideKeyboard();
                    toolbar.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    rootView.setVisibility(GONE);
                }
            });

            // start the animation
            anim.start();

            return true;
        } else {
            return false;
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

    private void setRadius(){
        radius = (int) Math.hypot(toolbar.getWidth(), toolbar.getHeight());
    }

    public interface SearchBarCallback {
        void performSearch(String searchText);
    }
}
