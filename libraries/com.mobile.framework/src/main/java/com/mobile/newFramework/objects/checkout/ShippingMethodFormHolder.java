package com.mobile.newFramework.objects.checkout;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by rsoares on 6/2/15.
 */
public class ShippingMethodFormHolder implements IJSONSerializable{

    public String id;
    public String key;
    public String name;
    public String value;
    public String label;
    public String type;
    public boolean required;
    public ArrayList<String> options;
    public HashMap<String, ShippingMethodOption> optionsShippingMethod;
    public ArrayList<ShippingMethodSubFormHolder> shippingMethodsSubForms;

    /**
     * Form empty constructor.
     */
    public ShippingMethodFormHolder() {
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
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.optString(RestConstants.ID);
            name = jsonObject.optString(RestConstants.JSON_NAME_TAG);
            key = jsonObject.optString(RestConstants.JSON_KEY_TAG);
            value = jsonObject.optString(RestConstants.VALUE);
            label = jsonObject.optString(RestConstants.LABEL);
            type = jsonObject.optString(RestConstants.TYPE);

            if(jsonObject.has(RestConstants.JSON_RULES_TAG)){
                required = jsonObject.getJSONObject(RestConstants.JSON_RULES_TAG).optBoolean(RestConstants.JSON_REQUIRED_TAG, false);
            }

            JSONObject optionsObject = jsonObject.getJSONObject(RestConstants.JSON_OPTIONS_TAG);

            Iterator<?> opts = optionsObject.keys();
            while (opts.hasNext()) {
                String key = opts.next().toString();
                options.add(key);
                ShippingMethodOption shippingMethod = new ShippingMethodOption();
                shippingMethod.initialize(key, optionsObject.optJSONObject(key));
                optionsShippingMethod.put(key, shippingMethod);
            }

        } catch (JSONException e) {
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

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

}
