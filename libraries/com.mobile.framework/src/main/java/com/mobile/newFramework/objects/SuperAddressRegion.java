package com.mobile.newFramework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.LogTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuperAddressRegion implements IJSONSerializable, Parcelable {

	private static final String TAG = LogTagHelper.create(SuperAddressRegion.class);

	private ArrayList<AddressRegion> regions;

    /**
     * Empty constructor
     */
    public SuperAddressRegion() {
    }

	/**
	 * Empty constructor
	 * @throws JSONException
	 */
	public SuperAddressRegion(JSONObject jsonObject) throws JSONException {
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
			regions = new ArrayList<>();
			// For each item
			JSONArray jsonArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				// Save the region
				regions.add(new AddressRegion(json));
			}
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
        if (regions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(regions);
        };

	}

	/**
	 * Constructor with parcel
	 *
	 * @param in
	 */
	private SuperAddressRegion(Parcel in) {
        if (in.readByte() == 0x01) {
            regions = new ArrayList<AddressRegion>();
            in.readList(regions, AddressRegion.class.getClassLoader());
        } else {
            regions = null;
        }

	}

	/**
	 * The creator
	 */
	public static final Creator<SuperAddressRegion> CREATOR = new Creator<SuperAddressRegion>() {
		public SuperAddressRegion createFromParcel(Parcel in) {
			return new SuperAddressRegion(in);
		}

		public SuperAddressRegion[] newArray(int size) {
			return new SuperAddressRegion[size];
		}
	};

    public ArrayList<AddressRegion> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<AddressRegion> regions) {
        this.regions = regions;
    }
}
