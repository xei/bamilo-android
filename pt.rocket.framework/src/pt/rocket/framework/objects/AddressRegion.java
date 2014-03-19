package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

public class AddressRegion implements IJSONSerializable, Parcelable {

	private static final String TAG = LogTagHelper.create(AddressRegion.class);

	private int id;
	private int countryId;
	private String code;
	private String name;
	private String sort;

	/**
	 * Empty constructor
	 * @throws JSONException 
	 */
	public AddressRegion(JSONObject jsonObject) throws JSONException { 
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
	public int getCountryId() {
		return countryId;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the sort
	 */
	public String getSort() {
		return sort;
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
	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
//		return "ID: " + id + " " +
//				"COUNTRY: " + countryId + " " +
//				"CODE: " + code + " " +
//				"NAME: " + name + " " +
//				"SORT: " + sort
//				;
		return name;
	}

	/**
	 * ############### IJSON ###############
	 */

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		id = jsonObject.getInt("id_customer_address_region");
		countryId = jsonObject.getInt("fk_country");
		code = jsonObject.getString("code");
		name = jsonObject.getString("name");
		sort = jsonObject.optString("sort");
		Log.d(TAG, this.toString());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
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
		dest.writeInt(countryId);
		dest.writeString(code);
		dest.writeString(name);
		dest.writeString(sort);
	}

	/**
	 * Constructor with parcel
	 * 
	 * @param in
	 */
	private AddressRegion(Parcel in) {
		id = in.readInt();
		countryId = in.readInt();
		code = in.readString();
		name = in.readString();
		sort = in.readString();
	}

	/**
	 * The creator
	 */
	public static final Parcelable.Creator<AddressRegion> CREATOR = new Parcelable.Creator<AddressRegion>() {
		public AddressRegion createFromParcel(Parcel in) {
			return new AddressRegion(in);
		}

		public AddressRegion[] newArray(int size) {
			return new AddressRegion[size];
		}
	};

}
