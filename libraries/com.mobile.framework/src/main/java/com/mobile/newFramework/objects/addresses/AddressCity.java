package com.mobile.newFramework.objects.addresses;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.utils.LogTagHelper;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

public class AddressCity implements IJSONSerializable, Parcelable {

	private static final String TAG = LogTagHelper.create(AddressCity.class);

	private int id;
	private String value;

	/**
	 * Empty constructor
	 * @throws JSONException
	 */
	public AddressCity(JSONObject jsonObject) throws JSONException {
		initialize(jsonObject);
	}

	/**
	 * ########### GETTERS ###########
	 */

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the countryId
	 */
	public String getValue() {
		return value;
	}



	/**
	 * ########### SETTERS ###########
	 */

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}



	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
//		return "ID: " + id + " " +
//				"VALUE: " + value + " " +
//				"GROUP: " + group
//				;
		return value;
	}

	/**
	 * ############### IJSON ###############
	 */

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		id = jsonObject.getInt("id");
		value = jsonObject.getString("value");
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
		return RequiredJson.OBJECT_DATA;
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
		dest.writeInt(id);
		dest.writeString(value);
	}

	/**
	 * Constructor with parcel
	 *
	 * @param in
	 */
	private AddressCity(Parcel in) {
		id = in.readInt();
		value = in.readString();
	}

	/**
	 * The creator
	 */
	public static final Creator<AddressCity> CREATOR = new Creator<AddressCity>() {
		public AddressCity createFromParcel(Parcel in) {
			return new AddressCity(in);
		}

		public AddressCity[] newArray(int size) {
			return new AddressCity[size];
		}
	};

}
