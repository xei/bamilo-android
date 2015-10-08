package com.mobile.newFramework.pojo;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Success {

    public static @Nullable HashMap<String, String> createMap(JSONObject messages) throws JSONException {
        return messages != null ? createMapOfSuccess(messages.optJSONArray(RestConstants.JSON_SUCCESS_TAG)) : null;
        // TODO : VALIDATE IF THIS IS NECESSARY
//        if (messages != null) {
//          Object obj = messages.get(RestConstants.JSON_SUCCESS_TAG);
//			// Case array
//			if (obj instanceof JSONArray) {
//            return createMapOfSuccess((JSONArray) obj);
//			}
//			// Case object
//			else if (obj instanceof JSONObject) {
//				return createMapOfSuccess((JSONObject) obj);
//			}
//        }
//        return null;
    }

//    private static @Nullable HashMap<String, String> createMapOfSuccess(JSONObject success) {
//        if (success != null) {
//            HashMap<String, String> successHashMap = new HashMap<>();
//            Iterator<?> keys = success.keys();
//
//            while (keys.hasNext()) {
//                String key = (String) keys.next();
//                Object value = success.opt(key);
//                if (value instanceof String) {
//                    successHashMap.put(key, (String) value);
//                }
//            }
//            return successHashMap;
//        } else {
//            return null;
//        }
//    }

    private static @Nullable HashMap<String, String> createMapOfSuccess(JSONArray success) throws JSONException {
        if (success != null) {
            HashMap<String, String> successHashMap = new HashMap<>();
            for (int i = 0; i < success.length(); i++) {
                successHashMap.put(RestConstants.JSON_SUCCESS_TAG, success.getString(i));
            }
            return successHashMap;
        } else {
            return null;
        }
    }
}
