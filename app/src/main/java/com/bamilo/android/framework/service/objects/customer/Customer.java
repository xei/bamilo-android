package com.bamilo.android.framework.service.objects.customer;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

/**
 * Class that represents a Customer. Alice_Model_RatingForm[title]=Teste
 *
 * @author GuilhermeSilva
 */
public class Customer implements IJSONSerializable, Parcelable {

    private static final String TAG = Customer.class.getSimpleName();

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String birthday;
    private String nationalId;
    private String phoneNumber;
    private boolean guest;
    private HashSet<String> mWishListCache;

    /**
     * Customer empty constructor
     */
    public Customer() {
        // ...
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            if (jsonObject != null) {
                if (jsonObject.has(RestConstants.METADATA)) {
                    jsonObject = jsonObject.getJSONObject(RestConstants.METADATA);
                }
                // Entity
                jsonObject = jsonObject.getJSONObject(RestConstants.CUSTOMER_ENTITY);
                // Data
                id = jsonObject.getString(RestConstants.ID);
                firstName = jsonObject.optString(RestConstants.FIRST_NAME);
                lastName = jsonObject.optString(RestConstants.LAST_NAME);
                email = jsonObject.getString(RestConstants.EMAIL);
                gender = jsonObject.optString(RestConstants.GENDER);
                birthday = jsonObject.optString(RestConstants.BIRTHDAY);
                nationalId = jsonObject.optString(RestConstants.NATIONAL_ID);
                phoneNumber = jsonObject.optString(RestConstants.PHONE);
                // Get wish list products
                JSONArray jsonArray = jsonObject.optJSONArray(RestConstants.WISH_LIST_PRODUCTS);
                if (jsonArray != null && jsonArray.length() > 0) {
                    mWishListCache = new HashSet<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.optJSONObject(i);
                        String sku = item.optString(RestConstants.SKU);
                        if (TextUtils.isNotEmpty(sku)) {
                            mWishListCache.add(sku);
                        }
                    }
                }
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.COMPLETE_JSON;
    }

    /**
     * gets the customer's id
     *
     * @return the customer id
     */
    public int getId() {
        return Integer.parseInt(id);
    }

    /**
     * gets the customer's id as String
     *
     * @return the customer id
     */

    public String getIdAsString() {
        return id;
    }

    /**
     * gets the customer first name
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * gets the customer last name
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * gets the customer email
     *
     * @return the customer email
     */
    public String getEmail() {
        return email;
    }

    /**
     * gets the customer gender
     *
     * @return the customer gender
     */
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    /**
     * @return the guest
     */
    public boolean isGuest() {
        return guest;
    }

    /**
     * @param guest the guest to set
     */
    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    /**
     * Wish list for cache.
     */
    public HashSet<String> getWishListCache() {
        return mWishListCache;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.ID, id);
            jsonObject.put(RestConstants.FIRST_NAME, firstName);
            jsonObject.put(RestConstants.LAST_NAME, lastName);
            jsonObject.put(RestConstants.EMAIL, email);
            jsonObject.put(RestConstants.GENDER, gender);
            jsonObject.put(RestConstants.NATIONAL_ID, nationalId);
            jsonObject.put(RestConstants.PHONE, phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    /**
     * ########### Parcelable ###########
     *
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
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(gender);
        dest.writeString(birthday);
        dest.writeString(nationalId);
        dest.writeString(phoneNumber);
        dest.writeBooleanArray(new boolean[]{guest});
    }

    /**
     * Parcel constructor
     */
    private Customer(Parcel in) {
        this.id = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.gender = in.readString();
        this.birthday = in.readString();
        this.nationalId = in.readString();
        this.phoneNumber = in.readString();
        in.readBooleanArray(new boolean[]{guest});
    }


    /**
     * Create parcelable
     */
    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}
