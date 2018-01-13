package com.mobile.interfaces.tracking;

import android.content.Context;

import com.mobile.classes.models.BaseEventModel;

/**
 * Created by narbeh on 12/3/17.
 */

public interface IEventTracker extends IBaseTracker {
    void trackEventSearchFiltered(Context context, BaseEventModel eventModel);
    void trackEventRecommendationTapped(Context context, BaseEventModel eventModel);
    void trackEventSearchBarSearched(Context context, BaseEventModel eventModel);
    void trackEventViewProduct(Context context, BaseEventModel eventModel);
    void trackEventSearch(Context context, BaseEventModel eventModel);
    void trackEventPurchased(Context context, BaseEventModel eventModel);
    void trackEventTeaserPurchased(Context context, BaseEventModel eventModel);
    void trackEventTeaserTapped(Context context, BaseEventModel eventModel);
    void trackEventAddToCart(Context context, BaseEventModel eventModel);
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
}
