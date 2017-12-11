package com.mobile.utils.tracking.emarsys;

import com.mobile.classes.models.BaseEventModel;
import com.mobile.classes.models.LoginEventModel;
import com.mobile.constants.tracking.EmarsysEventConstants;
import com.mobile.constants.tracking.EventConstants;
import com.mobile.extlibraries.emarsys.EmarsysMobileEngage;
import com.mobile.extlibraries.emarsys.EmarsysMobileEngageResponse;
import com.mobile.factories.EmarsysEventFactory;
import com.mobile.utils.tracking.BaseEventTracker;
import com.mobile.view.BaseActivity;

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
    public void trackEventAddToCart(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventAddToWishList(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventAppOpened(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventCatalogSortChanged(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventCatalogViewChanged(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventCheckoutFinished(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventCheckoutStart(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventLogin(BaseActivity activity, BaseEventModel eventModel) {
        LoginEventModel loginEventModel = (LoginEventModel)eventModel;
        sendEventToEmarsys(activity, EmarsysEventConstants.Login,
                EmarsysEventFactory.login(
                        loginEventModel.method,
                        loginEventModel.emailDomain,
                        loginEventModel.isSuccess));
    }

    @Override
    public void trackEventLogout(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventPurchased(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventRecommendationTapped(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventRemoveFromWishList(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSearch(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSearchBarSearched(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSearchFiltered(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSignup(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventTeaserPurchased(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventTeaserTapped(BaseActivity activity, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventViewProduct(BaseActivity activity, BaseEventModel eventModel) {

    }

    private void sendEventToEmarsys(BaseActivity activity, String event, HashMap<String, Object> attributes) {
        EmarsysMobileEngageResponse emarsysMobileEngageResponse = new EmarsysMobileEngageResponse() {
            @Override
            public void EmarsysMobileEngageResponse(boolean success) {}
        };
        EmarsysMobileEngage.getInstance(activity.getApplicationContext()).sendCustomEvent(event, attributes, emarsysMobileEngageResponse);
    }
}
