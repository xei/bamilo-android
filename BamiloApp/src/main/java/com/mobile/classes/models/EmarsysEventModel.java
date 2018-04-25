package com.mobile.classes.models;

import com.mobile.constants.tracking.EmarsysEventConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by narbeh on 12/5/17.
 */

public final class EmarsysEventModel extends SimpleEventModel {
    public Map<String, String> emarsysAttributes;

    public EmarsysEventModel(String category, String action, String label, long value, Map<String, String> emarsysAttributes) {
        super(category, action, label, value);
        this.emarsysAttributes = emarsysAttributes;
    }

    public static Map<String, String> createAuthEventModelAttributes(String loginMethod, String emailDomain, boolean success) {
        HashMap<String, String> attributes= new HashMap<>();
        attributes.put(EmarsysEventConstants.Method, loginMethod != null ? loginMethod : "");
        attributes.put(EmarsysEventConstants.EmailDomain, emailDomain != null ? emailDomain : "");
        attributes.put(EmarsysEventConstants.Success, Boolean.toString(success));
        return attributes;
    }

    public static Map<String, String> createAppOpenEventModelAttributes(String source) {
        HashMap<String, String> attributes= new HashMap<>();
        attributes.put(EmarsysEventConstants.Source, source != null ? source : "");
        return attributes;
    }

    public static Map<String, String> createAddToCartEventModelAttributes(String sku, long basketValue, boolean success) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(EmarsysEventConstants.SKU, sku);
        attributes.put(EmarsysEventConstants.BasketValue, Long.toString(basketValue));
        attributes.put(EmarsysEventConstants.Success, Boolean.toString(success));

        return attributes;
    }

    public static Map<String, String> createAddToWishListEventModelAttributes(String sku, String categoryUrlKey, boolean success) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(EmarsysEventConstants.SKU, sku != null ? sku : "");
        attributes.put(EmarsysEventConstants.CategoryUrlKey, categoryUrlKey != null ? categoryUrlKey : "");
        attributes.put(EmarsysEventConstants.Success, Boolean.toString(success));

        return attributes;
    }

    public static Map<String, String> createPurchaseEventModelAttributes(String categories, long basketValue, boolean success) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(EmarsysEventConstants.Categories, categories);
        attributes.put(EmarsysEventConstants.BasketValue, Long.toString(basketValue));
        attributes.put(EmarsysEventConstants.Success, Boolean.toString(success));

        return attributes;
    }

    public static Map<String, String> createSearchEventModelAttributes(String categoryUrlKey, String keywords) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(EmarsysEventConstants.CategoryUrlKey, categoryUrlKey != null ? categoryUrlKey : "");
        attributes.put(EmarsysEventConstants.Keywords, keywords);

        return attributes;
    }

    public static Map<String, String> createViewProductEventModelAttributes(String categoryUrlKey, long price) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(EmarsysEventConstants.CategoryUrlKey, categoryUrlKey);
        attributes.put(EmarsysEventConstants.Price, Long.toString(price));

        return attributes;
    }

    public static Map<String, String> createRemoveFromCartEventModelAttributes(String sku) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(EmarsysEventConstants.SKU, sku);

        return attributes;
    }
}
