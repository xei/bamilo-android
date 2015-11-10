package com.mobile.utils.ui;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.view.R;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.2
 * @date 2015/06/15
 */
public class ErrorLayoutFactory {

    public static final int NO_NETWORK_LAYOUT = 1;

    public static final int UNEXPECTED_ERROR_LAYOUT = 2;

    public static final int CART_EMPTY_LAYOUT = 3;

    public static final int NO_FAVOURITES_LAYOUT = 4;

    public static final int NO_RECENT_SEARCHES_LAYOUT = 5;

    public static final int NO_RECENTLY_VIEWED_LAYOUT = 6;

    public static final int CONTINUE_SHOPPING_LAYOUT = 7;

    public static final int CATALOG_NO_RESULTS = 8;

    public static final int CATALOG_UNEXPECTED_ERROR = 9;

    public static final int NO_ORDERS_LAYOUT = 10;

    private View mErrorLayout;

    private int actualError;

    /**
     * Create a new instance of ErrorLayoutFactory.
     *
     * @throws java.lang.IllegalStateException If layout is null.
     */
    public ErrorLayoutFactory(ViewGroup errorLayout){
        if(errorLayout  == null){
            throw new IllegalStateException("Error Layout not initialized");
        }
        actualError = -1;
        mErrorLayout = errorLayout;
    }

    public void showErrorLayout(int error) {
        if(actualError != error) {
            //build
            switch (error) {
                case NO_NETWORK_LAYOUT:
                    buildNoNetworkLayout();
                    break;
                case UNEXPECTED_ERROR_LAYOUT:
                    buildUnexpectedErrorLayout();
                    break;
                case CART_EMPTY_LAYOUT:
                    buildCartEmptyLayout();
                    break;
                case CONTINUE_SHOPPING_LAYOUT:
                    buildContinueShoppingLayout();
                    break;
                case CATALOG_NO_RESULTS:
                    buildCatalogNoResultsLayout();
                    break;
                case CATALOG_UNEXPECTED_ERROR:
                    buildCatalogUnexpectedErrorLayout();
                    break;
                case NO_FAVOURITES_LAYOUT:
                    buildNoFavouritesLayout(error);
                    break;
                case NO_RECENT_SEARCHES_LAYOUT:
                    buildNoRecentSearchesLayout(error);
                    break;
                case NO_RECENTLY_VIEWED_LAYOUT:
                    buildNoRecentlyViewedLayout(error);
                    break;
                case NO_ORDERS_LAYOUT:
                    buildNoOrdersLayout(error);
                    break;
            }
        }
        //show
        show();
    }

    private void buildNoFavouritesLayout(int error){
        showGenericError(error, R.drawable.ic_saved_empty, R.string.no_saved_items, R.string.no_saved_items_subtitle);
    }

    private void buildNoRecentSearchesLayout(int error){
        showGenericError(error, R.drawable.img_norecentsearch, R.string.recentsearch_no_searches, R.string.recent_searches_empty);
    }

    private void buildNoRecentlyViewedLayout(int error){
        showGenericError(error, R.drawable.ic_recentlyviewed_empty, R.string.no_recently_viewed_items, R.string.no_recently_viewed_items_subtitle);
    }

    private void buildNoOrdersLayout(int error){
        showGenericError(error, R.drawable.ic_orders_empty, R.string.no_orders_message, R.string.no_orders);
    }

    /**
     * show dynamic error message
     */
    private void showGenericError(int error, int image, int principalMessage, int detailMessage) {
        new Builder()
                .setImage(image)
                .setPrincipalMessage(principalMessage)
                .setDetailMessage(detailMessage)
                .setButtonVisible(false)
                .setRotationVisible(false);
        actualError = error;
    }

    private void buildNoNetworkLayout() {
        new Builder()
                .setImage(R.drawable.img_connect)
                .setPrincipalMessage(R.string.error_no_connection)
                .setDetailMessage(R.string.internet_no_connection_details_label)
                .setRotationVisible(true)
                .setButtonMessage(R.string.try_again_retry)
                .setButtonBackground(R.color.black_700);
        actualError = NO_NETWORK_LAYOUT;
    }

    private void buildUnexpectedErrorLayout(){
        new Builder()
                .setImage(R.drawable.img_warning)
                .setPrincipalMessageVisible(false)
                .setDetailMessage(R.string.server_error)
                .setRotationVisible(true)
                .setButtonMessage(R.string.try_again_retry)
                .setButtonBackground(R.color.black_700);
        actualError = UNEXPECTED_ERROR_LAYOUT;
    }

