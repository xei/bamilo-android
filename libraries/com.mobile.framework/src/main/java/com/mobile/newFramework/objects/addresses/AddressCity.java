package com.mobile.newFramework.objects.addresses;

import org.json.JSONException;
import org.json.JSONObject;

public class AddressCity extends FormListItem {

    public AddressCity(int mValue, String mLabel) {
        super(mValue, mLabel);
    }

    public AddressCity(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

}
