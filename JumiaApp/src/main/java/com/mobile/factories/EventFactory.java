package com.mobile.factories;

import com.mobile.constants.EventConstants;
import com.mobile.utils.EventTracker;
import com.newrelic.agent.android.harvest.Event;

import java.util.HashMap;

/**
 * Created by Narbeh M. on 4/26/17.
 */

public final class EventFactory {
    public enum OpenAppEventSourceType {
        OPEN_APP_SOURCE_DIRECT("direct", 0),
        OPEN_APP_SOURCE_DEEPLINK("deeplink", 1),
        OPEN_APP_SOURCE_PUSH_NOTIFICATION("push_notification", 2);

        private String stringValue;
        private int intValue;
        OpenAppEventSourceType(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    public static HashMap<String, Object> login(String loginMethod, String emailDomain, boolean success) {
        HashMap<String, Object> attributes = EventTracker.getBasicAttributes();
        attributes.put(EventConstants.Method, loginMethod);
        attributes.put(EventConstants.EmailDomain, (emailDomain != null ? emailDomain : EventConstants.UNKNOWN_EVENT_VALUE));
        attributes.put(EventConstants.Success, success);

        return attributes;
    }

    public static HashMap<String, Object> signup(String signupMethod, String emailDomain, boolean success) {
        HashMap<String, Object> attributes = EventTracker.getBasicAttributes();
        attributes.put(EventConstants.Method, signupMethod);
        attributes.put(EventConstants.EmailDomain, emailDomain);
        attributes.put(EventConstants.Success, success);

        return attributes;
    }

    public static HashMap<String, Object> logout(boolean success) {
        HashMap<String, Object> attributes = EventTracker.getBasicAttributes();
        attributes.put(EventConstants.Success, success);

        return attributes;
    }

    public static HashMap<String, Object> openApp(OpenAppEventSourceType source) {
        HashMap<String, Object> attributes =  EventTracker.getBasicAttributes();
        attributes.put(EventConstants.Source, source.toString());

        return attributes;
    }

    public static HashMap<String, Object> addToCart(String sku, long basketValue, boolean success) {
        HashMap<String, Object> attributes = EventTracker.getBasicAttributes();
        attributes.put(EventConstants.SKU, sku);
        attributes.put(EventConstants.BasketValue, basketValue);
        attributes.put(EventConstants.Success, success);

        return attributes;
    }

    public static HashMap<String, Object> addToFavorites(String categoryUrlKey, boolean success) {
        HashMap<String, Object> attributes = EventTracker.getBasicAttributes();
        attributes.put(EventConstants.CategoryUrlKey, categoryUrlKey);
        attributes.put(EventConstants.Success, success);

        return attributes;
    }

    public static HashMap<String, Object> purchase(String categories, long basketValue, boolean success) {
        HashMap<String, Object> attributes = EventTracker.getBasicAttributes();
        attributes.put(EventConstants.Categories, categories);
        attributes.put(EventConstants.BasketValue, basketValue);
        attributes.put(EventConstants.Success, success);

        return attributes;
    }

    public static HashMap<String, Object> search(String categoryUrlKey, String keywords) {
        HashMap<String, Object> attributes = EventTracker.getBasicAttributes();
        attributes.put(EventConstants.CategoryUrlKey, categoryUrlKey);
        attributes.put(EventConstants.Keywords, keywords);

        return attributes;
    }

    public static HashMap<String, Object> viewProduct(String categoryUrlKey, long price) {
        HashMap<String, Object> attributes = EventTracker.getBasicAttributes();
        attributes.put(EventConstants.CategoryUrlKey, categoryUrlKey);
        attributes.put(EventConstants.Price, price);

        return attributes;
    }
}
