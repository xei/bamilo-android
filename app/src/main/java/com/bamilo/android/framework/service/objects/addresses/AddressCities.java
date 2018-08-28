package com.bamilo.android.framework.service.objects.addresses;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddressCities extends ArrayList<AddressCity> implements IJSONSerializable {

	public static final String TAG = AddressCities.class.getSimpleName();

    /**
     * Empty constructor
     */
    public AddressCities() {
    }

	public AddressCities(List<AddressCity> addressCities) {
		for(AddressCity addressCity : addressCities){
			add(addressCity);
		}
	}

	public AddressCities(AddressCities addressCities){
		this((List)addressCities);
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
			add(new AddressCity(json));
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
