/**
 * @author Guilherme Silva
 * @version 1.01
 * <p/>
 * 2012/06/18
 * <p/>
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;

import org.json.JSONObject;

import com.mobile.framework.output.Print;

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
    private int fkCustomer;
    private int fkCountry;
    private int fkCustomerAddressRegion;
    private int fkCustomerAddressCity;
    private boolean isDefaultBilling;
    private boolean isDefaultShipping;
    private boolean hidden;
    private String createdAt;
    private String updatedAt;
    private int createdBy;
    private int updatedBy;
    private String region;

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
        id = dataObject.optInt(RestConstants.JSON_ADDRESS_ID_TAG);
        if (dataObject.has(RestConstants.JSON_ADDRESS_ID_TAG_2))
            id = dataObject.optInt(RestConstants.JSON_ADDRESS_ID_TAG_2);

        firstName = dataObject.optString(RestConstants.JSON_FIRST_NAME_TAG);
        lastName = dataObject.optString(RestConstants.JSON_LAST_NAME_TAG);
        address1 = dataObject.optString(RestConstants.JSON_ADDRESS1_TAG);
        address2 = dataObject.optString(RestConstants.JSON_ADDRESS2_TAG);
        city = dataObject.optString(RestConstants.JSON_CITY_TAG);
        postcode = dataObject.optString(RestConstants.JSON_POSTCODE_TAG);
        phone = dataObject.optString(RestConstants.JSON_PHONE_TAG);
        additionalPhone = dataObject.optString(RestConstants.JSON_ADDITIONAL_PHONE_TAG);
        fkCustomer = dataObject.optInt(RestConstants.JSON_CUSTOMER_ID_TAG);
        fkCountry = dataObject.optInt(RestConstants.JSON_COUNTRY_ID_TAG);
        fkCustomerAddressRegion = dataObject.optInt(RestConstants.JSON_REGION_ID_TAG);
        fkCustomerAddressCity = dataObject.optInt(RestConstants.JSON_CITY_ID_TAG);
        isDefaultBilling = "1".equals(dataObject.optString(RestConstants.JSON_IS_DEFAULT_BILLING_TAG));
        isDefaultShipping = "1".equals(dataObject.optString(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG));
        hidden = dataObject.optBoolean(RestConstants.JSON_HIDDEN_TAG);
        createdAt = dataObject.optString(RestConstants.JSON_CREATED_AT_TAG);
        updatedAt = dataObject.optString(RestConstants.JSON_UPDATED_AT_TAG);
        createdBy = dataObject.optInt(RestConstants.JSON_CREATED_BY_TAG);
        updatedBy = dataObject.optInt(RestConstants.JSON_UPDATED_BY_TAG);
        region = dataObject.optString(RestConstants.JSON_REGION_NAME_TAG);
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
     * @return the address 2
     */
    public String getAddress2() {
        return address2;
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

    public String getAdditionalPhone() {
        return additionalPhone;
    }

    /**
     * @return the fkCustomerAddressRegion
     */
    public int getFkCustomerAddressRegion() {
        return fkCustomerAddressRegion;
    }

    /**
     * @return the fkCustomerAddressCity
     */
    public int getFkCustomerAddressCity() {
        return fkCustomerAddressCity;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @return the isDefaultBilling
     */
    public boolean isDefaultBilling() {
        return isDefaultBilling;
    }

    /**
     * @return the isDefaultShipping
     */
    public boolean isDefaultShipping() {
        return isDefaultShipping;
    }

    /**
     * @param idCustomerAddress the idCustomerAddress to set
     */
    public void setId(int idCustomerAddress) {
        this.id = idCustomerAddress;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address1 = address;
    }

    /**
     * @param address the address 2 to set
     */
    public void setAddress2(String address) {
        this.address2 = address;
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
     * @param fkCustomerAddressRegion the fkCustomerAddressRegion to set
     */
    public void setFkCustomerAddressRegion(int fkCustomerAddressRegion) {
        this.fkCustomerAddressRegion = fkCustomerAddressRegion;
    }

    /**
     * @param fkCustomerAddressCity the fkCustomerAddressCity to set
     */
    public void setFkCustomerAddressCity(int fkCustomerAddressCity) {
        this.fkCustomerAddressCity = fkCustomerAddressCity;
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
        dest.writeInt(fkCustomer);
        dest.writeInt(fkCountry);
        dest.writeInt(fkCustomerAddressRegion);
        dest.writeInt(fkCustomerAddressCity);
        dest.writeBooleanArray(new boolean[]{isDefaultBilling, isDefaultShipping, hidden});
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeInt(createdBy);
        dest.writeInt(updatedBy);
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
        fkCustomer = in.readInt();
        fkCountry = in.readInt();
        fkCustomerAddressRegion = in.readInt();
        fkCustomerAddressCity = in.readInt();
        in.readBooleanArray(new boolean[]{isDefaultBilling, isDefaultShipping, hidden});
        createdAt = in.readString();
        updatedAt = in.readString();
        createdBy = in.readInt();
        updatedBy = in.readInt();
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

