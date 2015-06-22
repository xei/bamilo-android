package com.mobile.newFramework.objects.statics;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 6/22/15.
 */
public class StaticTermsConditions implements IJSONSerializable{

    private String html;
    private String type;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        html = jsonObject.getString(RestConstants.JSON_HTML_TAG);
        type = jsonObject.getString(RestConstants.JSON_TYPE_TAG);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.OBJECT_DATA;
    }
}
