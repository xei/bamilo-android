package com.mobile.service.objects.checkout;

import com.mobile.service.forms.FormField;
import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to represent the shipping form.
 * @author rsoares
 * @modified sergio pereira
 */
public class ShippingForm implements IJSONSerializable {

    public String name;
    public String method;
    public String action;
    public ArrayList<ShippingFormField> fields;

    /**
     * Empty constructor.
     */
    public ShippingForm() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject json) throws JSONException {
        JSONObject jsonObject = json.getJSONObject(RestConstants.FORM_ENTITY);
        name = jsonObject.optString(RestConstants.FORM);
        method = jsonObject.optString(RestConstants.METHOD);
        action = jsonObject.optString(RestConstants.ACTION);
        // Get shipping fields
        JSONArray fieldsArray = jsonObject.getJSONArray(RestConstants.FIELDS);
        if (fieldsArray != null) {
            fields = new ArrayList<>();
            for (int i = 0; i < fieldsArray.length(); ++i) {
                // Get json
                JSONObject jsonField = fieldsArray.getJSONObject(i);
                // Get key
                String type = jsonField.optString(RestConstants.TYPE);
                // Case radio options (All shipping options)
                if (TextUtils.equals(type, FormField.RADIO)) {
                    ShippingFormField field = new ShippingFormField();
                    field.initialize(fieldsArray.getJSONObject(i));
                    fields.add(field);
                }
                // Case pickup station and regions
                else {
                    ShippingFormFieldPUS subForm = new ShippingFormFieldPUS();
                    subForm.initialize(fieldsArray.getJSONObject(i));
                    // Find the saved "PickupStation" key
                    for (int j = 0; j < fields.size(); j++) {
                        if (fields.get(j).options.contains(subForm.scenario)) {
                            fields.get(j).shippingMethodsSubForms.add(subForm);
                        }
                    }
                }
            }
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
