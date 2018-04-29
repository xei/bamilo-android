package com.mobile.utils.tracking;

import android.app.Activity;
import android.content.Context;

import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.BaseEventModel;
import com.mobile.classes.models.MainEventModel;
import com.mobile.constants.tracking.EmarsysEventConstants;
import com.mobile.managers.AppManager;
import com.mobile.utils.DateUtils;
import com.mobile.utils.pushwoosh.PushWooshCounter;
import com.mobile.utils.ui.UIUtils;
import com.pushwoosh.PushManager;
import com.pushwoosh.inapp.InAppFacade;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class PushWooshTracker extends BaseEventTracker {

    private static PushWooshTracker instance = null;

    private Activity activity;

    protected PushWooshTracker() {
    }

    public static PushWooshTracker getInstance(Activity activity) {
        if (instance == null) {
            instance = new PushWooshTracker();
            instance.activity = activity;
        }
        return instance;
    }

    @Override
    public String getTrackerName() {
        return "PushWooshTracker";
    }

    @Override
    public void setCampaignUrl(String campaignUrl) {}

    @Override
    public void trackEventAddToCart(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel pem = (MainEventModel) eventModel;
            sendEventToPushWoosh(EmarsysEventConstants.AddToCart, pem.customAttributes);
        }
    }

    @Override
    public void trackEventRemoveFromCart(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel pem = (MainEventModel) eventModel;
            sendEventToPushWoosh(EmarsysEventConstants.RemoveFromCart, pem.customAttributes);
        }
    }

    @Override
    public void trackEventAddToWishList(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel pem = (MainEventModel) eventModel;
            sendEventToPushWoosh(EmarsysEventConstants.AddToFavorites, pem.customAttributes);
        }
    }

    @Override
    public void trackEventAppOpened(Context context, BaseEventModel eventModel) {
        PushWooshCounter.increaseAppOpenCount();
        HashMap<String, Object> openCount = new HashMap<>();
        openCount.put("AppOpenCount", PushWooshCounter.getAppOpenCount());
        PushManager.sendTags(context, openCount, null);

        if (eventModel instanceof MainEventModel) {
            MainEventModel pem = (MainEventModel) eventModel;
            sendEventToPushWoosh(EmarsysEventConstants.AppOpened, pem.customAttributes);
        }
    }

    @Override
    public void trackEventLogin(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel pem = (MainEventModel) eventModel;
            sendEventToPushWoosh(EmarsysEventConstants.Login, pem.customAttributes);
        }
    }

    @Override
    public void trackEventLogout(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel pem = (MainEventModel) eventModel;
            sendEventToPushWoosh(EmarsysEventConstants.Logout, pem.customAttributes);
        }
    }

    @Override
    public void trackEventPurchase(Context context, BaseEventModel eventModel) {
        PushWooshCounter.increasePurchseCount();
        HashMap<String, Object> purchaseCount = new HashMap<>();
        purchaseCount.put("PurchaseCount", PushWooshCounter.getPurchaseCount());
        PushManager.sendTags(context, purchaseCount, null);

        if (eventModel instanceof MainEventModel) {
            MainEventModel pem = (MainEventModel) eventModel;
            sendEventToPushWoosh(EmarsysEventConstants.Purchase, pem.customAttributes);
        }
    }

    @Override
    public void trackEventSearch(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel mem = (MainEventModel) eventModel;
            sendEventToPushWoosh(EmarsysEventConstants.Search, mem.customAttributes);
        }
    }

    @Override
    public void trackEventSignup(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel pem = (MainEventModel) eventModel;
            sendEventToPushWoosh(EmarsysEventConstants.SignUp, pem.customAttributes);
        }
    }

    @Override
    public void trackEventViewProduct(Context context, BaseEventModel eventModel) {
        if (eventModel instanceof MainEventModel) {
            MainEventModel pem = (MainEventModel) eventModel;
            sendEventToPushWoosh(EmarsysEventConstants.ViewProduct, pem.customAttributes);
        }
    }

    protected void sendEventToPushWoosh(String event, Map<String, String> attributes) {
        HashMap<String, Object> pushWooshAttrs = getBasicAttributes();
        pushWooshAttrs.putAll(attributes);
        InAppFacade.postEvent(activity, event, pushWooshAttrs);
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

    public static void destroyTracker() {
        if (instance != null) {
            instance.activity = null;
            instance = null;
        }
    }
}
