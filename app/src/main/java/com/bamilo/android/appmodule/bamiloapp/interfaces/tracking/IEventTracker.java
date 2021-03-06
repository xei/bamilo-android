package com.bamilo.android.appmodule.bamiloapp.interfaces.tracking;

import android.content.Context;

import com.bamilo.android.appmodule.bamiloapp.models.BaseEventModel;

/**
 * Created by narbeh on 12/3/17.
 */

public interface IEventTracker extends IBaseTracker {
    void trackEventSearchFiltered(Context context, BaseEventModel eventModel);
    void trackEventRecommendationTapped(Context context, BaseEventModel eventModel);
    void trackEventSearchBarSearched(Context context, BaseEventModel eventModel);
    void trackEventViewProduct(Context context, BaseEventModel eventModel);
    void trackEventSearch(Context context, BaseEventModel eventModel);
    void trackEventSearchSuggestions(Context context, BaseEventModel eventModel);
    void trackEventPurchased(Context context, BaseEventModel eventModel);
    void trackEventPurchase(Context context, BaseEventModel eventModel);
    void trackEventTeaserPurchased(Context context, BaseEventModel eventModel);
    void trackEventTeaserTapped(Context context, BaseEventModel eventModel);
    void trackEventAddToCart(Context context, BaseEventModel eventModel);
    void trackEventBuyNow(Context context, BaseEventModel eventModel);
    void trackEventRemoveFromCart(Context context, BaseEventModel eventModel);
    void trackEventAddToWishList(Context context, BaseEventModel eventModel);
    void trackEventRemoveFromWishList(Context context, BaseEventModel eventModel);
    void trackEventAppOpened(Context context, BaseEventModel eventModel);
    void trackEventLogout(Context context, BaseEventModel eventModel);
    void trackEventLogin(Context context, BaseEventModel eventModel);
    void trackEventSignup(Context context, BaseEventModel eventModel);
    void trackEventCatalogViewChanged(Context context, BaseEventModel eventModel);
    void trackEventCatalogSortChanged(Context context, BaseEventModel eventModel);
    void trackEventCheckoutStart(Context context, BaseEventModel eventModel);
    void trackEventCheckoutFinished(Context context, BaseEventModel eventModel);
    void trackEventSpecificationTapped(Context context, BaseEventModel eventModel);
    void trackEventDescriptionTapped(Context context, BaseEventModel eventModel);
    void trackEventAddReviewTapped(Context context, BaseEventModel eventModel);
    void trackEventRateTapped(Context context, BaseEventModel eventModel);
    void trackEventOtherSellersTapped(Context context, BaseEventModel eventModel);
}
