package com.mobile.newFramework.objects.addresses;

import org.json.JSONException;
import org.json.JSONObject;

public class AddressPostalCode extends FormListItem {

    public AddressPostalCode(int mValue, String mLabel) {
        super(mValue, mLabel);
    }

    public AddressPostalCode(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

}
