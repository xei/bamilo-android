package com.mobile.utils.tracking;

import com.mobile.classes.models.BaseEventModel;
import com.mobile.interfaces.tracking.IEventTracker;
import com.mobile.view.BaseActivity;

import java.util.HashMap;

/**
 * Created by Narbeh M. on 4/29/17.
 */

public abstract class BaseEventTracker implements IEventTracker {
    @Override
    public String getTrackerName() {
        return null;
    }

    @Override
    public void trackEventAddToCart(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventAddToWishList(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventAppOpened(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventCatalogSortChanged(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventCatalogViewChanged(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventCheckoutFinished(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventCheckoutStart(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventLogin(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventLogout(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventPurchased(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventRecommendationTapped(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventRemoveFromWishList(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventSearch(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventSearchBarSearched(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventSearchFiltered(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventSignup(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventTeaserPurchased(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventTeaserTapped(BaseActivity activity, BaseEventModel eventModel) { return; }

    @Override
    public void trackEventViewProduct(BaseActivity activity, BaseEventModel eventModel) { return; }
}
