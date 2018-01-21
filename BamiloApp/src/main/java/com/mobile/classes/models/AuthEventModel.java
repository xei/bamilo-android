package com.mobile.classes.models;

import com.mobile.constants.tracking.EmarsysEventConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by narbeh on 12/5/17.
 */

public final class AuthEventModel extends SimpleEventModel {
    public Map<String, Object> emarsysAttributes;

    public AuthEventModel(String category, String action, String label, long value, Map<String, Object> emarsysAttributes) {
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
}
