package com.mobile.newFramework.objects.addresses;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to save the phone prefixes
 * @author spereira
 */
public class PhonePrefixes extends ArrayList<PhonePrefix> implements IJSONSerializable {

    public static final String TAG = PhonePrefixes.class.getSimpleName();

    private int defaultPosition = 0;

    /**
     * Empty constructor
     */
    public PhonePrefixes() {
        // ...
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // For each item
        JSONArray jsonArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            // Create
            PhonePrefix prefix = new PhonePrefix(json);
            // Default
            if(prefix.isDefault()) defaultPosition = i;
            // Save
            add(prefix);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public int getDefaultPosition() {
        return defaultPosition;
    }

    /**
     * return a prefix position by knowing it's value
     */
    public int getPositionFromValue(String value) {
        if(TextUtils.isNotEmpty(value)){
            for (int i = 0; i < this.size() ; i++) {
                if(this.get(i).getValue() == Integer.parseInt(value)){
                    return i;
                }
            }
        }
        return IntConstants.INVALID_POSITION;
    }

}
