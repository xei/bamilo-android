package com.mobile.newFramework.objects.customer;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

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

    private static final java.lang.String TAG = Customer.class.getSimpleName();

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String birthday;
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
            // Entity
            jsonObject = jsonObject.getJSONObject(RestConstants.JSON_CUSTOMER_ENTITY_TAG);
            // Data
            id = jsonObject.optString(RestConstants.ID);
            firstName = jsonObject.getString(RestConstants.FIRST_NAME);
            lastName = jsonObject.getString(RestConstants.LAST_NAME);
            email = jsonObject.optString(RestConstants.EMAIL);
            gender = jsonObject.optString(RestConstants.JSON_GENDER_TAG);
            birthday = jsonObject.optString(RestConstants.JSON_BIRTHDAY_TAG);
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
        } catch (JSONException e) {
            Print.e(TAG, "ERROR: JSE ON PARSING CUSTOMER", e);
            return false;
        }
        return true;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
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

    /**
     * gets the customer last name
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
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
            jsonObject.put(RestConstants.JSON_GENDER_TAG, gender);
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
        in.readBooleanArray(new boolean[]{guest});
    }


    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<Customer> CREATOR = new Parcelable.Creator<Customer>() {
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

}
