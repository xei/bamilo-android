package com.mobile.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.utils.LogTagHelper;

import de.akquinet.android.androlog.Log;

public class AddressCity implements IJSONSerializable, Parcelable {

	private static final String TAG = LogTagHelper.create(AddressCity.class);

	private int id;
	private String value;
	private String group;

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
	 * @return the code
	 */
	public String getGroup() {
		return group;
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
	 * @param countryId
	 *            the countryId to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setGroup(String group) {
		this.group = group;
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
		group = jsonObject.getString("group");
		Log.d(TAG, this.toString());
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
		dest.writeString(group);
	}

	/**
	 * Constructor with parcel
	 * 
	 * @param in
	 */
	private AddressCity(Parcel in) {
		id = in.readInt();
		value = in.readString();
		group = in.readString();
	}

	/**
	 * The creator
	 */
	public static final Parcelable.Creator<AddressCity> CREATOR = new Parcelable.Creator<AddressCity>() {
		public AddressCity createFromParcel(Parcel in) {
			return new AddressCity(in);
		}

		public AddressCity[] newArray(int size) {
			return new AddressCity[size];
		}
	};

}
