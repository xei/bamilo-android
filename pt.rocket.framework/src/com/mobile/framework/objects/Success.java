package com.mobile.framework.objects;

import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;

public class Success {
	public static HashMap<String, String> createMapOfSuccess(JSONObject success) {
		HashMap<String, String> successHashMap = new HashMap<String, String>();
		Iterator<?> keys = success.keys();

        while (keys.hasNext()) {
        	String key = (String)keys.next();
        	Object value = success.opt(key);
            if(value instanceof String){
            	successHashMap.put(key, (String)value);
            }
        }
        return successHashMap;
		
	}
}
