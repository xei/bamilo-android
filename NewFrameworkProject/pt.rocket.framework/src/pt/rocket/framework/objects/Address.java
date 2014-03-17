/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import pt.rocket.framework.rest.RestConstants;

import java.util.HashMap;

/**
 * Class that represents the customer address. Use the method getaddressName to
 * get the name of the address
 * 
 * @author GuilhermeSilva
 * 
 */
public class Address implements IJSONSerializable, Parcelable {
    /**
     * List of string the represent the class properties in the Address Object
     * Model
     */
    public static final String OBJECT_MODEL_ID = "address_id";
    public static final String OBJECT_MODEL_FIRST_NAME = "AddressForm[first_name]";
    public static final String OBJECT_MODEL_MIDDLE_NAME = "AddressForm[middle_name]";
    public static final String OBJECT_MODEL_LAST_NAME = "AddressForm[last_name]";
    // public static final String OBJECT_MODEL_AGE = "age";
    public static final String OBJECT_MODEL_PHONE = "AddressForm[phone]";
    public static final String OBJECT_MODEL_STREET_ADDRESS1 = "AddressForm[address1]";
    public static final String OBJECT_MODEL_STREET_ADDRESS2 = "AddressForm[address2]";
    public static final String OBJECT_MODEL_POSTAL_CODE = "AddressForm[postcode]";
    public static final String OBJECT_MODEL_CITY = "AddressForm[city]";
    public static final String OBJECT_MODEL_REGION = "AddressForm[region]";
    public static final String OBJECT_MODEL_COMPANY = "AddressForm[company]";
    // public static final String OBJECT_MODEL_COUNTRY = "country";
    public static final String OBJECT_MODEL_IS_DEFAULT_BILLING = "AddressForm[is_default_billing]";
    public static final String OBJECT_MODEL_IS_DEFAULT_SHIPPING = "AddressForm[is_default_shipping]";

    private int addressId;

    private String company;

    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    // private int age;
    private String phone;

    private String streetAddress1;
    private String streetAddress2;
    private String postalCode;
    private String city;
    private String region;

    private boolean isDefaultBilling;
    private boolean isDefaultShipping;

    /**
     * Address empty constructor
     */
    public Address() {
        addressId = -1;

        firstName = "";
        lastName = "";
        // age = 0;
        phone = "";

        company = "";

        streetAddress1 = "";
        streetAddress2 = "";
        postalCode = "";
        city = "";
        // country = "";

        isDefaultBilling = false;
        isDefaultShipping = false;
    }

    /**
     * Address class constructor. Used only for tests. Should not be used on the
     * final application.
     * 
     * @param addressId
     *            Unique id of the address
     * @param firstName
     *            First name of the address responsible
     * @param lastName
     *            Last name of the address responsible
     * @param age
     *            Age of the address responsible
     * @param phone
     *            Phone associated to the address
     * @param streetAddress
     *            Name of the address street
     * @param postalCode
     *            Postal code of the address
     * @param city
     *            City of the address
     * @param country
     *            Country of the address
     * @param isDefaultBilling
     *            Sets if the address is the default address for purchase
     *            billing
     * @param isDefaultShipping
     *            Sets if the address is the default address for purchase
     *            shipping
     */
    public Address(int addressId, String firstName, String lastName, int age, String phone, String streetAddress, String postalCode, String city,
            String country, boolean isDefaultBilling, boolean isDefaultShipping) {
        this.addressId = addressId;
        this.firstName = firstName;
        this.lastName = lastName;
        // this.age = age;
        this.phone = phone;

        this.streetAddress1 = streetAddress;
        this.postalCode = postalCode;
        this.city = city;
        // this.country = country;

        company = "";

        this.isDefaultBilling = isDefaultBilling;
        this.isDefaultShipping = isDefaultShipping;
    }

    public String getId() {
        return id;
    }

    /**
     * @return the addressId
     */
    public int getAddressId() {
        return addressId;
    }

