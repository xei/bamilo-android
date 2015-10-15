package com.mobile.utils.ui;

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

    /**
     * ################# ANIMATION DURATION #################
     */

//    public static final int _4_SECONDS = 4000;
    public static final int _5_SECONDS = 5000;

    /**
     * ################# WARNING BAR #################
     */

    public static final int CHOOSE_ONE_SIZE = 1;
    public static final int ADDED_ITEM_TO_CART = 2;
    public static final int ADDED_ITEMS_TO_CART = 3;
    public static final int NO_INTERNET = 4;
    public static final int ERROR_ADD_TO_CART = 5;
    public static final int PROBLEM_FETCHING_DATA = 6;
    public static final int PROBLEM_FETCHING_DATA_ANIMATION = 7;
    public static final int ERROR_ADD_PRODUCTS_TO_CART = 8;
    public static final int ADDED_TO_SAVED = 9;
    public static final int REMOVE_FROM_SAVED = 10;

    /**
     * The last warning that was built and might be re-used.
     */
    protected int actualWarning;

    protected View mWarningBar;

    /**
     * Create a new instance of WarningFactory.
     *
     * @param warningBar
     * @throws java.lang.IllegalStateException In case of warningBar is null.
     */
    public WarningFactory(View warningBar){

        if(warningBar == null){
            throw new IllegalStateException("Warning bar not initialized");
        }

        actualWarning = -1;

        this.mWarningBar = warningBar;
        mWarningBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideWarning();
            }
        });
    }

    /**
     * Constructs warning bar.
     *
     * @param warning The warning desired.
     */
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
            case ERROR_ADD_TO_CART:
                showErrorAddToCart();
                break;
            case ERROR_ADD_PRODUCTS_TO_CART:
                showErrorAddProductsToCart();
                break;
            case PROBLEM_FETCHING_DATA:
                showWarningProblemFetchingData(false);
                break;
            case PROBLEM_FETCHING_DATA_ANIMATION:
                showWarningProblemFetchingData(true);
                break;
            case ADDED_TO_SAVED:
                showWarningAddedItemToSaved();
                break;
            case REMOVE_FROM_SAVED:
                showWarningRemovedItemFromSaved();
                break;
        }
    }

    private void showErrorAddProductsToCart() {
        if(actualWarning != ERROR_ADD_PRODUCTS_TO_CART){
            new Builder().setText(R.string.some_products_not_added)
                    .setBackground(R.color.red_warning)
                    .setImageVisibility(true)
                    .setAnimationDuration(_5_SECONDS)
                    .startAnimation();
            actualWarning = ERROR_ADD_PRODUCTS_TO_CART;
        } else {
            new Builder().startAnimation();
        }
    }

    private void showErrorAddToCart() {
        if(actualWarning != ERROR_ADD_TO_CART){
            new Builder().setText(R.string.error_add_to_shopping_cart)
                    .setBackground(R.color.red_warning)
                    .setImageVisibility(true)
                    .setAnimationDuration(_5_SECONDS)
                    .startAnimation();
            actualWarning = ERROR_ADD_TO_CART;
        } else {
            new Builder().startAnimation();
        }
    }

    private void showWarningChooseOneSize(){
        if(actualWarning != CHOOSE_ONE_SIZE) {
            new Builder().setText(R.string.product_variance_choose_error)
                    .setBackground(R.color.red_warning)
                    .setImageVisibility(false)
                    .show();

            actualWarning = CHOOSE_ONE_SIZE;
        } else {
            new Builder().show();
        }
    }

    private void showWarningAddedItemToCart(){
        if(actualWarning != ADDED_ITEM_TO_CART) {
        new Builder().setText(R.string.added_to_shop_cart_dialog_text)
                .setBackground(R.color.green_warning)
                .setImageVisibility(false)
                .setAnimationDuration(_5_SECONDS)
                .startAnimation();
            actualWarning = ADDED_ITEM_TO_CART;
        } else {
            new Builder().startAnimation();
        }
    }

    private void showWarningAddedItemsToCart(){
        if(actualWarning != ADDED_ITEMS_TO_CART) {

        new Builder().setText(R.string.added_bundle_to_shop_cart_dialog_text)
                .setBackground(R.color.green_warning)
                .setImageVisibility(false)
                .setAnimationDuration(_5_SECONDS)
                .startAnimation();
            actualWarning = ADDED_ITEMS_TO_CART;
        } else {
            new Builder().startAnimation();
        }
    }

    private void showWarningNoInternet(){
        if(actualWarning != NO_INTERNET) {
            new Builder().setText(R.string.no_internet_access_warning_title)
                    .setBackground(R.color.red_warning)
                    .setImageVisibility(true)
                    .setAnimationDuration(_5_SECONDS)
                    .startAnimation();
            actualWarning = NO_INTERNET;
        } else {
            new Builder().startAnimation();
        }
    }

    private void showWarningProblemFetchingData(boolean withAnimation){
        if(!withAnimation){
            if(actualWarning != PROBLEM_FETCHING_DATA) {
                new Builder().setText(R.string.server_error)
                        .setBackground(R.color.red_warning)
                        .setImageVisibility(true)
                        .show();
                actualWarning = PROBLEM_FETCHING_DATA;
            } else {
                new Builder().show();
            }

        } else {

            if(actualWarning != PROBLEM_FETCHING_DATA_ANIMATION) {
                new Builder().setText(R.string.server_error)
                        .setBackground(R.color.red_warning)
                        .setImageVisibility(true)
                        .setAnimationDuration(_5_SECONDS)
                        .startAnimation();
                actualWarning = PROBLEM_FETCHING_DATA_ANIMATION;
            } else {
                new Builder().startAnimation();
            }

        }
    }

    private void showWarningAddedItemToSaved(){
        if(actualWarning != ADDED_TO_SAVED) {
            new Builder().setText(R.string.products_added_saved)
                    .setBackground(R.color.green_warning)
                    .setImageVisibility(false)
                    .setAnimationDuration(_5_SECONDS)
                    .startAnimation();
            actualWarning = ADDED_TO_SAVED;
        } else {
            new Builder().startAnimation();
        }
    }

    private void showWarningRemovedItemFromSaved(){
        if(actualWarning != REMOVE_FROM_SAVED) {
            new Builder().setText(R.string.products_removed_saved)
                    .setBackground(R.color.green_warning)
                    .setImageVisibility(false)
                    .setAnimationDuration(_5_SECONDS)
                    .startAnimation();
            actualWarning = REMOVE_FROM_SAVED;
        } else {
            new Builder().startAnimation();
        }
    }

    public void hideWarning(){
        new Builder().hide();
    }

    /**
     * Class used for building the warning.
     */
    private class Builder {

        Builder(){
            mWarningBar.clearAnimation();
        }

        private int animationLength = _5_SECONDS;

        Builder setText(int message){
            ((TextView)mWarningBar.findViewById(R.id.warning_text)).setText(message);
            return this;
        }

        Builder setBackground(int drawable){
            mWarningBar.setBackgroundResource(drawable);
            mWarningBar.setAlpha(0.95f);
            return this;
        }

        Builder setImageVisibility(boolean isToShowImage){
            mWarningBar.findViewById(R.id.warning_image).setVisibility(isToShowImage ? View.VISIBLE : View.GONE);
            return this;
        }

        Builder setImage(int image){
            View imageView = mWarningBar.findViewById(R.id.warning_image);
            imageView.setVisibility(View.VISIBLE);
            ((ImageView)imageView).setImageResource(image);
            return this;
        }

        Builder setAnimationDuration(int animationLength){
            this.animationLength = animationLength;
            return this;
        }

        Builder startAnimation(){
            mWarningBar.setVisibility(View.VISIBLE);
            UIUtils.animateFadeInAndOut(mWarningBar.getContext(), mWarningBar, animationLength);
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
