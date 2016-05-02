package com.mobile.newFramework.objects.addresses;

import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent a return reason.
 * - label: "I want a different item / لقد أردت منتج آخر مختلف",
 * - value: "R 2- Customer - wants different item"
 * @author spereira
 */
public class ReturnReason extends FormListItem {

    private String mKey;

    public ReturnReason(String value, String label) {
        super(IntConstants.INVALID_POSITION, label);
        this.mKey = value;
    }

    public ReturnReason(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        this.mKey = jsonObject.getString(RestConstants.VALUE);
        super.mLabel = jsonObject.getString(RestConstants.LABEL);
        return true;
    }

    public String getKey() {
        return mKey;
    }

    @Override
    public String getValueAsString() {
        return mKey;
    }
}
