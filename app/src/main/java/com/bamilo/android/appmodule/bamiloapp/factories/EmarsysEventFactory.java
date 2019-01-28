//package com.bamilo.android.appmodule.bamiloapp.factories;
//
//import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
//import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EmarsysEventConstants;
//import com.bamilo.android.appmodule.bamiloapp.managers.AppManager;
//import com.bamilo.android.appmodule.bamiloapp.utils.DateUtils;
//import com.bamilo.android.appmodule.bamiloapp.utils.tracking.BaseEventTracker;
//import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
//
//import java.util.Date;
//import java.util.HashMap;
//
///**
// * Created by Narbeh M. on 4/26/17.
// */
//
//public final class EmarsysEventFactory {
//    private static HashMap<String, Object> getBasicAttributes() {
//        HashMap<String, Object> attributes = new HashMap<>();
//
//        attributes.put(EmarsysEventConstants.AppVersion, AppManager.getAppFullFormattedVersion());
//        attributes.put(EmarsysEventConstants.Platform, "android");
//        attributes.put(EmarsysEventConstants.Connection, UIUtils.networkType(BamiloApplication.INSTANCE.getApplicationContext()));
//        attributes.put(EmarsysEventConstants.Date, DateUtils.getWebNormalizedDateTimeString(new Date()));
//        if(BamiloApplication.INSTANCE.isCustomerLoggedIn()) {
//            String userGender = BamiloApplication.CUSTOMER.getGender();
//            if(userGender != null) {
//                attributes.put(EmarsysEventConstants.Gender, userGender);
//            }
//        }
//
//        return attributes;
//    }
//
//    public enum OpenAppEventSourceType {
//        OPEN_APP_SOURCE_NONE("none", 0),
//        OPEN_APP_SOURCE_DIRECT("direct", 1),
//        OPEN_APP_SOURCE_DEEPLINK("deeplink", 2),
//        OPEN_APP_SOURCE_PUSH_NOTIFICATION("push_notification", 3);
//
//        private String stringValue;
//        private int intValue;
//        OpenAppEventSourceType(String toString, int value) {
//            stringValue = toString;
//            intValue = value;
//        }
//
//        @Override
//        public String toString() {
//            return stringValue;
//        }
//    }
//
//    public static HashMap<String, Object> login(String loginMethod, String emailDomain, boolean success) {
//        HashMap<String, Object> attributes = getBasicAttributes();
//        attributes.put(EmarsysEventConstants.Method, loginMethod);
//        attributes.put(EmarsysEventConstants.EmailDomain, getSafeEventValue(emailDomain));
//        attributes.put(EmarsysEventConstants.Success, success);
//
//        return attributes;
//    }
//
//    public static HashMap<String, Object> signup(String signupMethod, String emailDomain, boolean success) {
//        HashMap<String, Object> attributes = getBasicAttributes();
//        attributes.put(EmarsysEventConstants.Method, signupMethod);
//        attributes.put(EmarsysEventConstants.EmailDomain, getSafeEventValue(emailDomain));
//        attributes.put(EmarsysEventConstants.Success, success);
//
//        return attributes;
//    }
//
//    public static HashMap<String, Object> logout(boolean success) {
//        HashMap<String, Object> attributes = getBasicAttributes();
//        attributes.put(EmarsysEventConstants.Success, success);
//
//        return attributes;
//    }
//
//    public static HashMap<String, Object> openApp(OpenAppEventSourceType source) {
//        HashMap<String, Object> attributes = getBasicAttributes();
//        attributes.put(EmarsysEventConstants.Source, source.toString());
//
//        return attributes;
//    }
//
//    public static HashMap<String, Object> addToCart(String sku, long basketValue, boolean success) {
//        HashMap<String, Object> attributes = getBasicAttributes();
//        attributes.put(EmarsysEventConstants.SKU, sku);
//        attributes.put(EmarsysEventConstants.BasketValue, basketValue);
//        attributes.put(EmarsysEventConstants.Success, success);
//
//        return attributes;
//    }
//
//    public static HashMap<String, Object> addToFavorites(String categoryUrlKey, boolean success) {
//        HashMap<String, Object> attributes = getBasicAttributes();
//        attributes.put(EmarsysEventConstants.CategoryUrlKey, getSafeEventValue(categoryUrlKey));
//        attributes.put(EmarsysEventConstants.Success, success);
//
//        return attributes;
//    }
//
//    public static HashMap<String, Object> purchase(String categories, long basketValue, boolean success) {
//        HashMap<String, Object> attributes = getBasicAttributes();
//        attributes.put(EmarsysEventConstants.Categories, categories);
//        attributes.put(EmarsysEventConstants.BasketValue, basketValue);
//        attributes.put(EmarsysEventConstants.Success, success);
//
//        return attributes;
//    }
//
//    public static HashMap<String, Object> search(String categoryUrlKey, String keywords) {
//        HashMap<String, Object> attributes = getBasicAttributes();
//        attributes.put(EmarsysEventConstants.CategoryUrlKey, getSafeEventValue(categoryUrlKey));
//        attributes.put(EmarsysEventConstants.Keywords, keywords);
//
//        return attributes;
//    }
//
//    public static HashMap<String, Object> viewProduct(String categoryUrlKey, long price) {
//        HashMap<String, Object> attributes = getBasicAttributes();
//        attributes.put(EmarsysEventConstants.CategoryUrlKey, categoryUrlKey);
//        attributes.put(EmarsysEventConstants.Price, price);
//
//        return attributes;
//    }
//
//    private static String getSafeEventValue(String value) {
//        return value != null ? value : EmarsysEventConstants.UNKNOWN_EVENT_VALUE;
//    }
//}
