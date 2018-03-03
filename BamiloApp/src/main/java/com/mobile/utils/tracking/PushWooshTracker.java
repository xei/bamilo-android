package com.mobile.utils.tracking;

import android.app.Activity;
import android.content.Context;

import com.mobile.classes.models.BaseEventModel;
import com.mobile.utils.pushwoosh.PushWooshCounter;
import com.mobile.utils.tracking.emarsys.EmarsysTracker;
import com.pushwoosh.PushManager;
import com.pushwoosh.inapp.InAppFacade;

import java.util.HashMap;
import java.util.Map;

public final class PushWooshTracker extends EmarsysTracker {

    private static PushWooshTracker instance = null;

    private Activity activity;

    protected PushWooshTracker() {
    }

    @Override
    public void trackEventAppOpened(Context context, BaseEventModel eventModel) {
        super.trackEventAppOpened(context, eventModel);
        PushWooshCounter.increaseAppOpenCount();
        HashMap<String, Object> openCount = new HashMap<>();
        openCount.put("AppOpenCount", PushWooshCounter.getAppOpenCount());
        PushManager.sendTags(context, openCount, null);
    }

    @Override
    public void trackEventPurchase(Context context, BaseEventModel eventModel) {
        super.trackEventPurchase(context, eventModel);
        PushWooshCounter.increasePurchseCount();
        HashMap<String, Object> purchaseCount = new HashMap<>();
        purchaseCount.put("PurchaseCount", PushWooshCounter.getPurchaseCount());
        PushManager.sendTags(context, purchaseCount, null);
    }

    public static EmarsysTracker getInstance(Activity activity) {
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
    protected void sendEventToEmarsys(Context context, String event, Map<String, Object> attributes) {
        Map<String, Object> pushWooshAttrs = getBasicAttributes();
        pushWooshAttrs.putAll(attributes);
        InAppFacade.postEvent(activity, event, pushWooshAttrs);
    }

    public static void destroyTracker() {
        if (instance != null) {
            instance.activity = null;
            instance = null;
        }
    }
}
