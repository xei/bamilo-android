package pt.rocket.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ScrollView;
import de.akquinet.android.androlog.Log;

/**
 * This Class implements and manage the Expandable and Collapse Animation <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Manuel Silva
 * @version 1.01
 * 
 * 2012/06/19
 * 
 */
public class ExpandCollapseAnimation extends Animation {
    private View mAnimatedView;
    private View headerView;
    private ScrollView mScrollView;
    private int mEndHeight;
    private int mType;
    int x = 0;
    int y = 0;
    int coor[] = {x,y};
    /**
     * Initializes expand collapse animation, has two types, collapse (1) and expand (0).
     * @param view The view to animate
     * @param duration
     * @param type The type of animation: 0 to close, 1 to open
     * @param endHeight the final height
     * 1 will collapse view and set to gone
     */
    public ExpandCollapseAnimation(View view, ScrollView scroll, View hView,int duration, int type, int endHeight) {
        setDuration(duration);
        mAnimatedView = view;
        headerView = hView;
        mScrollView = scroll;
        mEndHeight = endHeight;
        headerView.getLocationOnScreen(coor);
        mType = type;
        if(mType == 0) {
            mAnimatedView.getLayoutParams().height = 0;
            mAnimatedView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1.0f) {
            if(mType == 0) {
                mAnimatedView.getLayoutParams().height = (int) (mEndHeight * interpolatedTime);
            } else {
                mAnimatedView.getLayoutParams().height = mEndHeight - (int) (mEndHeight * interpolatedTime);
            }
            mAnimatedView.requestLayout();
        } else {
            if(mType == 0) {
                mAnimatedView.getLayoutParams().height = mEndHeight;
                mAnimatedView.requestLayout();
                
                
                mScrollView.scrollTo(mAnimatedView.getLeft(), (coor[1]-150));
                Log.i("ANIMATION", "x: "+mAnimatedView.getLeft()+" y: "+coor[1]);
            } else {
                mAnimatedView.getLayoutParams().height = 0;
                mAnimatedView.setVisibility(View.GONE);
                mAnimatedView.requestLayout();
                mAnimatedView.getLayoutParams().height = mEndHeight;
            }
        }
    }
}