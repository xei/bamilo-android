/**
 * @author Guilherme Silva
 * @version 1.01
 * <p/>
 * 2012/06/18
 * <p/>
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package com.mobile.newFramework.objects.addresses;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONObject;

/**
 * #################### ADDRESS ####################  
 */

/**
 * Class used to save a Address
 * @author sergiopereira
 *
 */
public class Address implements IJSONSerializable, Parcelable {

    private static final String TAG = Address.class.getSimpleName();

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

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Constructor
     */
    public Address(JSONObject jsonObject) {
        initialize(jsonObject);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject dataObject) {
        Print.d(TAG, "INITIALIZE");
        try {
            id = dataObject.getInt(RestConstants.ID);
            firstName = dataObject.getString(RestConstants.JSON_FIRST_NAME_TAG);
            lastName = dataObject.getString(RestConstants.JSON_LAST_NAME_TAG);
            address1 = dataObject.getString(RestConstants.JSON_ADDRESS1_TAG);
            address2 = dataObject.getString(RestConstants.JSON_ADDRESS2_TAG);
            city = dataObject.getString(RestConstants.CITY);
            postcode = dataObject.optString(RestConstants.JSON_POSTCODE_TAG);
            phone = dataObject.getString(RestConstants.JSON_PHONE_TAG);
            region = dataObject.optString(RestConstants.REGION);
            additionalPhone = dataObject.optString(RestConstants.JSON_ADDITIONAL_PHONE_TAG);
        }catch(Exception e){
            Print.e("PARSING ERROR","Error in parsing data: "+e.getMessage());
            return false;
        }

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

    /**
     * @return the idCustomerAddress
     */
    public int getId() {
        return id;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address1;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the postcode
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param idCustomerAddress the idCustomerAddress to set
     */
    public void setId(int idCustomerAddress) {
        this.id = idCustomerAddress;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address1 = address;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
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

