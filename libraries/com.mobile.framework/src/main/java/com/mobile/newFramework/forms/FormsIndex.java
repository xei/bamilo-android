package com.mobile.newFramework.forms;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Defines the super object to handle data from the form.
 * @author Paulo Carvalho
 *
 */
public class FormsIndex extends HashMap<String, FormData> implements IJSONSerializable {

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        JSONArray dataArray;
        try {
            dataArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
            int dataArrayLength = dataArray.length();
            for (int i = 0; i < dataArrayLength; ++i) {
                JSONObject formDataObject = dataArray.getJSONObject(i);
                FormData formData = new FormData();
                formData.initialize(formDataObject);
           //     put(formData.getAction(), formData);
                put(formData.getType(), formData);  //type instead of action: mobapi 1.8
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }

}
