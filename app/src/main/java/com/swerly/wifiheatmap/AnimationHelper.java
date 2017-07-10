package com.swerly.wifiheatmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by Seth on 7/9/2017.
 */

public class AnimationHelper {
    private Context context;

    public AnimationHelper(Context context){
        this.context = context;
    }

    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow)
    {
        final View myView = ((Activity)context).findViewById(viewID);

        int width=myView.getWidth();

        if(posFromRight>0)
            width -= (posFromRight *  context.getResources() . getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (context.getResources() . getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2 );

        if(containsOverflow)
            width-= context.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx=width;
        int cy=myView.getHeight()/2;

        Animator anim;
        if(isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0,(float)width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float)width, 0);

        anim.setDuration((long)220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isShow)
                {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if(isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();


    }
}
