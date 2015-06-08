//package com.mobile.framework.objects;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.framework.rest.RestConstants;
//import com.mobile.newFramework.utils.LogTagHelper;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class AddressRegion implements IJSONSerializable, Parcelable {
//
//	public static final String TAG = LogTagHelper.create(AddressRegion.class);
//
//	private int id;
//	private String name;
//
//	/**
//	 * Empty constructor
//	 * @throws JSONException
//	 */
//	public AddressRegion(JSONObject jsonObject) throws JSONException {
//		initialize(jsonObject);
//	}
//
//	/**
//	 * ########### GETTERS ###########
//	 */
//
//	/**
//	 * @return the id
//	 */
//	public int getId() {
//		return id;
//	}
//
//	/**
//	 * @return the name
//	 */
//	public String getName() {
//		return name;
//	}
//
//	/**
//	 * ########### SETTERS ###########
//	 */
//
//	/**
//	 * @param id
//	 *            the id to set
//	 */
//	public void setId(int id) {
//		this.id = id;
//	}
//
//	/**
//	 * @param name
//	 *            the name to set
//	 */
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
////		return "ID: " + id + " " +
////				"COUNTRY: " + countryId + " " +
////				"CODE: " + code + " " +
////				"NAME: " + name + " " +
////				"SORT: " + sort
////				;
//		return name;
//	}
//
//	/**
//	 * ############### IJSON ###############
//	 */
//
//	@Override
//	public boolean initialize(JSONObject jsonObject) throws JSONException {
//		id = jsonObject.getInt(RestConstants.JSON_ID_ADDRESS_REGION_TAG);
//		name = jsonObject.getString(RestConstants.JSON_NAME_TAG);
//		return true;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//	 */
//	@Override
//	public JSONObject toJSON() {
//		return null;
//	}
//
//	/**
//	 * ############### Parcelable ###############
//	 */
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see android.os.Parcelable#describeContents()
//	 */
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
//	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeInt(id);
//		dest.writeString(name);
//	}
//
//	/**
//	 * Constructor with parcel
//	 *
//	 * @param in
//	 */
//	private AddressRegion(Parcel in) {
//		id = in.readInt();
//		name = in.readString();
//	}
//
//	/**
//	 * The creator
//	 */
//	public static final Parcelable.Creator<AddressRegion> CREATOR = new Parcelable.Creator<AddressRegion>() {
//		public AddressRegion createFromParcel(Parcel in) {
//			return new AddressRegion(in);
//		}
//
//		public AddressRegion[] newArray(int size) {
//			return new AddressRegion[size];
//		}
//	};
//
//}
