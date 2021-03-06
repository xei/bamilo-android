package com.bamilo.android.framework.service.objects.addresses;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

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
    private boolean isValid;
    private boolean isDefault;

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
        // TODO VALIDATE THIS
        id = dataObject.optInt(RestConstants.CUSTOMER_ADDRESS_ID);
        if (id == 0) {
            id = dataObject.optInt(RestConstants.ID);
        }
        if (id == 0) {
            id = dataObject.getInt(RestConstants.ID_SALES_ORDER_ADDRESS);
        }
        firstName = dataObject.optString(RestConstants.FIRST_NAME);
        lastName = dataObject.optString(RestConstants.LAST_NAME);
        address1 = dataObject.optString(RestConstants.ADDRESS_1);
        // Optional
        if (!dataObject.isNull(RestConstants.ADDRESS_2)) {
            address2 = dataObject.optString(RestConstants.ADDRESS_2);
        }
        if (!dataObject.isNull(RestConstants.POSTCODE)) {
            postcode = dataObject.optString(RestConstants.POSTCODE);
        }
        phone = dataObject.optString(RestConstants.PHONE);
        region = dataObject.optString(RestConstants.REGION);
        city = dataObject.optString(RestConstants.CITY);
        additionalPhone = dataObject.optString(RestConstants.ADDITIONAL_PHONE);

        isValid = dataObject.optBoolean(RestConstants.IS_VALID, true);
        isDefault = false;
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
    public int getRequiredJson() {
        return RequiredJson.METADATA;
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

    public String getAddress2() {
        return address2;
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
     * Check whether the Address is valid or not.
     */
    public boolean isValid() {
        return isValid;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean value) {
        isDefault = value;
    }

    public String getAddressString() {
        String result = "";
        result += getCity() + " - " + getAddress() + "\r\n" + getFirstName() + " " + getLastName();
        return result;
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
        dest.writeByte((byte) (isValid ? 0x01 : 0x00));
    }

    /**
     * Parcel constructor
     */
    private Address(Parcel in) {
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
        isValid = in.readByte() != 0x00;
    }

    /**
     * Create parcelable
     */
    public static final Creator<Address> CREATOR = new Creator<Address>() {
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

}

