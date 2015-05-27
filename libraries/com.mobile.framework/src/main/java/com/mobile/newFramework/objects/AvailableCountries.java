package com.mobile.newFramework.objects;

import com.mobile.framework.rest.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rsoares on 5/21/15.
 */
public class AvailableCountries extends ArrayList<CountryObject> implements com.mobile.newFramework.objects.IJSONSerializable {

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONArray sessionJSONArray = null;
        if (null != jsonObject) {
            sessionJSONArray = jsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);
        }
        if(sessionJSONArray != null){
            for (int i = 0; i < sessionJSONArray.length(); i++) {
                CountryObject mCountryObject = new CountryObject();
                try {
                    mCountryObject.initialize(sessionJSONArray.getJSONObject(i));
                    this.add(mCountryObject);
                } catch (JSONException e) {
//                    Log.w(TAG, "WARNING JSON EXCEPTION ON PARSE COUNTRIES", e);
                }
            }
        }

        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }
}
