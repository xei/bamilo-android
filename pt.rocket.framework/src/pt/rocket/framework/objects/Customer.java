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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.utils.LogTagHelper;
import android.annotation.SuppressLint;
import de.akquinet.android.androlog.Log;

/**
 * Class that represents a Customer. Alice_Model_RatingForm[title]=Teste

 * @author GuilhermeSilva
 *
 */
public class Customer implements IJSONSerializable {
	private final static String TAG = LogTagHelper.create( Customer.class );
	
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
	
    private final String JSON_ID_TAG = "id_customer";
    private final String JSON_FIRST_NAME_TAG = "first_name";
    private final String JSON_LAST_NAME_TAG = "last_name";
    private final String JSON_EMAIL_TAG = "email";
    private final String JSON_BIRTHDAY_TAG = "birthday";
    private final String JSON_GENDER_TAG = "gender";
    
    private String id;
    private CustomerPrefix prefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private Date birthday;
    private CustomerGender gender;
    private String password;

    /**
     * Customer empty constructor
     */
    private Customer() {
        id = "-1";
        firstName = "";
        middleName = "";
        lastName = "";
        email = "";
        birthday = new Date();
        gender = CustomerGender.Gender;
        password = "";
    }
    
    public Customer(JSONObject jsonObject) {
    	initialize(jsonObject);
    }

    /**
     * @param id of the customer.
     * @param firstName of the customer
     * @param middleName of the customer.
     * @param lastName of the customer.
     * @param email of the customer.
     * @param password of the customer.
     * @param gender of the customer.
     * @param birthDay of the customer.
     * @param customerPrefix of the customer.
     */
    public Customer(String id, String firstName, String middleName, String lastName, String email, String password, CustomerGender gender, Date birthDay,
            CustomerPrefix customerPrefix) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthDay;
        this.gender = gender;
        this.password = password;
        this.prefix = customerPrefix;
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
     * gets the customer middle name
     * 
     * @return the middle name
     */
    public String getMiddleName() {
        return middleName;
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

    /**
     * return's the customer birthday
     * 
     * @return the birthday
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * @param birthday
     *            the birthday to set
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param middleName
     *            the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param gender
     *            the gender to set
     */
    public void setGender(CustomerGender gender) {
        this.gender = gender;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the prefix
     */
    public CustomerPrefix getPrefix() {
        return prefix;
    }

    /**
     * @param prefix
     *            the prefix to set
     */
    public void setPrefix(CustomerPrefix prefix) {
        this.prefix = prefix;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(JSON_ID_TAG);
            Log.i("CUSTOMER ID"," => "+id);
            firstName = jsonObject.getString(JSON_FIRST_NAME_TAG);
            lastName = jsonObject.getString(JSON_LAST_NAME_TAG);
            email = jsonObject.getString(JSON_EMAIL_TAG);
            
            String genderString = jsonObject.optString(JSON_GENDER_TAG);
            if(genderString == null) {
            	gender = CustomerGender.UNKNOWN;
            } else if (genderString.equals("male")) {
                gender = CustomerGender.Male;
            } else if(genderString.equals("female")) {
                gender = CustomerGender.Female;
            } else {
                gender = CustomerGender.Gender;
            }
            
        } catch (JSONException e) {
        	Log.e( TAG, "Error parsing the jsonobject to customer", e );
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
            jsonObject.put(JSON_ID_TAG, id);
            jsonObject.put(JSON_FIRST_NAME_TAG, firstName);
            jsonObject.put(JSON_LAST_NAME_TAG, lastName);
            jsonObject.put(JSON_EMAIL_TAG, email);
            
            jsonObject.put(JSON_GENDER_TAG, gender==CustomerGender.Male?"male":"female");
            jsonObject.put(JSON_BIRTHDAY_TAG, birthday.toString());
            
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }
}
