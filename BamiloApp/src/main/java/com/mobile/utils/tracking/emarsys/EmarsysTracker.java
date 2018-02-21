package com.mobile.utils.tracking.emarsys;

import android.content.Context;

import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.BaseEventModel;
import com.mobile.classes.models.EmarsysEventModel;
import com.mobile.constants.tracking.EmarsysEventConstants;
import com.mobile.extlibraries.emarsys.EmarsysMobileEngage;
import com.mobile.extlibraries.emarsys.EmarsysMobileEngageResponse;
import com.mobile.managers.AppManager;
import com.mobile.utils.DateUtils;
import com.mobile.utils.tracking.BaseEventTracker;
import com.mobile.utils.ui.UIUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public void setCampaignUrl(String campaignUrl) {

    }

    @Override
    public void trackEventAddToCart(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof EmarsysEventModel) {
            EmarsysEventModel aem = (EmarsysEventModel) eventModel;
            sendEventToEmarsys(context, EmarsysEventConstants.AddToCart, aem.emarsysAttributes);
        }
    }

    @Override
    public void trackEventRemoveFromCart(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof EmarsysEventModel) {
            EmarsysEventModel aem = (EmarsysEventModel) eventModel;
            sendEventToEmarsys(context, EmarsysEventConstants.RemoveFromCart, aem.emarsysAttributes);
        }
    }

    @Override
    public void trackEventAddToWishList(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof EmarsysEventModel) {
            EmarsysEventModel aem = (EmarsysEventModel) eventModel;
            sendEventToEmarsys(context, EmarsysEventConstants.AddToFavorites, aem.emarsysAttributes);
        }
    }

    @Override
    public void trackEventAppOpened(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof EmarsysEventModel) {
            EmarsysEventModel aem = (EmarsysEventModel) eventModel;
            sendEventToEmarsys(context, EmarsysEventConstants.OpenApp, aem.emarsysAttributes);
        }
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
        if (eventModel instanceof EmarsysEventModel) {
            EmarsysEventModel aem = (EmarsysEventModel) eventModel;
            sendEventToEmarsys(context, EmarsysEventConstants.Login, aem.emarsysAttributes);
        }
    }

    @Override
    public void trackEventLogout(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof EmarsysEventModel) {
            EmarsysEventModel aem = (EmarsysEventModel) eventModel;
            sendEventToEmarsys(context, EmarsysEventConstants.Logout, aem.emarsysAttributes);
        }
    }

    @Override
    public void trackEventPurchased(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventPurchase(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof EmarsysEventModel) {
            EmarsysEventModel aem = (EmarsysEventModel) eventModel;
            sendEventToEmarsys(context, EmarsysEventConstants.Purchase, aem.emarsysAttributes);
        }
    }

    @Override
    public void trackEventRecommendationTapped(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventRemoveFromWishList(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSearch(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof EmarsysEventModel) {
            EmarsysEventModel aem = (EmarsysEventModel) eventModel;
            sendEventToEmarsys(context, EmarsysEventConstants.Search, aem.emarsysAttributes);
        }
    }

    @Override
    public void trackEventSearchSuggestions(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSearchBarSearched(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSearchFiltered(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventSignup(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof EmarsysEventModel) {
            EmarsysEventModel aem = (EmarsysEventModel) eventModel;
            sendEventToEmarsys(context, EmarsysEventConstants.SignUp, aem.emarsysAttributes);
        }
    }

    @Override
    public void trackEventTeaserPurchased(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventTeaserTapped(Context context, BaseEventModel eventModel) {

    }

    @Override
    public void trackEventViewProduct(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof EmarsysEventModel) {
            EmarsysEventModel aem = (EmarsysEventModel) eventModel;
            sendEventToEmarsys(context, EmarsysEventConstants.ViewProduct, aem.emarsysAttributes);
        }
    }

    protected void sendEventToEmarsys(Context context, String event, Map<String, Object> attributes) {
        EmarsysMobileEngageResponse emarsysMobileEngageResponse = new EmarsysMobileEngageResponse() {
            @Override
            public void EmarsysMobileEngageResponse(boolean success) {}
        };
        Map<String, Object> emarsysAttrs = getBasicAttributes();
        emarsysAttrs.putAll(attributes);
        EmarsysMobileEngage.getInstance(context).sendCustomEvent(event, emarsysAttrs, emarsysMobileEngageResponse);
    }

    protected HashMap<String, Object> getBasicAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put(EmarsysEventConstants.AppVersion, AppManager.getAppFullFormattedVersion());
        attributes.put(EmarsysEventConstants.Platform, "android");
        attributes.put(EmarsysEventConstants.Connection, UIUtils.networkType(BamiloApplication.INSTANCE.getApplicationContext()));
        attributes.put(EmarsysEventConstants.Date, DateUtils.getWebNormalizedDateTimeString(new Date()));
        if(BamiloApplication.isCustomerLoggedIn()) {
            String userGender = BamiloApplication.CUSTOMER.getGender();
            if(userGender != null) {
                attributes.put(EmarsysEventConstants.Gender, userGender);
            }
        }

        return attributes;
    }
}
