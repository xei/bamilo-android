package com.mobile.newFramework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.LogTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuperAddressCity implements IJSONSerializable, Parcelable {

	private static final String TAG = LogTagHelper.create(SuperAddressCity.class);

	private ArrayList<AddressCity> cities;


    /**
     * Empty constructor
     */
    public SuperAddressCity() {
    }


    /**
	 * Empty constructor
	 * @throws JSONException
	 */
	public SuperAddressCity(JSONObject jsonObject) throws JSONException {
		initialize(jsonObject);
	}

	/**
	 * ########### GETTERS ###########
	 */



	/**
	 * ############### IJSON ###############
	 */

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {

		try {
			// Regions
			cities = new ArrayList<>();
			// For each item
			JSONArray jsonArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				// Save the region
				cities.add(new AddressCity(json));
			}
            //FIXME
//          bundle.putString(CUSTOM_TAG, customTag);

        } catch (JSONException e) {
//			Log.w(TAG, "PARSE EXCEPTION", e);
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
		// TODO
		return null;
	}

	@Override
	public RequiredJson getRequiredJson() {
		return RequiredJson.METADATA;
	}

	/**
	 * ############### Parcelable ###############
	 */

	/*
	 * (non-Javadoc)
	 *
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
        if (cities == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(cities);
        };

	}

	/**
	 * Constructor with parcel
	 *
	 * @param in
	 */
	private SuperAddressCity(Parcel in) {
        if (in.readByte() == 0x01) {
			cities = new ArrayList<AddressCity>();
            in.readList(cities, AddressCity.class.getClassLoader());
        } else {
			cities = null;
        }

	}

	/**
	 * The creator
	 */
	public static final Creator<SuperAddressCity> CREATOR = new Creator<SuperAddressCity>() {
		public SuperAddressCity createFromParcel(Parcel in) {
			return new SuperAddressCity(in);
		}

		public SuperAddressCity[] newArray(int size) {
			return new SuperAddressCity[size];
		}
	};

    public ArrayList<AddressCity> getCities() {
        return cities;
    }

    public void setCities(ArrayList<AddressCity> cities) {
        this.cities = cities;
    }
}
