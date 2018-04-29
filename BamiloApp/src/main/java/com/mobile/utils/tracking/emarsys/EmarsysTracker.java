package com.mobile.utils.tracking.emarsys;

import android.content.Context;

import com.emarsys.mobileengage.MobileEngage;
import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.BaseEventModel;
import com.mobile.classes.models.MainEventModel;
import com.mobile.constants.tracking.EmarsysEventConstants;
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
        if (eventModel instanceof MainEventModel) {
            MainEventModel aem = (MainEventModel) eventModel;
            sendEventToEmarsys(EmarsysEventConstants.AddToCart, aem.customAttributes);
        }
    }

    @Override
    public void trackEventRemoveFromCart(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel aem = (MainEventModel) eventModel;
            sendEventToEmarsys(EmarsysEventConstants.RemoveFromCart, aem.customAttributes);
        }
    }

    @Override
    public void trackEventAddToWishList(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel aem = (MainEventModel) eventModel;
            sendEventToEmarsys(EmarsysEventConstants.AddToFavorites, aem.customAttributes);
        }
    }

    @Override
    public void trackEventAppOpened(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel aem = (MainEventModel) eventModel;
            sendEventToEmarsys(EmarsysEventConstants.AppOpened, aem.customAttributes);
        }
    }

    @Override
    public void trackEventLogin(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel aem = (MainEventModel) eventModel;
            sendEventToEmarsys(EmarsysEventConstants.Login, aem.customAttributes);
        }
    }

    @Override
    public void trackEventLogout(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel aem = (MainEventModel) eventModel;
            sendEventToEmarsys(EmarsysEventConstants.Logout, aem.customAttributes);
        }
    }

    @Override
    public void trackEventPurchase(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel aem = (MainEventModel) eventModel;
            sendEventToEmarsys(EmarsysEventConstants.Purchase, aem.customAttributes);
        }
    }

    @Override
    public void trackEventSearch(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel aem = (MainEventModel) eventModel;
            sendEventToEmarsys(EmarsysEventConstants.Search, aem.customAttributes);
        }
    }

    @Override
    public void trackEventSignup(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel aem = (MainEventModel) eventModel;
            sendEventToEmarsys(EmarsysEventConstants.SignUp, aem.customAttributes);
        }
    }

    @Override
    public void trackEventViewProduct(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel aem = (MainEventModel) eventModel;
            sendEventToEmarsys(EmarsysEventConstants.ViewProduct, aem.customAttributes);
        }
    }

    public void trackEventAppLogin(int contactFieldID, String contactFieldValue) {
        if(contactFieldID > 0 && contactFieldValue != null) {
            MobileEngage.appLogin(contactFieldID, contactFieldValue);
        } else {
            MobileEngage.appLogin();
        }
    }

    public void trackEventAppLogout() {
        MobileEngage.appLogout();
    }

    protected void sendEventToEmarsys(String event, Map<String, String> attributes) {
        Map<String, String> emarsysAttrs = getBasicAttributes();
        emarsysAttrs.putAll(attributes);
        MobileEngage.trackCustomEvent(event, attributes);
    }

    protected HashMap<String, String> getBasicAttributes() {
        HashMap<String, String> attributes = new HashMap<>();

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
