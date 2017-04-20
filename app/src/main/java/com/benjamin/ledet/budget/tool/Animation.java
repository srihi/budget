package com.benjamin.ledet.budget.tool;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.benjamin.ledet.budget.R;

/**
 * Created by benjamin on 17/04/2017.
 */

public class Animation {

    public static void slideDown(Context ctx, View v){

        android.view.animation.Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slideUp(Context ctx, View v){

        android.view.animation.Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }
}
