package com.mobile.utils.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.view.R;

/**
 * Created by rsoares on 6/15/15.
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

    private View mErrorLayout;

    private int actualError;

    /**
     * Create a new instance of ErrorLayoutFactory.
     *
     * @param errorLayout
     * @throws java.lang.IllegalStateException If layout is null.
     */
    public ErrorLayoutFactory(ViewGroup errorLayout){

        if(errorLayout  == null){
            throw new IllegalStateException("Error Layout not initialized");
        }

        actualError = -1;

        this.mErrorLayout = errorLayout;
//        errorLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideWarning();
//            }
//        });
    }

    public void showErrorLayout(int error) {
        switch (error) {
            case NO_NETWORK_LAYOUT:
                showNoNetworkLayout();
                break;
            case UNEXPECTED_ERROR_LAYOUT:
                showUnexpectedErrorLayout();
                break;
            case CART_EMPTY_LAYOUT:
                showCartEmptyLayout();
                break;
            case NO_FAVOURITES_LAYOUT:
                showNoFavouritesLayout();
                break;
            case NO_RECENT_SEARCHES_LAYOUT:
                showNoRecentSearchesLayout();
                break;
            case NO_RECENTLY_VIEWED_LAYOUT:
                showNoRecentlyViewedLayout();
                break;
            case CONTINUE_SHOPPING_LAYOUT:
                showContinueShoppingLayout();
                break;
            case CATALOG_NO_RESULTS:
                showCatalogNoResultsLayout();
                break;
            case CATALOG_UNEXPECTED_ERROR:
                showCatalogUnexpectedErrorLayout();
                break;
        }
    }

    private void showNoNetworkLayout() {
        if(actualError != NO_NETWORK_LAYOUT){
            new Builder()
                    .setImage(R.drawable.img_connect)
                    .setPrincipalMessage(R.string.error_no_connection)
                    .setDetailMessage(R.string.internet_no_connection_details_label)
                    .setRotationVisible(true)
                    .setButtonMessage(R.string.try_again_retry)
                    .setButtonBackground(R.drawable.btn_grey);
            actualError = NO_NETWORK_LAYOUT;
        } else {
//            new Builder().startAnimation();
        }
    }

    private void showUnexpectedErrorLayout(){
        if(actualError != UNEXPECTED_ERROR_LAYOUT){
            new Builder()
                    .setImage(R.drawable.img_warning)
                    .setPrincipalMessageVisible(false)
                    .setDetailMessage(R.string.server_error)
                    .setRotationVisible(true)
                    .setButtonMessage(R.string.try_again_retry)
                    .setButtonBackground(R.drawable.btn_grey);
            actualError = UNEXPECTED_ERROR_LAYOUT;
        } else {
//            new Builder().startAnimation();
        }
    }

    private void showCartEmptyLayout(){
        if(actualError != CART_EMPTY_LAYOUT){
            new Builder()
                    .setImage(R.drawable.img_emptycart)
                    .setPrincipalMessage(R.string.order_no_items)
//                    .setDetailMessage(R.string.server_error)
                    .setDetailMessageVisible(false)
                    .setButtonMessage(R.string.continue_shopping)
                    .setRotationVisible(false)
                    .setButtonBackground(R.drawable.btn_orange);
            actualError = CART_EMPTY_LAYOUT;
        }
    }

    private void showNoFavouritesLayout(){
        if(actualError != NO_FAVOURITES_LAYOUT){
            new Builder()
                    .setImage(R.drawable.img_nofavourites)
                    .setPrincipalMessage(R.string.favourite_no_favourites)
//                    .setDetailMessage(R.string.server_error)
                    .setDetailMessageVisible(false)
                    .setButtonMessage(R.string.continue_shopping)
                    .setRotationVisible(false)
                    .setButtonBackground(R.drawable.btn_orange);
            actualError = NO_FAVOURITES_LAYOUT;
        }
    }

    private void showNoRecentSearchesLayout(){
        if(actualError != NO_RECENT_SEARCHES_LAYOUT){
            new Builder()
                    .setImage(R.drawable.img_norecentsearch)
                    .setPrincipalMessage(R.string.recentsearch_no_searches)
//                    .setDetailMessage(R.string.server_error)
                    .setDetailMessageVisible(false)
                    .setButtonMessage(R.string.continue_shopping)
                    .setRotationVisible(false)
                    .setButtonBackground(R.drawable.btn_orange);
            actualError = NO_RECENT_SEARCHES_LAYOUT;
        }
    }

    private void showNoRecentlyViewedLayout(){
        if(actualError != NO_RECENTLY_VIEWED_LAYOUT){
            new Builder()
                    .setImage(R.drawable.img_norecentview)
                    .setPrincipalMessage(R.string.recentlyview_no_searches)
//                    .setDetailMessage(R.string.server_error)
                    .setDetailMessageVisible(false)
                    .setButtonMessage(R.string.continue_shopping)
                    .setRotationVisible(false)
                    .setButtonBackground(R.drawable.btn_orange);
            actualError = NO_RECENTLY_VIEWED_LAYOUT;
        }
    }

    private void showContinueShoppingLayout() {
        if(actualError != CONTINUE_SHOPPING_LAYOUT){
            new Builder()
                    .setImage(R.drawable.img_warning)
//                    .setPrincipalMessage(R.string.recentlyview_no_searches)
                    .setPrincipalMessageVisible(false)
                    .setDetailMessage(R.string.server_error)
                    .setButtonMessage(R.string.continue_shopping)
                    .setRotationVisible(false)
                    .setButtonBackground(R.drawable.btn_orange);
            actualError = CONTINUE_SHOPPING_LAYOUT;
        }
    }

    private void showCatalogNoResultsLayout(){
        if(actualError != CATALOG_NO_RESULTS){
            new Builder()
                    .setImage(R.drawable.img_filternoresults)
                    .setPrincipalMessage(R.string.catalog_no_results)
//                    .setDetailMessage(R.string.server_error)
                    .setDetailMessageVisible(false)
                    .setButtonMessage(R.string.catalog_edit_filters)
                    .setRotationVisible(false)
                    .setButtonBackground(R.drawable.btn_orange);
            actualError = CATALOG_NO_RESULTS;
        }
    }

    private void showCatalogUnexpectedErrorLayout(){
        if(actualError != CATALOG_UNEXPECTED_ERROR){
            new Builder()
                    .setImage(R.drawable.img_filternoresults)
                    .setPrincipalMessage(R.string.server_error)
//                    .setDetailMessage(R.string.server_error)
                    .setDetailMessageVisible(false)
                    .setButtonMessage(R.string.catalog_edit_filters)
                    .setRotationVisible(false)
                    .setButtonBackground(R.drawable.btn_orange);
            actualError = CATALOG_UNEXPECTED_ERROR;
        }
    }

    private class Builder{

        Builder(){
//            setImage(0);

        }

        Builder setRotationVisible(boolean isToShow){
            View retrySpinning = mErrorLayout.findViewById(R.id.fragment_root_error_spinning);
            if(retrySpinning != null) {
                if(isToShow){
                    retrySpinning.setVisibility(View.VISIBLE);
                } else {
                    retrySpinning.clearAnimation();
                    retrySpinning.setVisibility(View.GONE);
                }
            }
            return this;
        }

        Builder setButtonMessage(int message){
            View buttonMessage = mErrorLayout.findViewById(R.id.fragment_root_error_button_message);
            if(buttonMessage instanceof TextView){
                ((TextView)buttonMessage).setText(message);
            }
            return this;
        }

        Builder setButtonBackground(int background){
            View button = mErrorLayout.findViewById(R.id.fragment_root_error_button);
            if(button != null){
                button.setBackgroundResource(background);
            }
            return this;
        }

        Builder setImage(int image){
            View imageView = mErrorLayout.findViewById(R.id.fragment_root_error_image);
            if(imageView instanceof ImageView){
                imageView.setVisibility(View.VISIBLE);
                ((ImageView)imageView).setImageResource(image);
            }
            return this;
        }

        Builder setImageVisibible(boolean isToShow){
            View imageView = mErrorLayout.findViewById(R.id.fragment_root_error_image);
            if(imageView != null){
                imageView.setVisibility(isToShow ? View.VISIBLE : View.GONE);
            }
            return this;
        }

        Builder setPrincipalMessage(int message){
            View messageView = mErrorLayout.findViewById(R.id.fragment_root_error_label);
            if(messageView instanceof TextView){
                messageView.setVisibility(View.VISIBLE);
                ((TextView)messageView).setText(message);
            }
            return this;
        }

        Builder setPrincipalMessageVisible(boolean isToShow){
            View messageView = mErrorLayout.findViewById(R.id.fragment_root_error_label);
            if(messageView != null){
                messageView.setVisibility(isToShow ? View.VISIBLE : View.GONE);
            }
            return this;
        }

        Builder setDetailMessage(int message){
            View messageView = mErrorLayout.findViewById(R.id.fragment_root_error_details_label);
            if(messageView instanceof TextView){
                messageView.setVisibility(View.VISIBLE);
                ((TextView)messageView).setText(message);
            }
            return this;
        }

        Builder setDetailMessageVisible(boolean isToShow){
            View messageView = mErrorLayout.findViewById(R.id.fragment_root_error_details_label);
            if(messageView != null){
                messageView.setVisibility(isToShow ? View.VISIBLE : View.GONE);
            }
            return this;
        }

    }

}
