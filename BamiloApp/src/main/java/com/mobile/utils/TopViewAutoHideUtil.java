package com.mobile.utils;

import android.animation.ValueAnimator;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.view.View;
import android.view.ViewGroup;

public class TopViewAutoHideUtil {
    private int minScrollRange, maxScrollRange;
    private int viewScrolledAmount, totalScrolled = 0;
    private View view;
    private ViewPropertyAnimatorCompat mScrollAnimator;
    private ValueAnimator mValueAnimator;
    private OnViewShowHideListener onViewShowHideListener;


    public TopViewAutoHideUtil(int minScrollRange, int maxScrollRange, View view) {
        this.minScrollRange = minScrollRange;
        this.maxScrollRange = maxScrollRange;
        this.view = view;
    }

    public void onScroll(int dy) {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
        totalScrolled += dy;
        int totalDy = viewScrolledAmount;
        totalDy += dy;
        int offset = constrain(totalDy, minScrollRange, maxScrollRange);
        viewScrolledAmount = offset;
        scrollPosition(offset);
    }

    private void scrollPosition(int offset) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.topMargin = offset;
        view.setLayoutParams(params);
        totalScrolled += offset - viewScrolledAmount;
        if (onViewShowHideListener != null) {
            if (viewScrolledAmount == minScrollRange) {
                onViewShowHideListener.onViewHid();
            } else if (viewScrolledAmount == maxScrollRange) {
                onViewShowHideListener.onViewShowed();
            }
        }
    }

    public void onStopScroll(boolean scrollingToTop) {
        if ((Math.abs(viewScrolledAmount) >= Math.abs(minScrollRange)) && scrollingToTop) {
            animateScroll(viewScrolledAmount, minScrollRange);
        } else {
            animateScroll(viewScrolledAmount, maxScrollRange);
        }
    }

    private void animateScroll(int fromValue, int toValue) {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
        mValueAnimator = ValueAnimator.ofInt(fromValue, toValue);
        mValueAnimator.setDuration(100);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                viewScrolledAmount = value;
                scrollPosition(value);
            }
        });
        mValueAnimator.start();
    }

    private int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    public void setOnViewShowHideListener(OnViewShowHideListener onViewShowHideListener) {
        this.onViewShowHideListener = onViewShowHideListener;
    }

    public interface OnViewShowHideListener {
        void onViewHid();
        void onViewShowed();
    }
}
