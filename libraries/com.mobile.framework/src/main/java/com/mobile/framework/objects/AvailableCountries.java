package com.mobile.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by rsoares on 5/21/15.
 */
public class AvailableCountries implements IJSONSerializable{
    private List<CountryObject> availableCountries;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        return false;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
