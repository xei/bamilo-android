package com.mobile.interfaces.tracking;

import com.mobile.classes.models.BaseEventModel;
import com.mobile.view.BaseActivity;

import java.util.HashMap;

/**
 * Created by narbeh on 12/3/17.
 */

public interface IEventTracker extends IBaseTracker {
    void trackEventSearchFiltered(BaseActivity activity, BaseEventModel eventModel);
    void trackEventRecommendationTapped(BaseActivity activity, BaseEventModel eventModel);
    void trackEventSearchBarSearched(BaseActivity activity, BaseEventModel eventModel);
    void trackEventViewProduct(BaseActivity activity, BaseEventModel eventModel);
    void trackEventSearch(BaseActivity activity, BaseEventModel eventModel);
    void trackEventPurchased(BaseActivity activity, BaseEventModel eventModel);
    void trackEventTeaserPurchased(BaseActivity activity, BaseEventModel eventModel);
    void trackEventTeaserTapped(BaseActivity activity, BaseEventModel eventModel);
    void trackEventAddToCart(BaseActivity activity, BaseEventModel eventModel);
    void trackEventAddToWishList(BaseActivity activity, BaseEventModel eventModel);
    void trackEventRemoveFromWishList(BaseActivity activity, BaseEventModel eventModel);
    void trackEventAppOpened(BaseActivity activity, BaseEventModel eventModel);
    void trackEventLogout(BaseActivity activity, BaseEventModel eventModel);
    void trackEventLogin(BaseActivity activity, BaseEventModel eventModel);
    void trackEventSignup(BaseActivity activity, BaseEventModel eventModel);
    void trackEventCatalogViewChanged(BaseActivity activity, BaseEventModel eventModel);
    void trackEventCatalogSortChanged(BaseActivity activity, BaseEventModel eventModel);
    void trackEventCheckoutStart(BaseActivity activity, BaseEventModel eventModel);
    void trackEventCheckoutFinished(BaseActivity activity, BaseEventModel eventModel);
}
