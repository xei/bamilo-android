package com.mobile.utils.ui;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.view.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
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
    public static final int SSL_ERROR_LAYOUT = 11;
    public static final int UNKNOWN_CHECKOUT_STEP_ERROR_LAYOUT = 12;
    public static final int CAMPAIGN_UNAVAILABLE_LAYOUT = 13;

    @IntDef({
            NO_NETWORK_LAYOUT,
            UNEXPECTED_ERROR_LAYOUT,
            CART_EMPTY_LAYOUT,
            NO_FAVOURITES_LAYOUT,
            NO_RECENT_SEARCHES_LAYOUT,
            NO_RECENTLY_VIEWED_LAYOUT,
            CONTINUE_SHOPPING_LAYOUT,
            CATALOG_NO_RESULTS,
            CATALOG_UNEXPECTED_ERROR,
            NO_ORDERS_LAYOUT,
            SSL_ERROR_LAYOUT,
            UNKNOWN_CHECKOUT_STEP_ERROR_LAYOUT,
            CAMPAIGN_UNAVAILABLE_LAYOUT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface LayoutErrorType {
    }

    private final View mErrorLayout;

    private int actualError;

    /**
     * Create a new instance of ErrorLayoutFactory.
     *
     * @throws java.lang.IllegalStateException If layout is null.
     */
    public ErrorLayoutFactory(ViewGroup errorLayout) {
        if (errorLayout == null) {
            throw new IllegalStateException("Error Layout not initialized");
        }
        actualError = -1;
        mErrorLayout = errorLayout;
    }

    /**
     * Build the error layout.
     *
     * @param error - The layout error type
     */
    public void showErrorLayout(@LayoutErrorType int error) {
        if (actualError != error) {
            // Save error
            actualError = error;
            // Build layout
            switch (error) {
                case CAMPAIGN_UNAVAILABLE_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.ic_campaigns2)
                            .setPrincipalMessage(R.string.campaign_unavailable_title)
                            .setDetailMessage(R.string.campaign_unavailable_description)
                            .setButtonMessage(R.string.continue_shopping)
                            .setButtonTextColor(R.color.white)
                            .setButtonBackground(R.color.color_accent)
                            .setRotationVisible(false);
                    break;
                case UNKNOWN_CHECKOUT_STEP_ERROR_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.ic_warning)
                            .setPrincipalMessage(R.string.an_error_occurred)
                            .setDetailMessage(R.string.customer_service_info)
                            .setButtonVisible(false)
                            .showContactInfo();
                    break;
                case SSL_ERROR_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.ic_warning)
                            .setPrincipalMessage(R.string.an_error_occurred)
                            .setDetailMessage(R.string.customer_service_info)
                            .setButtonVisible(false)
                            .showContactInfo();
                    break;
                case NO_NETWORK_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.img_connect)
                            .setPrincipalMessage(R.string.error_no_connection)
                            .setDetailMessage(R.string.internet_no_connection_details_label)
                            .setButtonMessage(R.string.try_again_retry)
                            .setButtonBackground(R.color.black_700)
                            .setRotationVisible(true);
                    break;
                case UNEXPECTED_ERROR_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.ic_warning)
                            .setPrincipalMessage(R.string.error_problem_fetching_data)
                            .setDetailMessage(R.string.server_error)
                            .setButtonMessage(R.string.continue_shopping)
                            .setButtonTextColor(R.color.white)
                            .setButtonBackground(R.color.color_accent)
                            .setRotationVisible(false);
                    break;
                case CART_EMPTY_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.ico_empty_cart)
                            .setPrincipalMessage(R.string.order_no_items)
                            .setButtonMessage(R.string.continue_shopping)
                            .setButtonTextColor(R.color.white)
                            .setButtonBackground(R.color.color_accent)
                            .setRotationVisible(false);
                    break;
                case CONTINUE_SHOPPING_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.ic_warning)
                            .setPrincipalMessage(R.string.error_problem_fetching_data)
                            .setDetailMessage(R.string.server_error)
                            .setButtonMessage(R.string.continue_shopping)
                            .setButtonTextColor(R.color.white)
                            .setButtonBackground(R.color.color_accent)
                            .setRotationVisible(false);
                    break;
                case CATALOG_NO_RESULTS:
                    new Builder()
                            .setImage(R.drawable.ic_filter_empty)
                            .setPrincipalMessage(R.string.catalog_no_results)
                            .setDetailMessage(R.string.catalog_no_results_details)
                            .setButtonTextColor(R.color.white)
                            .setButtonMessage(R.string.catalog_edit_filters)
                            .setButtonBackground(R.color.color_accent)
                            .setRotationVisible(false);
                    break;
                case CATALOG_UNEXPECTED_ERROR:
                    new Builder()
                            .setImage(R.drawable.ic_filter_empty)
                            .setPrincipalMessage(R.string.server_error)
                            .setButtonMessage(R.string.catalog_edit_filters)
                            .setButtonBackground(R.color.color_accent)
                            .setRotationVisible(false);
                    break;
                case NO_FAVOURITES_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.ic_saved_empty)
                            .setPrincipalMessage(R.string.no_saved_items)
                            .setDetailMessage(R.string.no_saved_items_subtitle)
                            .setButtonVisible(false)
                            .setRotationVisible(false);
                    break;
                case NO_RECENT_SEARCHES_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.img_norecentsearch)
                            .setPrincipalMessage(R.string.recentsearch_no_searches)
                            .setDetailMessage(R.string.recent_searches_empty)
                            .setButtonVisible(false)
                            .setRotationVisible(false);
                    break;
                case NO_RECENTLY_VIEWED_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.ic_recentlyviewed_empty)
                            .setPrincipalMessage(R.string.no_recently_viewed_items)
                            .setDetailMessage(R.string.no_recently_viewed_items_subtitle)
                            .setButtonVisible(false)
                            .setRotationVisible(false);
                    break;
                case NO_ORDERS_LAYOUT:
                    new Builder()
                            .setImage(R.drawable.ic_orders_empty)
                            .setPrincipalMessage(R.string.no_orders)
                            .setDetailMessage(R.string.no_orders_message)
                            .setButtonVisible(false)
                            .setRotationVisible(false);
                    break;
            }
        }
        //show
        show();
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
    private class Builder {

        Builder() {
            // ...
        }

        Builder setRotationVisible(boolean isToShow) {
            View retrySpinning = mErrorLayout.findViewById(R.id.fragment_root_error_spinning);
            if (isToShow) {
                retrySpinning.setVisibility(View.VISIBLE);
            } else {
                retrySpinning.clearAnimation();
                retrySpinning.setVisibility(View.GONE);
            }
            return this;
        }

        Builder showContactInfo() {
            // Get contact info
            String phone = CountryPersistentConfigs.getCountryPhoneNumber(mErrorLayout.getContext());
            String email = CountryPersistentConfigs.getCountryEmail(mErrorLayout.getContext());
            // Set contact info
            mErrorLayout.findViewById(R.id.contacts_info).setVisibility(View.VISIBLE);
            ((TextView) mErrorLayout.findViewById(R.id.phone_text)).setText(phone);
            ((TextView) mErrorLayout.findViewById(R.id.email_text)).setText(email);
            return this;
        }

        Builder setButtonMessage(@StringRes int message) {
            ((TextView) mErrorLayout.findViewById(R.id.fragment_root_error_button_message)).setText(message);
            return this;
        }

        Builder setButtonBackground(@DrawableRes int background) {
            mErrorLayout.findViewById(R.id.fragment_root_error_button).setBackgroundResource(background);
            return this;
        }

        Builder setButtonVisible(boolean isToShow) {
            mErrorLayout.findViewById(R.id.fragment_root_error_button).setVisibility(isToShow ? View.VISIBLE : View.GONE);
            return this;
        }

        Builder setButtonTextColor(@ColorRes int color) {
            ((TextView) mErrorLayout.findViewById(R.id.fragment_root_error_button_message))
                    .setTextColor(ContextCompat.getColor(mErrorLayout.getContext(), color));
            return this;
        }

        Builder setImage(@DrawableRes int image) {
            View imageView = mErrorLayout.findViewById(R.id.fragment_root_error_image);
            imageView.setVisibility(View.VISIBLE);
            ((ImageView) imageView).setImageResource(image);
            return this;
        }

        Builder setImageVisibility(boolean isToShow) {
            mErrorLayout.findViewById(R.id.fragment_root_error_image).setVisibility(isToShow ? View.VISIBLE : View.GONE);
            return this;
        }

        Builder setPrincipalMessage(@StringRes int message) {
            View messageView = mErrorLayout.findViewById(R.id.fragment_root_error_label);
            messageView.setVisibility(View.VISIBLE);
            ((TextView) messageView).setText(message);
            return this;
        }

        Builder setPrincipalMessageVisible(boolean isToShow) {
            mErrorLayout.findViewById(R.id.fragment_root_error_label).setVisibility(isToShow ? View.VISIBLE : View.GONE);
            return this;
        }

        Builder setDetailMessage(@StringRes int message) {
            View messageView = mErrorLayout.findViewById(R.id.fragment_root_error_details_label);
            messageView.setVisibility(View.VISIBLE);
            ((TextView) messageView).setText(message);
            return this;
        }
    }

}
