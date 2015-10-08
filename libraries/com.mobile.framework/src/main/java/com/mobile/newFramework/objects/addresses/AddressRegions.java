package com.mobile.newFramework.objects.addresses;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddressRegions extends ArrayList<AddressRegion> implements IJSONSerializable {

    public static final String TAG = AddressRegions.class.getSimpleName();

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
        // For each item
        JSONArray jsonArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            // Save the region
            add(new AddressRegion(json));
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
