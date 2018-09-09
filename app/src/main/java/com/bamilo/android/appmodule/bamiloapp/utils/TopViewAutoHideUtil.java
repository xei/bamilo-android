package com.bamilo.android.appmodule.bamiloapp.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class TopViewAutoHideUtil {
    private int minScrollRange, maxScrollRange;
    private int viewScrolledAmount;
    private View view;
    private ValueAnimator mValueAnimator;
    private OnViewShowHideListener onViewShowHideListener;
    private boolean searchBarHidden = true;
    private int totalScrollAmount;


    public TopViewAutoHideUtil(int minScrollRange, int maxScrollRange, View view) {
        this.minScrollRange = minScrollRange;
        this.maxScrollRange = maxScrollRange;
        this.view = view;
    }

    public void onScroll(int dy) {
        totalScrollAmount += dy;
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
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
        if (onViewShowHideListener != null) {
            if (viewScrolledAmount == minScrollRange) {
                searchBarHidden = true;
                onViewShowHideListener.onViewHid();
            } else if (viewScrolledAmount == maxScrollRange) {
                searchBarHidden = false;
                onViewShowHideListener.onViewShowed();
            }
        }
    }

    public void onStopScroll(boolean scrollingToTop) {
        if (totalScrollAmount < minScrollRange && viewScrolledAmount < minScrollRange / 2) {
            animateScroll(viewScrolledAmount, minScrollRange);
        } else {
            animateScroll(viewScrolledAmount, maxScrollRange);
        }
    }

    public void showSearchBar() {
        animateScroll(viewScrolledAmount, maxScrollRange);
    }

    public void syncState(int scrolledAmount) {
        if (Math.abs(scrolledAmount) < Math.abs(minScrollRange)) {
            showSearchBar();
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

    public int getMinScrollRange() {
        return minScrollRange;
    }

    public int getMaxScrollRange() {
        return maxScrollRange;
    }

    public boolean isSearchBarHidden() {
        return searchBarHidden;
    }

    public interface OnViewShowHideListener {
        void onViewHid();

        void onViewShowed();
    }
}
