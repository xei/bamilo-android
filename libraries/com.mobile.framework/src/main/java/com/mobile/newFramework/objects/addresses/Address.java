package com.mobile.newFramework.objects.addresses;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to save a Address
 *
 * @author sergiopereira
 */
public class Address implements IJSONSerializable, Parcelable {

    public static final String TAG = Address.class.getSimpleName();

    private int id;
    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String city;
    private String postcode;
    private String phone;
    private String additionalPhone;
    private String region;

    /**
     * Constructor
     */
    public Address() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject dataObject) throws JSONException {
        // Mandatory
        // TODO ID
        id = dataObject.optInt(RestConstants.CUSTOMER_ADDRESS_ID);
   /*     if (id == 0) {
            id = dataObject.getInt(RestConstants.ID);
        }*/
        firstName = dataObject.getString(RestConstants.FIRST_NAME);
        lastName = dataObject.getString(RestConstants.LAST_NAME);
        address1 = dataObject.getString(RestConstants.ADDRESS_1);
        // Optional
        address2 = dataObject.optString(RestConstants.ADDRESS_2);
        postcode = dataObject.optString(RestConstants.POSTCODE);
        phone = dataObject.getString(RestConstants.PHONE);
        region = dataObject.optString(RestConstants.REGION);
        city = dataObject.optString(RestConstants.CITY);
        additionalPhone = dataObject.optString(RestConstants.ADDITIONAL_PHONE);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address1;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getRegion() {
        return region;
    }

    /**
     * ########### PARCEL ###########
     */

	/*
     * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(address1);
        dest.writeString(address2);
        dest.writeString(city);
        dest.writeString(postcode);
        dest.writeString(phone);
        dest.writeString(additionalPhone);
        dest.writeString(region);
    }

    /**
     * Parcel constructor
     */
    public Address(Parcel in) {
        id = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        address1 = in.readString();
        address2 = in.readString();
        city = in.readString();
        postcode = in.readString();
        phone = in.readString();
        additionalPhone = in.readString();
        region = in.readString();
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

}

