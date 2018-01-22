package com.mobile.classes.models;

import com.mobile.constants.tracking.EmarsysEventConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by narbeh on 12/5/17.
 */

public final class EmarsysEventModel extends SimpleEventModel {
    public Map<String, Object> emarsysAttributes;

    public EmarsysEventModel(String category, String action, String label, long value, Map<String, Object> emarsysAttributes) {
        super(category, action, label, value);
        this.emarsysAttributes = emarsysAttributes;
    }

    public static Map<String, Object> createAuthEventModelAttributes(String loginMethod, String emailDomain, boolean success) {
        HashMap<String, Object> attributes= new HashMap<>();
        attributes.put(EmarsysEventConstants.Method, loginMethod);
        attributes.put(EmarsysEventConstants.EmailDomain, emailDomain != null ? emailDomain : "");
        attributes.put(EmarsysEventConstants.Success, success);
        return attributes;
    }

    public static Map<String, Object> createAppOpenEventModelAttributes(String source) {
        HashMap<String, Object> attributes= new HashMap<>();
        attributes.put(EmarsysEventConstants.Source, source);
        return attributes;
    }

    public static Map<String, Object> createAddToCartEventModelAttributes(String sku, long basketValue, boolean success) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(EmarsysEventConstants.SKU, sku);
        attributes.put(EmarsysEventConstants.BasketValue, basketValue);
        attributes.put(EmarsysEventConstants.Success, success);

        return attributes;
    }
}
