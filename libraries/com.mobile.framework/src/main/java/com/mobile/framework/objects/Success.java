package com.mobile.framework.objects;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class Success {
	public static HashMap<String, String> createMap(JSONObject messages) throws JSONException {
		if(messages != null) {
			Object obj = messages.get(RestConstants.JSON_SUCCESS_TAG);
			if (obj instanceof JSONArray) {
				return createMapOfSuccess((JSONArray) obj);
			}

			if (obj instanceof JSONObject) {
				return createMapOfSuccess((JSONObject) obj);
			}
		}
		return  null;
	}

	private static HashMap<String, String> createMapOfSuccess(JSONObject success){
		if(success != null) {
			HashMap<String, String> successHashMap = new HashMap<String, String>();
			Iterator<?> keys = success.keys();

			while (keys.hasNext()) {
				String key = (String) keys.next();
				Object value = success.opt(key);
				if (value instanceof String) {
					successHashMap.put(key, (String) value);
				}
			}
			return successHashMap;
		} else {
			return null;
		}
	}

	private static HashMap<String, String> createMapOfSuccess(JSONArray success) throws JSONException {
		if(success != null) {
			HashMap<String, String> successHashMap = new HashMap<String, String>();
			for (int i = 0; i < success.length(); i++) {
				successHashMap.put(RestConstants.JSON_SUCCESS_TAG,success.getString(i));
			}
			return successHashMap;
		} else {
			return null;
		}
	}
}
