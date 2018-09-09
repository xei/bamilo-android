package com.bamilo.android.appmodule.bamiloapp.utils.tracking;

import android.content.Context;
import com.bamilo.android.appmodule.bamiloapp.models.BaseEventModel;
import com.bamilo.android.appmodule.bamiloapp.interfaces.tracking.IEventTracker;

/**
 * Created by Narbeh M. on 4/29/17.
 */

public abstract class BaseEventTracker implements IEventTracker {
    @Override
    public String getTrackerName() {
        return null;
    }

    @Override
    public void trackEventAddToCart(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventAddToWishList(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventAppOpened(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventCatalogSortChanged(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventCatalogViewChanged(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventCheckoutFinished(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventCheckoutStart(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventLogin(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventLogout(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventPurchased(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventPurchase(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventRecommendationTapped(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventRemoveFromWishList(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventSearch(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventSearchSuggestions(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventSearchBarSearched(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventSearchFiltered(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventSignup(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventTeaserPurchased(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventTeaserTapped(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventViewProduct(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventSpecificationTapped(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventDescriptionTapped(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventAddReviewTapped(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventRateTapped(Context context, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventOtherSellersTapped(Context context, BaseEventModel eventModel) { return; }

}
