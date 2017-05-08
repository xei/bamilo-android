package com.mobile.utils.ui;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emarsys.predict.RecommendedItem;
import com.mobile.libraries.emarsys.predict.recommended.RecommendListCompletionHandler;
import com.mobile.libraries.emarsys.predict.recommended.RecommendManager;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.home.holder.RecommendationsCartHolder;
import com.mobile.utils.home.holder.RecommendationsHolder;
import com.mobile.view.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

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
    RecommendationsCartHolder recommendationsTeaserHolder;
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
                    .setContent(R.drawable.ic_campaigns2, R.string.campaign_unavailable_title, R.string.campaign_unavailable_description)
                    .showContinueButton();
                    break;
                case SSL_ERROR_LAYOUT:
                case UNKNOWN_CHECKOUT_STEP_ERROR_LAYOUT:
                    new Builder()
                    .setContent(R.drawable.ic_warning, R.string.an_error_occurred, R.string.customer_service_info)
                    .showContactInfo();
                    break;
                case NO_NETWORK_LAYOUT:
                    new Builder()
                    .setContent(R.drawable.img_connect, R.string.error_no_connection, R.string.internet_no_connection_details_label)
                    .setButton(R.string.try_again_retry, R.color.black,R.drawable._gen_selector_button_grey)
                    .showRetryButton()
                    .showButtonSpinning();
                    break;
                case UNEXPECTED_ERROR_LAYOUT:
                    new Builder()
                    .setContent(R.drawable.ic_warning, R.string.error_problem_fetching_data, R.string.server_error)
                    .showContinueButton();
                    break;
                case CART_EMPTY_LAYOUT:
                    new Builder()
                    .setContent(R.drawable.ico_empty_cart, R.string.order_no_items)
                    .showRecommendation("PERSONAL");
                    break;
                case CONTINUE_SHOPPING_LAYOUT:
                    new Builder()
                    .setContent(R.drawable.ic_warning, R.string.error_problem_fetching_data, R.string.server_error)
                    .showContinueButton();
                    break;
                case CATALOG_NO_RESULTS:
                    new Builder()
                    .setContent(R.drawable.ic_filter_empty, R.string.catalog_no_results, R.string.catalog_no_results_details)
                    .setButton(R.string.catalog_edit_filters, R.color.white, R.color.color_accent);
                    break;
                case CATALOG_UNEXPECTED_ERROR:
                    new Builder()
                    .setContent(R.drawable.ic_filter_empty, R.string.server_error)
                    .setButton(R.string.catalog_edit_filters, R.color.color_accent);
                    break;
                case NO_FAVOURITES_LAYOUT:
                    new Builder()
                    .setContent(R.drawable.ic_saved_empty, R.string.no_saved_items, R.string.no_saved_items_subtitle)
                    .showRecommendation("POPULAR");
                    break;
                case NO_RECENT_SEARCHES_LAYOUT:
                    new Builder()
                    .setContent(R.drawable.img_norecentsearch, R.string.recentsearch_no_searches, R.string.recent_searches_empty);
                    break;
                case NO_RECENTLY_VIEWED_LAYOUT:
                    new Builder()
                    .setContent(R.drawable.ic_recentlyviewed_empty, R.string.no_recently_viewed_items, R.string.no_recently_viewed_items_subtitle);
                    break;
                case NO_ORDERS_LAYOUT:
                    new Builder()
                    .setContent(R.drawable.ic_orders_empty, R.string.no_orders, R.string.no_orders_message);
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

        Builder setContent(@DrawableRes int image, @StringRes int title) {
            // Hide spinning button to avoid that it appears on a new error screen after network issues.
            hideButtonSpinning();
            hideButton();
            setImage(image);
            setPrincipalMessage(title);
            hideDetailMessage();
            hideRecommendation();
            return this;
        }

        Builder setContent(@DrawableRes int image, @StringRes int title, @StringRes int message) {
            // Hide spinning button to avoid that it appears on a new error screen after network issues.
            hideButtonSpinning();
            hideButton();
            setImage(image);
            setPrincipalMessage(title);
            setDetailMessage(message);
            hideRecommendation();
            return this;
        }

        Builder showContinueButton() {
            setButtonMessage(R.string.continue_shopping);
            setButtonTextColor(R.color.white);
            setButtonBackground(R.color.color_accent);
            showButton();
            return this;
        }


        private Builder showButton(){
            mErrorLayout.findViewById(R.id.fragment_root_error_button).setVisibility(View.VISIBLE);
            return this;
        }

        private Builder hideButton(){
            mErrorLayout.findViewById(R.id.fragment_root_error_button).setVisibility(View.GONE);
            return this;
        }

        Builder showRetryButton(){
            return showButton();
        }

        Builder setButton(@StringRes int message, @DrawableRes int background) {
            setButtonMessage(message);
            setButtonBackground(background);
            showButton();
            return this;
        }

        Builder setButton(@StringRes int message, @ColorRes int color, @DrawableRes int background) {
            setButtonMessage(message);
            setButtonTextColor(color);
            setButtonBackground(background);
            showButton();
            return this;
        }

        Builder showButtonSpinning() {
            View retrySpinning = mErrorLayout.findViewById(R.id.fragment_root_error_spinning);
            retrySpinning.clearAnimation();
            retrySpinning.setVisibility(View.VISIBLE);
            return this;
        }

        private Builder hideButtonSpinning() {
            View retrySpinning = mErrorLayout.findViewById(R.id.fragment_root_error_spinning);
            retrySpinning.clearAnimation();
            retrySpinning.setVisibility(View.GONE);
            return this;
        }

        private Builder hideRecommendation() {
            View recommendations = mErrorLayout.findViewById(R.id.recommendation_view);
            recommendations.setVisibility(View.GONE);
            return this;
        }

        private Builder showRecommendation(String type) {
            View recommendations = mErrorLayout.findViewById(R.id.recommendation_view);
            recommendations.setVisibility(View.VISIBLE);
            sendRecommend(mErrorLayout.getContext(), type);
            return this;
        }

        private Builder setButtonMessage(@StringRes int message) {
            ((TextView) mErrorLayout.findViewById(R.id.fragment_root_error_button_message)).setText(message);
            return this;
        }

        private Builder setButtonBackground(@DrawableRes int background) {
            mErrorLayout.findViewById(R.id.fragment_root_error_button).setBackgroundResource(background);
            return this;
        }

        private Builder setButtonTextColor(@ColorRes int color) {
            ((TextView) mErrorLayout.findViewById(R.id.fragment_root_error_button_message))
            .setTextColor(ContextCompat.getColor(mErrorLayout.getContext(), color));
            return this;
        }

        private Builder setImage(@DrawableRes int image) {
            ImageView imageView = (ImageView) mErrorLayout.findViewById(R.id.fragment_root_error_image);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(image);
            return this;
        }

        private Builder setPrincipalMessage(@StringRes int message) {
            TextView messageView = (TextView) mErrorLayout.findViewById(R.id.fragment_root_error_label);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(message);
            return this;
        }

        private Builder setDetailMessage(@StringRes int message) {
            TextView messageView = (TextView) mErrorLayout.findViewById(R.id.fragment_root_error_details_label);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(message);
            return this;
        }

        private Builder hideDetailMessage() {
            TextView messageView = (TextView) mErrorLayout.findViewById(R.id.fragment_root_error_details_label);
            messageView.setVisibility(View.INVISIBLE);
            return this;
        }
    }

    private void sendRecommend(final Context context, String type) {

        RecommendListCompletionHandler recommendListCompletionHandler = new RecommendListCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(String category, List<RecommendedItem> data) {
                if (data == null || data.size() == 0) {
                    View recommendations = mErrorLayout.findViewById(R.id.recommendation_view);
                    recommendations.setVisibility(View.GONE);
                    return;
                }
                try {
                    if (recommendationsTeaserHolder != null) {
                        ((ViewGroup) mErrorLayout).removeView(recommendationsTeaserHolder.itemView);
                    }
                    recommendationsTeaserHolder = new RecommendationsCartHolder(context, ((ViewGroup) mErrorLayout).findViewById(R.id.recommendation_view), null);

                    recommendationsTeaserHolder.onBind(data);

                    ((ViewGroup) mErrorLayout).addView(recommendationsTeaserHolder.itemView, ((ViewGroup) mErrorLayout).getChildCount() - 1);

                } catch (Exception ex) {

                }

            }
        };

        RecommendManager recommendManager = new RecommendManager();
        switch (type){
            case "POPULAR":
                recommendManager.sendPopularRecommend(recommendListCompletionHandler);
                break;

            case "PERSONAL":
            default:
                recommendManager.sendPersonalRecommend(recommendListCompletionHandler);
                break;
        }
    }
}
