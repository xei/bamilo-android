package com.bamilo.android.framework.service.objects.checkout;

import com.bamilo.android.framework.service.forms.PickUpStationObject;
import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by rsoares on 6/2/15.
 */
public class ShippingFormFieldPUS implements IJSONSerializable {

    public String key;
    public String scenario;
    public ArrayList<PickUpStationObject> options;
    public String type;
    public boolean required;
    public String value;
    public String name;
    public String id;
    public String label;

    /**
     * Form empty constructor.
     */
    public ShippingFormFieldPUS() {
        this.key = "";
        this.scenario = "";
        this.options = new ArrayList<>();
        this.type = "";
        this.required = false;
        this.value = "";
        this.id = "";
        this.name = "";
        this.label = "";
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get field info
        name = jsonObject.optString(RestConstants.NAME);
        key = jsonObject.optString(RestConstants.KEY);
        scenario = jsonObject.optString(RestConstants.SCENARIO);
        type = jsonObject.optString(RestConstants.TYPE);
        value = jsonObject.optString(RestConstants.VALUE);
        label = jsonObject.optString(RestConstants.LABEL);
        if (jsonObject.has(RestConstants.RULES)) {
            required = jsonObject.getJSONObject(RestConstants.RULES).optBoolean(RestConstants.REQUIRED);
        }
        // Get PUS options
        if (jsonObject.has(RestConstants.OPTIONS)) {
            JSONArray optionsArray = jsonObject.getJSONArray(RestConstants.OPTIONS);
            for (int i = 0; i < optionsArray.length(); i++) {
                PickUpStationObject mPickUpStationObject = new PickUpStationObject();
                mPickUpStationObject.initialize(optionsArray.getJSONObject(i));
                options.add(mPickUpStationObject);
            }
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

}
