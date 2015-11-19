//package com.mobile.newFramework.pojo;
//
//import android.support.annotation.Nullable;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//
//public class Success {
//
//    public static @Nullable HashMap<String, String> createMap(JSONObject messages) throws JSONException {
//        return messages != null ? createMapOfSuccess(messages.optJSONArray(RestConstants.SUCCESS)) : null;
//    }
//
//    private static @Nullable HashMap<String, String> createMapOfSuccess(JSONArray success) throws JSONException {
//        if (success != null) {
//            HashMap<String, String> successHashMap = new HashMap<>();
//            for (int i = 0; i < success.length(); i++) {
//                successHashMap.put(RestConstants.SUCCESS, success.getString(i));
//            }
//            return successHashMap;
//        } else {
//            return null;
//        }
//    }
//}
