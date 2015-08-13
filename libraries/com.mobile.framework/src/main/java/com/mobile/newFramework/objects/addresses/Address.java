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
    //alexandrapires: not come in mobapi 1.8
/*    private int fkCustomer;
    private int fkCountry;
    private int fkCustomerAddressRegion;
    private int fkCustomerAddressCity;*/
    private boolean isDefaultBilling;
    private boolean isDefaultShipping;
    //not in mobapi 1.8
/*    private boolean hidden;
    private String createdAt;
    private String updatedAt;
    private int createdBy;
    private int updatedBy;*/
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

        //alexandrapires:  mobapi 1.8 change
        try {
            id = dataObject.getInt(RestConstants.JSON_ID_TAG); //id instead of id_customer_address":
            firstName = dataObject.getString(RestConstants.JSON_FIRST_NAME_TAG);
            lastName = dataObject.getString(RestConstants.JSON_LAST_NAME_TAG);
            address1 = dataObject.getString(RestConstants.JSON_ADDRESS1_TAG);
            address2 = dataObject.getString(RestConstants.JSON_ADDRESS2_TAG);
            city = dataObject.getString(RestConstants.JSON_CITY_TAG);   //city instead of fk_customer_address_city
            postcode = dataObject.optString(RestConstants.JSON_POSTCODE_TAG);
            phone = dataObject.getString(RestConstants.JSON_PHONE_TAG);
            isDefaultBilling = dataObject.optBoolean(RestConstants.JSON_IS_DEFAULT_BILLING_TAG);
            isDefaultShipping = dataObject.optBoolean(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG);
            region = dataObject.optString(RestConstants.JSON_REGION);   //region instead of id_customer_address_region
            additionalPhone = dataObject.optString(RestConstants.JSON_ADDITIONAL_PHONE_TAG);

            //alexandrapires: not in mobapi 1.8
    /*
        /*   id = dataObject.optInt(RestConstants.JSON_ADDRESS_ID_TAG);
        if (dataObject.has(RestConstants.JSON_ADDRESS_ID_TAG_2))
            id = dataObject.optInt(RestConstants.JSON_ADDRESS_ID_TAG_2);

         middleName = dataObject.optString(RestConstants.JSON_MIDDLE_NAME_TAG);
        region = dataObject.optString(RestConstants.JSON_REGION_NAME_TAG);
        additionalPhone = dataObject.optString(RestConstants.JSON_ADDITIONAL_PHONE_TAG);
        fkCustomer = dataObject.optInt(RestConstants.JSON_CUSTOMER_ID_TAG);
        fkCountry = dataObject.optInt(RestConstants.JSON_COUNTRY_ID_TAG);
        fkCustomerAddressRegion = dataObject.optInt(RestConstants.JSON_REGION_ID_TAG);
        fkCustomerAddressCity = dataObject.optInt(RestConstants.JSON_CITY_ID_TAG);
          hidden = dataObject.optBoolean(RestConstants.JSON_HIDDEN_TAG);
        createdAt = dataObject.optString(RestConstants.JSON_CREATED_AT_TAG);
        updatedAt = dataObject.optString(RestConstants.JSON_UPDATED_AT_TAG);
        createdBy = dataObject.optInt(RestConstants.JSON_CREATED_BY_TAG);
        updatedBy = dataObject.optInt(RestConstants.JSON_UPDATED_BY_TAG);

        */
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
 /*   public int getFkCustomerAddressRegion() { //not in mobapi 1.8
        return fkCustomerAddressRegion;
    }*/

    /**
     * @return the fkCustomerAddressCity
     */
 /*   public int getFkCustomerAddressCity() {
        return fkCustomerAddressCity;
    }*/

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
    /*
    public void setFkCustomerAddressRegion(int fkCustomerAddressRegion) {   //nor in mobapi 1.8
        this.fkCustomerAddressRegion = fkCustomerAddressRegion;
    }*/

    /**
     * @param fkCustomerAddressCity the fkCustomerAddressCity to set
     */
 /*   public void setFkCustomerAddressCity(int fkCustomerAddressCity) {
        this.fkCustomerAddressCity = fkCustomerAddressCity;
    }*/

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
        //not in mobapi 1.8
  /*      dest.writeInt(fkCustomer);
        dest.writeInt(fkCountry);
        dest.writeInt(fkCustomerAddressRegion);
        dest.writeInt(fkCustomerAddressCity);
        dest.writeBooleanArray(new boolean[]{isDefaultBilling, isDefaultShipping, hidden});
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeInt(createdBy);
        dest.writeInt(updatedBy);*/
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
        //not in mobapi 1.8
/*        fkCustomer = in.readInt();
        fkCountry = in.readInt();
        fkCustomerAddressRegion = in.readInt();
        fkCustomerAddressCity = in.readInt();
        in.readBooleanArray(new boolean[]{isDefaultBilling, isDefaultShipping, hidden});
        createdAt = in.readString();
        updatedAt = in.readString();
        createdBy = in.readInt();
        updatedBy = in.readInt();*/
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

