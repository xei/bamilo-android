package com.bamilo.android.appmodule.bamiloapp.utils.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.emarsys.predict.RecommendedItem;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.RecommendListCompletionHandler;
import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.RecommendManager;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.RecommendationsCartHolder;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential.
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
    public static final int NETWORK_ERROR_LAYOUT = 14;
    public static final int MAINTENANCE_LAYOUT = 15;
    private static final int RES_NO_CONTENT = -1;

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
            CAMPAIGN_UNAVAILABLE_LAYOUT,
            NETWORK_ERROR_LAYOUT,
            MAINTENANCE_LAYOUT,
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
     * @throws IllegalStateException If layout is null.
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
        resetLayout();
//        if (actualError != error) {
        // Save error
        actualError = error;
        // Build layout
        switch (error) {
            case CAMPAIGN_UNAVAILABLE_LAYOUT:
                new Builder()
                        .setContent(R.drawable.ic_campaigns2, R.string.campaign_unavailable_title,
                                R.string.campaign_unavailable_description)
                        .showContinueButton();
                break;
            case SSL_ERROR_LAYOUT:
            case UNKNOWN_CHECKOUT_STEP_ERROR_LAYOUT:
                new Builder()
                        .setContent(R.drawable.ic_warning, R.string.an_error_occurred,
                                R.string.customer_service_info)
                        .showContactInfo();
                break;
            case NO_NETWORK_LAYOUT:
                new Builder()
                        .setContent(R.drawable.img_connect,
                                R.string.there_is_no_access_to_internet_label)
                        .setButton(R.string.retry_label, R.color.gray_1,
                                R.drawable.network_connection_retry_btn_bg)
                        .showRetryButton()
                        .showNetworkSettingsButtons()
                        .showButtonSpinning();
                break;
            case UNEXPECTED_ERROR_LAYOUT:
                new Builder()
                        .setContent(R.drawable.ic_warning, R.string.error_problem_fetching_data,
                                R.string.server_error)
                        .showContinueButton();
                break;
            case CART_EMPTY_LAYOUT:
                new Builder()
                        .setDetailMessage(R.drawable.ico_empty_cart_rtl, R.string.order_no_items)
                        .showRecommendation("PERSONAL");
                break;
            case CONTINUE_SHOPPING_LAYOUT:
                new Builder()
                        .setContent(R.drawable.ic_warning, R.string.error_problem_fetching_data,
                                R.string.server_error)
                        .showContinueButton();
                break;
            case CATALOG_NO_RESULTS:
                new Builder()
                        .setContent(R.drawable.ic_filter_empty, R.string.catalog_no_results,
                                R.string.catalog_no_results_details)
                        .setButton(R.string.return_label, R.color.white,
                                R.color.button_primary_color);
                break;
            case CATALOG_UNEXPECTED_ERROR:
                new Builder()
                        .setContent(R.drawable.ic_filter_empty, R.string.server_error)
                        .setButton(R.string.catalog_edit_filters, R.color.orange_lighter);
                break;
            case NO_FAVOURITES_LAYOUT:
                new Builder()
                        .setDetailMessage(R.drawable.ic_saved_empty,
                                R.string.no_saved_items_subtitle)
                        .showRecommendation("POPULAR");
                break;
            case NO_RECENT_SEARCHES_LAYOUT:
                new Builder()
                        .setContent(R.drawable.img_norecentsearch,
                                R.string.recentsearch_no_searches, R.string.recent_searches_empty);
                break;
            case NO_RECENTLY_VIEWED_LAYOUT:
                new Builder()
                        .setContent(R.drawable.ic_recentlyviewed_empty,
                                R.string.no_recently_viewed_items,
                                R.string.no_recently_viewed_items_subtitle);
                break;
            case NETWORK_ERROR_LAYOUT:
                // TODO: 12/11/2017 change error icon
                new Builder()
                        .setContent(R.drawable.img_request_failure, R.string.network_timeout_error,
                                R.string.please_wait_for_a_while)
                        .setButton(R.string.retry_label, R.color.retry_text_color,
                                R.drawable.network_connection_retry_btn_bg)
                        .showRetryButtonWithDelay(5000)
                        .showButtonSpinning();
                break;
            case MAINTENANCE_LAYOUT:
                // TODO: 12/11/2017 change error icon
                new Builder()
                        .setContent(R.drawable.img_request_failure,
                                R.string.server_in_maintenance_warning,
                                R.string.please_wait_for_a_while)
                        .setButton(R.string.retry_label, R.color.retry_text_color,
                                R.drawable.network_connection_retry_btn_bg)
                        .showRetryButtonWithDelay(5000)
                        .showButtonSpinning();
                break;
            case NO_ORDERS_LAYOUT:
                new Builder()
                        .setContent(R.drawable.ic_orders_empty, R.string.no_orders,
                                R.string.no_orders_message);
                break;
        }
