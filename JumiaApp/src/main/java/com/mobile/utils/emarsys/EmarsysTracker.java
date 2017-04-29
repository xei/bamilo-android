package com.mobile.utils.emarsys;

import com.mobile.constants.EventConstants;
import com.mobile.factories.EventFactory;
import com.mobile.libraries.emarsys.EmarsysMobileEngage;
import com.mobile.libraries.emarsys.EmarsysMobileEngageResponse;
import com.mobile.utils.EventTracker;
import com.mobile.view.BaseActivity;

import java.util.HashMap;

/**
 * Created by shahrooz on 4/12/17.
 */

public class EmarsysTracker extends EventTracker {
    private static EmarsysTracker instance = null;
    protected EmarsysTracker() {}

    public static EmarsysTracker getInstance() {
        if(instance == null) {
            instance = new EmarsysTracker();
        }
        return instance;
    }

    @Override
    public void postEvent(BaseActivity activity, String event, HashMap<String, Object> attributes) {
        EmarsysMobileEngageResponse emarsysMobileEngageResponse = new EmarsysMobileEngageResponse() {
            @Override
            public void EmarsysMobileEngageResponse(boolean success) {}
        };
        EmarsysMobileEngage.getInstance(activity.getApplicationContext()).sendCustomEvent(event, attributes,emarsysMobileEngageResponse);
    }

    public void login(BaseActivity activity, String method, String emailDomain, boolean success) {
        postEvent(activity, EventConstants.Login, EventFactory.login(method, (emailDomain != null ? emailDomain : EventConstants.UNKNOWN_EVENT_VALUE), success));
    }

    public void signUp(BaseActivity activity, String method, String emailDomain, boolean success) {
        postEvent(activity, EventConstants.SignUp, EventFactory.signup(method, emailDomain, success));
    }

    public void logOut(BaseActivity activity, boolean success) {
        postEvent(activity, EventConstants.Logout, EventFactory.logout(success));
    }

    public void openApp(BaseActivity activity, EventFactory.OpenAppEventSourceType source) {
        postEvent(activity, EventConstants.OpenApp, EventFactory.openApp(source));
    }

    public void addToCart(BaseActivity activity, String sku , Long basketValue, boolean success) {
        postEvent(activity, EventConstants.AddToCart, EventFactory.addToCart(sku, basketValue, success));
    }

    public void addToFavorites(BaseActivity activity, String categoryUrlKey, boolean success) {
        postEvent(activity, EventConstants.AddToFavorites, EventFactory.addToFavorites(categoryUrlKey, success));
    }

    public void purchase(BaseActivity activity, String categories , long basketValue, boolean success) {
        postEvent(activity, EventConstants.Purchase, EventFactory.purchase(categories, basketValue, success));
    }

    public void search(BaseActivity activity, String categoryUrlKey, String keywords) {
        postEvent(activity, EventConstants.Search, EventFactory.search(categoryUrlKey, keywords));
    }

    public void viewProduct(BaseActivity activity, String categoryUrlKey, long price) {
        postEvent(activity, EventConstants.ViewProduct, EventFactory.viewProduct(categoryUrlKey, price));
    }
}
