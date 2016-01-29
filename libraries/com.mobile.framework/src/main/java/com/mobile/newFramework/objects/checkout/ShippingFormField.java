package com.mobile.newFramework.objects.checkout;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rsoares on 6/2/15.
 */
public class ShippingFormField implements IJSONSerializable{

    public String id;
    public String key;
    public String name;
    public String value;
    public String label;
    public String type;
    public boolean required;
    public ArrayList<String> options;
    public HashMap<String, ShippingMethodOption> optionsShippingMethod;
    public ArrayList<ShippingFormFieldPUS> shippingMethodsSubForms;

    /**
     * Form empty constructor.
     */
    public ShippingFormField() {
        this.id = "";
        this.key = "";
        this.name = "";
        this.value = "";
        this.label = "";
        this.type = "";
        this.required = false;
        this.options = new ArrayList<>();
        this.optionsShippingMethod = new HashMap<>();
        this.shippingMethodsSubForms = new ArrayList<>();
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
        id = jsonObject.optString(RestConstants.ID);
        name = jsonObject.optString(RestConstants.NAME);
        key = jsonObject.optString(RestConstants.KEY);
        value = jsonObject.optString(RestConstants.VALUE);
        label = jsonObject.optString(RestConstants.LABEL);
        type = jsonObject.optString(RestConstants.TYPE);
        if (jsonObject.has(RestConstants.RULES)) {
            required = jsonObject.getJSONObject(RestConstants.RULES).optBoolean(RestConstants.REQUIRED);
        }
        // Save option
        JSONArray options = jsonObject.getJSONArray(RestConstants.OPTIONS);
        for (int i = 0; i < options.length(); i++) {
            ShippingMethodOption shippingMethod = new ShippingMethodOption();
            shippingMethod.initialize(options.getJSONObject(i));
            // Save the option with the value as a key
            this.options.add(shippingMethod.value);
            this.optionsShippingMethod.put(shippingMethod.value, shippingMethod);
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
        return RequiredJson.NONE;
    }

}
