package com.bamilo.android.framework.service.objects.addresses;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddressPostalCodes extends ArrayList<AddressPostalCode> implements IJSONSerializable {

	public static final String TAG = AddressPostalCodes.class.getSimpleName();

    /**
     * Empty constructor
     */
    public AddressPostalCodes() {
    }

	public AddressPostalCodes(List<AddressPostalCode> addressCities) {
		for(AddressPostalCode addressPostalCode : addressCities){
			add(addressPostalCode);
		}
	}

	/**
	 * ############### IJSON ###############
	 */

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		// For each item
		JSONArray jsonArray = jsonObject.getJSONArray(RestConstants.DATA);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			add(new AddressPostalCode(json));
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
		return RequiredJson.METADATA;
	}

}