    /**
     * @param addressId
     *            the addressId to set
     */
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    /**
     * @return the firstName
     */
    public String getCompany() {
        return company;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the middleName
     */
    public String getMiddletName() {
        return middleName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    // /**
    // * @return the age
    // */
    // public int getAge() {
    // return age;
    // }

    /**
     * @return the streetAddress1
     */
    public String getStreetAddress1() {
        return streetAddress1;
    }

    /**
     * @return the streetAddress2
     */
    public String getStreetAddress2() {
        return streetAddress2;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @return the postalCode
     */
    public String getRegion() {
        return region;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    // /**
    // * @return the country
    // */
    // public String getCountry() {
    // return country;
    // }

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
     * @param isDefaultBilling
     *            the isDefaultBilling to set
     */
    public void setDefaultBilling(boolean isDefaultBilling) {
        this.isDefaultBilling = isDefaultBilling;
    }

    /**
     * @param isDefaultShipping
     *            the isDefaultShipping to set
     */
    public void setDefaultShipping(boolean isDefaultShipping) {
        this.isDefaultShipping = isDefaultShipping;
    }

    /**
     * Generates the object model of the address. The model is represented as a
     * dictionary where the key and the values are strings.
     * 
     * @return the object model map for this address
     */
    public HashMap<String, String> getObjectModel() {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(OBJECT_MODEL_ID, addressId + "");
        map.put(RestConstants.JSON_ADDRESS_ID_TAG, addressId + "");

        map.put(OBJECT_MODEL_COMPANY, company);

        map.put(OBJECT_MODEL_FIRST_NAME, firstName);
        map.put(OBJECT_MODEL_LAST_NAME, lastName);
        map.put(OBJECT_MODEL_MIDDLE_NAME, middleName);
        map.put(OBJECT_MODEL_PHONE, phone);

        map.put(OBJECT_MODEL_STREET_ADDRESS1, streetAddress1);
        map.put(OBJECT_MODEL_STREET_ADDRESS2, streetAddress2);
        map.put(OBJECT_MODEL_POSTAL_CODE, postalCode);
        map.put(OBJECT_MODEL_CITY, city);
        map.put(OBJECT_MODEL_REGION, region);

        map.put(OBJECT_MODEL_IS_DEFAULT_BILLING, isDefaultBilling ? "1" : "0");
        map.put(OBJECT_MODEL_IS_DEFAULT_SHIPPING, isDefaultShipping ? "1" : "0");

        return map;
    }

    /**
     * Initializes the address based on an object model
     * 
     * @param objectModel
     * @return true if the value was correctly updated, and false if the object
     *         model contains one or more errors. If return is false, then the
     *         the object is obsolete and must be discarded.
     */
    public boolean initialize(HashMap<String, String> objectModel) {
        try {

            company = objectModel.get(OBJECT_MODEL_COMPANY);
            firstName = objectModel.get(OBJECT_MODEL_FIRST_NAME);
            lastName = objectModel.get(OBJECT_MODEL_LAST_NAME);
            middleName = objectModel.get(OBJECT_MODEL_MIDDLE_NAME);
            // age = Integer.parseInt(objectModel.get(OBJECT_MODEL_AGE));
            phone = objectModel.get(OBJECT_MODEL_PHONE);

            streetAddress1 = objectModel.get(OBJECT_MODEL_STREET_ADDRESS1);
            streetAddress2 = objectModel.get(OBJECT_MODEL_STREET_ADDRESS2);
            postalCode = objectModel.get(OBJECT_MODEL_POSTAL_CODE);
            city = objectModel.get(OBJECT_MODEL_CITY);
            region = objectModel.get(OBJECT_MODEL_REGION);

            // String isDefaultBillingString =
            // objectModel.get(OBJECT_MODEL_IS_DEFAULT_BILLING);
            // isDefaultBilling = isDefaultBillingString.equals("true");
            //
            // String isDefaultShippingString =
            // objectModel.get(OBJECT_MODEL_IS_DEFAULT_SHIPPING);
            // isDefaultShipping = isDefaultShippingString.equals("true");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            JSONObject dataObject = jsonObject;
            if (jsonObject.has(RestConstants.JSON_DATA_TAG)) {
                dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            }

            company = dataObject.optString(RestConstants.JSON_COMPANY_TAG);

            id = dataObject.getString(RestConstants.JSON_ADDRESS_ID_TAG);
            addressId = Integer.parseInt(id);
            firstName = dataObject.getString(RestConstants.JSON_FIRST_NAME_TAG);
            middleName = dataObject.optString(RestConstants.JSON_MIDDLE_NAME_TAG);
            lastName = dataObject.getString(RestConstants.JSON_LAST_NAME_TAG);
            city = dataObject.getString(RestConstants.JSON_CITY_TAG);

            phone = dataObject.getString(RestConstants.JSON_PHONE_TAG);
            streetAddress1 = dataObject.getString(RestConstants.JSON_ADDRESS1_TAG);
            streetAddress2 = dataObject.optString(RestConstants.JSON_ADDRESS2_TAG);
            postalCode = dataObject.getString(RestConstants.JSON_POSTCODE_TAG);
            region = dataObject.optString(RestConstants.JSON_REGION_TAG);

            isDefaultBilling = dataObject.getString(RestConstants.JSON_IS_DEFAULT_BILLING_TAG).equals("1");
            isDefaultShipping = dataObject.getString(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG).equals("1");

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_COMPANY_TAG, company);
            jsonObject.put(RestConstants.JSON_ADDRESS_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_FIRST_NAME_TAG, firstName);
            jsonObject.put(RestConstants.JSON_LAST_NAME_TAG, lastName);
            jsonObject.put(RestConstants.JSON_MIDDLE_NAME_TAG, middleName);
            jsonObject.put(RestConstants.JSON_CITY_TAG, city);
            jsonObject.put(RestConstants.JSON_PHONE_TAG, phone);
            jsonObject.put(RestConstants.JSON_ADDRESS1_TAG, streetAddress1);
            jsonObject.put(RestConstants.JSON_ADDRESS2_TAG, streetAddress2);
            jsonObject.put(RestConstants.JSON_POSTCODE_TAG, postalCode);
            jsonObject.put(RestConstants.JSON_REGION_TAG, region);
            jsonObject.put(RestConstants.JSON_IS_DEFAULT_BILLING_TAG, isDefaultBilling?1:0);
            jsonObject.put(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG, isDefaultShipping?1:0);
            
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * ########### Parcelable ###########
     * @author sergiopereira
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
	    dest.writeInt(addressId);
	    dest.writeString(company);
	    dest.writeString(id);
	    dest.writeString(firstName);
	    dest.writeString(middleName);
	    dest.writeString(lastName);
	    dest.writeString(phone);
	    dest.writeString(streetAddress1);
	    dest.writeString(streetAddress2);
	    dest.writeString(postalCode);
	    dest.writeString(city);
	    dest.writeString(region);
	    dest.writeBooleanArray(new boolean[] {isDefaultBilling, isDefaultShipping});
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private Address(Parcel in) {
	    addressId = in.readInt();
	    company = in.readString();
	    id = in.readString();
	    firstName = in.readString();
	    middleName = in.readString();
	    lastName = in.readString();
	    phone = in.readString();
	    streetAddress1 = in.readString();
	    streetAddress2 = in.readString();
	    postalCode = in.readString();
	    city = in.readString();
	    region = in.readString();
	    in.readBooleanArray(new boolean[] {isDefaultBilling, isDefaultShipping});
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
