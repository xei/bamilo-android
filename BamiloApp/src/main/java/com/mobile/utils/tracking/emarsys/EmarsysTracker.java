package com.mobile.utils.tracking.emarsys;

import android.content.Context;

import com.mobile.classes.models.BaseEventModel;
import com.mobile.classes.models.LoginEventModel;
import com.mobile.constants.tracking.EmarsysEventConstants;
import com.mobile.extlibraries.emarsys.EmarsysMobileEngage;
import com.mobile.extlibraries.emarsys.EmarsysMobileEngageResponse;
import com.mobile.factories.EmarsysEventFactory;
import com.mobile.utils.tracking.BaseEventTracker;

import java.util.HashMap;

public class EmarsysTracker extends BaseEventTracker {

    private static EmarsysTracker instance = null;

    protected EmarsysTracker() {}

    public static EmarsysTracker getInstance() {
        if(instance == null) {
            instance = new EmarsysTracker();
        }
        return instance;
    }

    @Override
    public String getTrackerName() {
        return "EmarsysTracker";
    }

    @Override
    public void trackEventAddToCart(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventAddToWishList(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventAppOpened(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventCatalogSortChanged(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventCatalogViewChanged(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventCheckoutFinished(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventCheckoutStart(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventLogin(Context context, BaseEventModel eventModel) {
        LoginEventModel loginEventModel = (LoginEventModel)eventModel;
        sendEventToEmarsys(context, EmarsysEventConstants.Login,
                EmarsysEventFactory.login(
                        loginEventModel.method,
                        loginEventModel.emailDomain,
                        loginEventModel.isSuccess));
    }

    @Override
    public void trackEventLogout(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventPurchased(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventRecommendationTapped(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventRemoveFromWishList(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSearch(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSearchBarSearched(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSearchFiltered(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSignup(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventTeaserPurchased(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventTeaserTapped(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventViewProduct(Context context, BaseEventModel eventModel) {

    }

    private void sendEventToEmarsys(Context context, String event, HashMap<String, Object> attributes) {
        EmarsysMobileEngageResponse emarsysMobileEngageResponse = new EmarsysMobileEngageResponse() {
            @Override
            public void EmarsysMobileEngageResponse(boolean success) {}
        };
        EmarsysMobileEngage.getInstance(context).sendCustomEvent(event, attributes, emarsysMobileEngageResponse);
    }
}