    private void buildCartEmptyLayout(){
        new Builder()
                .setImage(R.drawable.img_emptycart)
                .setPrincipalMessage(R.string.order_no_items)
                .setDetailMessageVisible(false)
                .setButtonMessage(R.string.continue_shopping)
                .setRotationVisible(false)
                .setButtonBackground(R.color.color_accent);
        actualError = CART_EMPTY_LAYOUT;
    }

    private void buildContinueShoppingLayout() {
        new Builder()
                .setImage(R.drawable.img_warning)
                .setPrincipalMessageVisible(false)
                .setDetailMessage(R.string.server_error)
                .setButtonMessage(R.string.continue_shopping)
                .setRotationVisible(false)
                .setButtonBackground(R.color.color_accent);
        actualError = CONTINUE_SHOPPING_LAYOUT;
    }

    private void buildCatalogNoResultsLayout(){
        new Builder()
                .setImage(R.drawable.img_filternoresults)
                .setPrincipalMessage(R.string.catalog_no_results)
                .setDetailMessageVisible(false)
                .setButtonMessage(R.string.catalog_edit_filters)
                .setRotationVisible(false)
                .setButtonBackground(R.color.color_accent);
        actualError = CATALOG_NO_RESULTS;
    }

    private void buildCatalogUnexpectedErrorLayout(){
        new Builder()
                .setImage(R.drawable.img_filternoresults)
                .setPrincipalMessage(R.string.server_error)
                .setDetailMessageVisible(false)
                .setButtonMessage(R.string.catalog_edit_filters)
                .setRotationVisible(false)
                .setButtonBackground(R.color.color_accent);
        actualError = CATALOG_UNEXPECTED_ERROR;
    }

    private void show() {
        mErrorLayout.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mErrorLayout.setVisibility(View.GONE);
    }

    /**
     * Class used for building the layout.
     */
    @SuppressWarnings("unused")
    private class Builder{

        Builder() {
            // ...
        }

        Builder setRotationVisible(boolean isToShow){
            View retrySpinning = mErrorLayout.findViewById(R.id.fragment_root_error_spinning);
            if(isToShow){
                retrySpinning.setVisibility(View.VISIBLE);
            } else {
                retrySpinning.clearAnimation();
                retrySpinning.setVisibility(View.GONE);
            }
            return this;
        }

        Builder setButtonMessage(@StringRes int message){
            ((TextView)mErrorLayout.findViewById(R.id.fragment_root_error_button_message)).setText(message);
            return this;
        }

        Builder setButtonBackground(@DrawableRes int background){
            mErrorLayout.findViewById(R.id.fragment_root_error_button).setBackgroundResource(background);
            return this;
        }

        Builder setButtonVisible(boolean isToShow){
            mErrorLayout.findViewById(R.id.fragment_root_error_button).setVisibility(isToShow ? View.VISIBLE : View.GONE);
            return this;
        }


        Builder setImage(@DrawableRes int image){
            View imageView = mErrorLayout.findViewById(R.id.fragment_root_error_image);
            imageView.setVisibility(View.VISIBLE);
            ((ImageView)imageView).setImageResource(image);
            return this;
        }

        Builder setImageVisibility(boolean isToShow){
            mErrorLayout.findViewById(R.id.fragment_root_error_image).setVisibility(isToShow ? View.VISIBLE : View.GONE);
            return this;
        }

        Builder setPrincipalMessage(@StringRes int message){
            View messageView = mErrorLayout.findViewById(R.id.fragment_root_error_label);
            messageView.setVisibility(View.VISIBLE);
            ((TextView)messageView).setText(message);
            return this;
        }

        Builder setPrincipalMessageVisible(boolean isToShow){
            mErrorLayout.findViewById(R.id.fragment_root_error_label).setVisibility(isToShow ? View.VISIBLE : View.GONE);
            return this;
        }

        Builder setDetailMessage(@StringRes int message){
            View messageView = mErrorLayout.findViewById(R.id.fragment_root_error_details_label);
            messageView.setVisibility(View.VISIBLE);
            ((TextView)messageView).setText(message);
            return this;
        }

        Builder setDetailMessageVisible(boolean isToShow){
            mErrorLayout.findViewById(R.id.fragment_root_error_details_label).setVisibility(isToShow ? View.VISIBLE : View.GONE);
            return this;
        }
    }

}
