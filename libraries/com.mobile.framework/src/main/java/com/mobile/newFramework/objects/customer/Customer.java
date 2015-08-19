package com.mobile.newFramework.objects.customer;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents a Customer. Alice_Model_RatingForm[title]=Teste
 *
 * @author GuilhermeSilva
 */
public class Customer implements IJSONSerializable, Parcelable {

    private static final java.lang.String TAG = Customer.class.getSimpleName();

    private String id;
    private CustomerPrefix prefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private CustomerGender gender;
    private String password;
    private String createdAt;
    private boolean guest;
    private String mBirthday;

    /**
     * Customer empty constructor
     */
    public Customer() {
        id = "-1";
        firstName = "";
        middleName = "";
        lastName = "";
        email = "";
        gender = CustomerGender.Gender;
        password = "";
        setCreatedAt("");
        guest = false;
        mBirthday = "";
    }

    public Customer(JSONObject jsonObject) {
        initialize(jsonObject);
    }

    /**
     * @param id             of the customer.
     * @param firstName      of the customer
     * @param middleName     of the customer.
     * @param lastName       of the customer.
     * @param email          of the customer.
     * @param password       of the customer.
     * @param gender         of the customer.
     * @param birthday       of the customer.
     * @param customerPrefix of the customer.
     */
    public Customer(String id, String firstName, String middleName, String lastName, String email, String password, CustomerGender gender,
                    CustomerPrefix customerPrefix, String createdAt, ArrayList<String> addresses, String birthday) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.password = password;
        this.prefix = customerPrefix;
        this.createdAt = createdAt;
        this.guest = false;
        this.mBirthday = birthday;
    }

    /**
     * Get user birthday.
     *
     * @return String or null
     * @author sergiopereira
     */
    public String getBirthday() {
        return mBirthday;
    }

//    /**
//     * Set user birthday.
//     *
//     * @param birthday
//     * @author sergiopereira
//     */
//    public void setBirthday(String birthday) {
//        this.mBirthday = birthday;
//    }

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

//    /**
//     * gets the customer middle name
//     *
//     * @return the middle name
//     */
//    public String getMiddleName() {
//        return middleName;
//    }

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
    public CustomerGender getGender() {
        return gender;
    }

    /**
     * gets the customer password
     *
     * @return the customer password
     */
    public String getPassword() {
        return password;
    }


//    /**
//     * @param firstName the firstName to set
//     */
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    /**
//     * @param middleName the middleName to set
//     */
//    public void setMiddleName(String middleName) {
//        this.middleName = middleName;
//    }
//
//    /**
//     * @param lastName the lastName to set
//     */
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(CustomerGender gender) {
        this.gender = gender;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

//    /**
//     * @return the prefix
//     */
//    public CustomerPrefix getPrefix() {
//        return prefix;
//    }
//
//    /**
//     * @param prefix the prefix to set
//     */
//    public void setPrefix(CustomerPrefix prefix) {
//        this.prefix = prefix;
//    }

    /**
     * @return The User Account creation date.
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param created_at - The User Account creation date.
     */
    public void setCreatedAt(String created_at) {
        this.createdAt = created_at;
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

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {

            if (jsonObject.has(RestConstants.JSON_CUSTOMER_ENTITY)) {
                JSONObject jsonObjCustomerEnt = jsonObject.getJSONObject(RestConstants.JSON_CUSTOMER_ENTITY);
                id = jsonObjCustomerEnt.getString(RestConstants.ID);
                firstName = jsonObjCustomerEnt.getString(RestConstants.JSON_FIRST_NAME_TAG);
                lastName = jsonObjCustomerEnt.getString(RestConstants.JSON_LAST_NAME_TAG);
                email = jsonObjCustomerEnt.getString(RestConstants.JSON_EMAIL_TAG);
                mBirthday = jsonObjCustomerEnt.optString(RestConstants.JSON_BIRTHDAY_TAG, "");

                String genderString = jsonObject.optString(RestConstants.JSON_GENDER_TAG);
                if (genderString == null) {
                    gender = CustomerGender.UNKNOWN;
                } else if (genderString.equals("male")) {
                    gender = CustomerGender.Male;
                } else if (genderString.equals("female")) {
                    gender = CustomerGender.Female;
                } else {
                    gender = CustomerGender.Gender;
                }

            }

        } catch (JSONException e) {
            Print.e(TAG, "Error parsing the jsonobject to customer", e);
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_ID_CUSTOMER_TAG, id);
            jsonObject.put(RestConstants.JSON_FIRST_NAME_TAG, firstName);
            jsonObject.put(RestConstants.JSON_LAST_NAME_TAG, lastName);
            jsonObject.put(RestConstants.JSON_EMAIL_TAG, email);
            jsonObject.put(RestConstants.JSON_GENDER_TAG, gender == CustomerGender.Male ? "male" : "female");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
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
        dest.writeString(middleName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeValue(gender);
        dest.writeString(password);
        dest.writeValue(prefix);
        dest.writeString(createdAt);
        dest.writeBooleanArray(new boolean[]{guest});
        dest.writeString(mBirthday);
    }

    /**
     * Parcel constructor
     */
    private Customer(Parcel in) {
        this.id = in.readString();
        this.firstName = in.readString();
        this.middleName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.gender = (CustomerGender) in.readValue(CustomerGender.class.getClassLoader());
        this.password = in.readString();
        this.prefix = (CustomerPrefix) in.readValue(CustomerPrefix.class.getClassLoader());
        this.createdAt = in.readString();
        in.readBooleanArray(new boolean[]{guest});
        mBirthday = in.readString();
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
