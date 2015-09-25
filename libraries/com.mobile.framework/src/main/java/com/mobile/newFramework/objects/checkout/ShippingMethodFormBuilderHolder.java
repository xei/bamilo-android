package com.mobile.newFramework.objects.checkout;

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
public class ShippingMethodFormBuilderHolder implements IJSONSerializable{
    public String id;
    public String name;
    public String method;
    public String action;
    // private float scale = 1;

    public ArrayList<ShippingMethodFormHolder> fields;

    /**
     * Empty constructor.
     */
    public ShippingMethodFormBuilderHolder() {
        this.id = "";
        this.name = "";
        this.method = "";
        this.action = "";
        this.fields = new ArrayList<>();
    }

    public ShippingMethodFormBuilderHolder(JSONObject jsonObject) {
        this();
        initialize(jsonObject);
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
            name = jsonObject.optString(RestConstants.JSON_FORM_TAG);
            method = jsonObject.optString(RestConstants.METHOD);
            action = jsonObject.optString(RestConstants.JSON_ACTION_TAG);

            fields.clear();

            JSONArray fieldsArray = jsonObject.getJSONArray(RestConstants.JSON_FIELDS_TAG);
            if(fieldsArray != null){
                for (int i = 0; i < fieldsArray.length(); ++i) {
                    if(!fieldsArray.getJSONObject(i).has(RestConstants.JSON_SCENARIO_TAG)){
                        ShippingMethodFormHolder field = new ShippingMethodFormHolder();
                        if (field.initialize(fieldsArray.getJSONObject(i))) {
                            fields.add(field);
                        }
                    } else {
                        ShippingMethodSubFormHolder subForm = new ShippingMethodSubFormHolder();
                        subForm.initialize(fieldsArray.getJSONObject(i));
                        for ( int j = 0; j < fields.size(); j++) {
                            if(fields.get(j).options.contains(subForm.scenario)){
                                fields.get(j).shippingMethodsSubForms.add(subForm);
                            }
                        }
                    }
                }
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.ID, id);
            jsonObject.put(RestConstants.JSON_FORM_TAG, name);
            jsonObject.put(RestConstants.METHOD, method);
            jsonObject.put(RestConstants.JSON_ACTION_TAG, action);

            JSONArray fieldArray = new JSONArray();
            for (ShippingMethodFormHolder field : fields) {
                fieldArray.put(field.toJSON());
            }

            jsonObject.put(RestConstants.JSON_FIELDS_TAG, fieldArray);

        } catch (JSONException e) {
//            Log.e(TAG, "trying to create json objects failed", e );
        }
        return jsonObject;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

}