//        }
        //show
        show();
    }

    private void resetLayout() {
        mErrorLayout.findViewById(R.id.fragment_root_error_spinning).clearAnimation();
        mErrorLayout.findViewById(R.id.fragment_root_error_button).clearAnimation();
        mErrorLayout.findViewById(R.id.fragment_root_error_button).setEnabled(true);
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
            mErrorLayout.findViewById(R.id.llNetworkSettings).setVisibility(View.GONE);
        }

        Builder showContactInfo() {
            // Get contact info
            String phone = CountryPersistentConfigs
                    .getCountryPhoneNumber(mErrorLayout.getContext());
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
            if (title != RES_NO_CONTENT) {
                setPrincipalMessage(title);
            } else {
                hideTitle();
            }
            setDetailMessage(message);
            hideRecommendation();
            return this;
        }

        Builder setDetailMessage(@DrawableRes int image, @StringRes int message) {
            // Hide spinning button to avoid that it appears on a new error screen after network issues.
            hideButtonSpinning();
            hideButton();
            setImage(image);
            setDetailMessageAsTitle(message);
            hideRecommendation();
            TextView messageView = mErrorLayout.findViewById(R.id.fragment_root_error_label);
            messageView.setVisibility(View.GONE);
            // messageView = (TextView) mErrorLayout.findViewById(R.id.fragment_root_error_details_label);
            // messageView.setVisibility(View.VISIBLE);
            return this;
        }


        Builder showContinueButton() {
            setButtonMessage(R.string.continue_shopping);
            setButtonTextColor(R.color.white);
            setButtonBackground(R.color.button_primary_color);
            showButton();
            return this;
        }


        private Builder showButton() {
            mErrorLayout.findViewById(R.id.fragment_root_error_button).setVisibility(View.VISIBLE);
            return this;
        }

        private Builder hideButton() {
            mErrorLayout.findViewById(R.id.fragment_root_error_button).setVisibility(View.GONE);
            return this;
        }

        Builder showRetryButton() {
            return showButton();
        }

        Builder showRetryButtonWithDelay(int visibilityDelayMillis) {
            mErrorLayout.findViewById(R.id.fragment_root_error_button).setVisibility(View.VISIBLE);
            AlphaAnimation animation = new AlphaAnimation(0, 1);
            animation.setStartOffset(visibilityDelayMillis);
            animation.setDuration(350); // default amount of animation duration
            animation.setFillBefore(true);
            mErrorLayout.findViewById(R.id.fragment_root_error_button).startAnimation(animation);
            return this;
        }

        Builder showNetworkSettingsButtons() {
            mErrorLayout.findViewById(R.id.llNetworkSettings).setVisibility(View.VISIBLE);
            mErrorLayout.findViewById(R.id.btnInternetSettings)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mErrorLayout.getContext()
                                    .startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    });
            return this;
        }

        Builder setButton(@StringRes int message, @DrawableRes int background) {
            setButtonMessage(message);
            setButtonBackground(background);
            showButton();
            return this;
        }

        Builder setButton(@StringRes int message, @ColorRes int color,
                @DrawableRes int background) {
            setButtonMessage(message);
            setButtonTextColor(color);
            setButtonTextSize(14);
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
            ((TextView) mErrorLayout.findViewById(R.id.fragment_root_error_button_message))
                    .setText(message);
            return this;
        }

        private Builder setButtonBackground(@DrawableRes int background) {
            mErrorLayout.findViewById(R.id.fragment_root_error_button)
                    .setBackgroundResource(background);
            return this;
        }

        private Builder setButtonTextColor(@ColorRes int color) {
            ((TextView) mErrorLayout.findViewById(R.id.fragment_root_error_button_message))
                    .setTextColor(ContextCompat.getColor(mErrorLayout.getContext(), color));
            return this;
        }

        private Builder setButtonTextSize(int textSizeSP) {
            ((TextView) mErrorLayout.findViewById(R.id.fragment_root_error_button_message))
                    .setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSP);
            return this;
        }

        private Builder setImage(@DrawableRes int image) {
            ImageView imageView = mErrorLayout.findViewById(R.id.fragment_root_error_image);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(image);
            return this;
        }

        private Builder setPrincipalMessage(@StringRes int message) {
            TextView messageView = mErrorLayout.findViewById(R.id.fragment_root_error_label);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(message);
            return this;
        }

        private Builder setDetailMessage(@StringRes int message) {
            TextView messageView = mErrorLayout
                    .findViewById(R.id.fragment_root_error_details_label);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(message);
            return this;
        }

        private Builder setDetailMessageAsTitle(@StringRes int message) {
            TextView messageView = mErrorLayout
                    .findViewById(R.id.fragment_root_error_details_label);
            messageView.setVisibility(View.VISIBLE);
            messageView.setTextColor(mErrorLayout.getResources().getColor(R.color.black_900));
            messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mErrorLayout.getResources().getDimension(R.dimen.dimen_16dp));
            messageView.setText(message);
            return this;
        }

        private Builder hideTitle() {
            TextView messageView = mErrorLayout.findViewById(R.id.fragment_root_error_label);
            messageView.setVisibility(View.GONE);
            return this;
        }

        private Builder hideDetailMessage() {
            TextView messageView = mErrorLayout
                    .findViewById(R.id.fragment_root_error_details_label);
            messageView.setVisibility(View.GONE);
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
                    recommendationsTeaserHolder = new RecommendationsCartHolder(context,
                            mErrorLayout
                                    .findViewById(R.id.recommendation_view), null);

                    recommendationsTeaserHolder.onBind(data);

                    ((ViewGroup) mErrorLayout).addView(recommendationsTeaserHolder.itemView,
                            ((ViewGroup) mErrorLayout).getChildCount() - 1);

                } catch (Exception ignored) {

                }

            }
        };

        RecommendManager recommendManager = new RecommendManager();
        switch (type) {
            case "POPULAR":
                recommendManager.sendPopularRecommend(15, recommendListCompletionHandler);
                break;

            case "PERSONAL":
            default:
                recommendManager.sendPersonalRecommend(15, recommendListCompletionHandler);
                break;
        }
    }
}
