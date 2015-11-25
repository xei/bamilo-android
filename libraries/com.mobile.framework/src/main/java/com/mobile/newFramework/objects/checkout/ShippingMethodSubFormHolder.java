package com.mobile.newFramework.objects.checkout;

import com.mobile.newFramework.forms.PickUpStationObject;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by rsoares on 6/2/15.
 */
public class ShippingMethodSubFormHolder implements IJSONSerializable{

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
    public ShippingMethodSubFormHolder() {
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
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.optString(RestConstants.ID);
            name = jsonObject.optString(RestConstants.JSON_NAME_TAG);
            key = jsonObject.optString(RestConstants.KEY);
            scenario = jsonObject.optString(RestConstants.JSON_SCENARIO_TAG);
            if(jsonObject.has(RestConstants.TYPE)){
                type = jsonObject.optString(RestConstants.TYPE);
            } else {
                type = RestConstants.METADATA;
            }

            value = jsonObject.optString(RestConstants.VALUE);
            label = jsonObject.optString(RestConstants.LABEL);

            /**
             * TODO: Verify if on SubForm can be required for more then one Form
             * jsonObject.getJSONObject(RestConstants.JSON_VALIDATION_TAG);
             */

            required = true;
            if(jsonObject.has(RestConstants.JSON_OPTIONS_TAG)){
                JSONArray optionsArray = jsonObject.getJSONArray(RestConstants.JSON_OPTIONS_TAG);
                for (int i = 0; i < optionsArray.length(); i++) {
                    PickUpStationObject mPickUpStationObject = new PickUpStationObject();
                    mPickUpStationObject.initialize(optionsArray.getJSONObject(i));
                    options.add(mPickUpStationObject);
                }
            }

        } catch (JSONException e) {
            return false;
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
