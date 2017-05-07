package com.mobile.newFramework.objects.addresses;

import org.json.JSONException;
import org.json.JSONObject;

public class AddressRegion extends FormListItem {

    public AddressRegion(int mValue, String mLabel) {
        super(mValue, mLabel);
    }

    public AddressRegion(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }
}
