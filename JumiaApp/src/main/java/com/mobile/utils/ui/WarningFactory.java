package com.mobile.utils.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.view.R;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/04/13
 */
public class WarningFactory {

    public static final int SECONDS_4 = 4000;
    public static final int SECONDS_5 = 4000;

    public static final int CHOOSE_ONE_SIZE = 1;
    public static final int ADDED_ITEM_TO_CART = 2;
    public static final int ADDED_ITEMS_TO_CART = 3;
    public static final int NO_INTERNET = 4;
    public static final int PROBLEM_FETCHING_DATA = 6;
    public static final int PROBLEM_FETCHING_DATA_ANIMATION = 7;

    private View mWarningBar;
    private Activity mActivity;

    public WarningFactory(Activity activity, View warningBar){

        if(activity == null || warningBar == null){
            throw new IllegalStateException("Warning bar not initialized");
        }

        this.mActivity = activity;
        this.mWarningBar = warningBar;
        mWarningBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideWarning();
            }
        });
    }

    public void showWarning(int warning){
        switch (warning){
            case CHOOSE_ONE_SIZE:
                showWarningChooseOneSize();
                break;
            case ADDED_ITEM_TO_CART:
                showWarningAddedItemToCart();
                break;
            case ADDED_ITEMS_TO_CART:
                showWarningAddedItemsToCart();
                break;
            case NO_INTERNET:
                showWarningNoInternet();
                break;
            case PROBLEM_FETCHING_DATA:
                showWarningProblemFetchingData(false);
                break;
            case PROBLEM_FETCHING_DATA_ANIMATION:
                showWarningProblemFetchingData(true);
                break;
        }
    }

    /**
     * ################# WARNING BAR #################
     */

    private void showWarningChooseOneSize(){
        new Builder().setText(R.string.product_variance_choose_error)
                .setBackground(R.drawable.titlebar_noaccess)
                .setImageVisibility(false)
                .show();
    }

    private void showWarningAddedItemToCart(){
        new Builder().setText(R.string.added_to_shop_cart_dialog_text)
                .setBackground(R.color.green_warning)
                .setImageVisibility(false)
                .setAnimationLength(SECONDS_5)
                .startAnimation();
    }

    private void showWarningAddedItemsToCart(){
        new Builder().setText(R.string.added_bundle_to_shop_cart_dialog_text)
                .setBackground(R.color.green_warning)
                .setImageVisibility(false)
                .setAnimationLength(SECONDS_5)
                .startAnimation();
    }

    private void showWarningNoInternet(){
        new Builder().setText(R.string.no_internet_access_warning_title)
                .setBackground(R.drawable.titlebar_noaccess)
                .setImageVisibility(true)
                .setAnimationLength(SECONDS_4)
                .startAnimation();
    }

    private void showWarningProblemFetchingData(boolean withAnimation){
        if(!withAnimation){
            new Builder().setText(R.string.server_error)
                    .setBackground(R.drawable.titlebar_noaccess)
                    .setImageVisibility(true)
                    .show();
        } else {
            new Builder().setText(R.string.server_error)
                    .setBackground(R.drawable.titlebar_noaccess)
                    .setImageVisibility(true)
                    .setAnimationLength(SECONDS_4)
                    .startAnimation();
        }
    }

    public void hideWarning(){
        new Builder().hide();
    }

    private class Builder {
        private int animationLength = SECONDS_5;

        Builder setText(int message){
            View label = mWarningBar.findViewById(R.id.warning_text);
            if(label instanceof TextView){
                ((TextView)label).setText(message);
            }
            return this;
        }

        Builder setBackground(int drawable){
            Drawable backgroundDrawable = mActivity.getDrawable(drawable);
            if(backgroundDrawable != null){
                mWarningBar.setBackground(backgroundDrawable);
            }
            return this;
        }

        Builder setImageVisibility(boolean isToShowImage){
            View image = mWarningBar.findViewById(R.id.warning_image);
            if(image != null) {
                image.setVisibility(isToShowImage ? View.VISIBLE : View.GONE);
            }
            return this;
        }

        Builder setImage(int image){
            View imageView = mWarningBar.findViewById(R.id.warning_image);
            if(imageView instanceof ImageView){
                imageView.setVisibility(View.VISIBLE);
                ((ImageView)imageView).setImageResource(image);
            }
            return this;
        }

        Builder setAnimationLength(int animationLength){
            this.animationLength = animationLength;
            return this;
        }

        Builder startAnimation(){
            UIUtils.animateFadeInAndOut(mActivity, mWarningBar, animationLength);
            return this;
        }

        Builder show(){
            mWarningBar.setVisibility(View.VISIBLE);
            return this;
        }

        Builder hide(){
            mWarningBar.setVisibility(View.GONE);
            return this;
        }
    }
}
