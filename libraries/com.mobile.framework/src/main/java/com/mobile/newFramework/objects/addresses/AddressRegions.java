package com.mobile.newFramework.objects.addresses;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.LogTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddressRegions extends ArrayList<AddressRegion> implements IJSONSerializable {

    public static final String TAG = LogTagHelper.create(AddressRegions.class);

    /**
     * Empty constructor
     */
    public AddressRegions() {
    }

    /**
     * ############### IJSON ###############
     */

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }


    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        try {
            // For each item
            JSONArray jsonArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                // Save the region
                add(new AddressRegion(json));
            }
        } catch (JSONException e) {
            // Log.w(TAG, "PARSE EXCEPTION", e);
            return false;
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

}
