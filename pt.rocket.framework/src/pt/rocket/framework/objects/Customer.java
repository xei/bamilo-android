package pt.rocket.framework.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * Class that represents a Customer. Alice_Model_RatingForm[title]=Teste

 * @author GuilhermeSilva
 *
 */
public class Customer implements IJSONSerializable, Parcelable{
	
	private final static String TAG = LogTagHelper.create( Customer.class );
	
	//@SuppressLint("SimpleDateFormat")
	//private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
    
    private String id;
    private CustomerPrefix prefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private Date birthday;
    private CustomerGender gender;
    private String password;
    private String created_at;
    private boolean guest;
    
	private ArrayList<String> addresses;

	private ArrayList<CustomerNewsletterSubscription> mNewsletterSubscriptions;
    
    
    /**
     * Customer empty constructor
     */
    public Customer() {
        id = "-1";
        firstName = "";
        middleName = "";
        lastName = "";
        email = "";
        birthday = new Date();
        gender = CustomerGender.Gender;
        password = "";
        setCreatedAt("");
        this.addresses = null;
        guest = false;
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
            CustomerPrefix customerPrefix, String createdAt, ArrayList<String> addresses) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthDay;
        this.gender = gender;
        this.password = password;
        this.prefix = customerPrefix;
        this.created_at = createdAt;
        this.addresses = addresses;
        this.guest = false;
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

    /**
     * 
     * @return The User Account creation date.
     */
	public String getCreatedAt() {
		return created_at;
	}
	
	/**
	 * 
	 * @param created_at - The User Account creation date.
	 */
	public void setCreatedAt(String created_at) {
		this.created_at = created_at;
	}
	
    /**
	 * @return the addresses
	 */
	public ArrayList<String> getAddresses() {
		return addresses;
	}

	/**
	 * @param addresses the addresses to set
	 */
	public void setAddresses(ArrayList<String> addresses) {
		this.addresses = addresses;
	}
	
	/**
	 * Method that validate if user has addresses
	 * @return true/false
	 */
	public boolean hasAddresses(){
		return (this.addresses != null && this.addresses.size() > 0 ) ? true : false;
	}
	
    /**
     * Get newsletter subscriptions
     * @return a list of {@link CustomerNewsletterSubscription}
     * @author sergiopereira
     */
    public ArrayList<CustomerNewsletterSubscription> getNewsletterSubscriptions(){
    	return mNewsletterSubscriptions;
    }
    
    /**
     * Save newsletter subscriptions
     * @param a list of {@link CustomerNewsletterSubscription}
     * @author sergiopereira
     */
    public void setNewsletterSubscriptions(ArrayList<CustomerNewsletterSubscription> subscriptions){
    	this.mNewsletterSubscriptions = subscriptions;
    }
    
    /**
     * Validate if exist newsletter subscription
     * @return boolean
     * @author sergiopereira
     */
    public boolean hasNewsletterSubscriptions(){
    	return (mNewsletterSubscriptions != null && mNewsletterSubscriptions.size() > 0) ? true : false;
    }

	/* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(RestConstants.JSON_ID_CUSTOMER_TAG);
            Log.i("CUSTOMER ID"," => "+id);
            firstName = jsonObject.getString(RestConstants.JSON_FIRST_NAME_TAG);
            lastName = jsonObject.getString(RestConstants.JSON_LAST_NAME_TAG);
            email = jsonObject.getString(RestConstants.JSON_EMAIL_TAG);
            created_at = jsonObject.getString(RestConstants.JSON_CREATED_AT_TAG);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());  
            try {  
            	birthday = format.parse(jsonObject.getString(RestConstants.JSON_BIRTHDAY_TAG));  
            } catch (ParseException e) {  
                e.printStackTrace();  
            }
 
            String genderString = jsonObject.optString(RestConstants.JSON_GENDER_TAG);
            if(genderString == null) {
            	gender = CustomerGender.UNKNOWN;
            } else if (genderString.equals("male")) {
                gender = CustomerGender.Male;
            } else if(genderString.equals("female")) {
                gender = CustomerGender.Female;
            } else {
                gender = CustomerGender.Gender;
            }
            
            // Save addresses :> "address_collection": { "4040": {}, "8241": {} }
            JSONObject addressesJson = jsonObject.optJSONObject(RestConstants.JSON_CUSTOMER_ADDRESS_COLLECTION_TAG);
            if(addressesJson != null && addressesJson.length() > 0 ){
            	addresses = new ArrayList<String>();
            	Iterator<?> iterator = addressesJson.keys();
            	while (iterator.hasNext()) {
					String key = (String) iterator.next();
					addresses.add(key);
				}
            }
            
            mNewsletterSubscriptions = new ArrayList<CustomerNewsletterSubscription>();
            
            // FIXME: Newsletter subscription NAFAMZ-6146
            // FIXME: NAFAMZ-6518
            JSONArray newsletterArray = jsonObject.optJSONArray("customer_newsletter_subscription");
            if(newsletterArray != null) {
            	for (int i = 0; i < newsletterArray.length(); i++) {
            		JSONObject object = newsletterArray.optJSONObject(i);
            		if(object != null) {
            			CustomerNewsletterSubscription newsletter = new CustomerNewsletterSubscription();
            			newsletter.initialize(object);
                		mNewsletterSubscriptions.add(newsletter);
            		}
				}
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
            jsonObject.put(RestConstants.JSON_ID_CUSTOMER_TAG, id);
            jsonObject.put(RestConstants.JSON_FIRST_NAME_TAG, firstName);
            jsonObject.put(RestConstants.JSON_LAST_NAME_TAG, lastName);
            jsonObject.put(RestConstants.JSON_EMAIL_TAG, email);
            
            jsonObject.put(RestConstants.JSON_GENDER_TAG, gender==CustomerGender.Male?"male":"female");
            jsonObject.put(RestConstants.JSON_BIRTHDAY_TAG, birthday.toString());
            
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
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
	    dest.writeString(id);
	    dest.writeString(firstName);
	    dest.writeString(middleName);
	    dest.writeString(lastName);
	    dest.writeString(email);
	    dest.writeLong(birthday.getTime());
	    dest.writeValue(gender);
	    dest.writeString(password);
	    dest.writeValue(prefix);
	    dest.writeString(created_at);
	    dest.writeBooleanArray(new boolean[]{guest});
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private Customer(Parcel in) {
        this.id = in.readString();
        this.firstName = in.readString();
        this.middleName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.birthday = new Date(in.readLong());
        this.gender = (CustomerGender) in.readValue(CustomerGender.class.getClassLoader());
        this.password = in.readString();
        this.prefix = (CustomerPrefix) in.readValue(CustomerPrefix.class.getClassLoader());
        this.created_at = in.readString();
        in.readBooleanArray(new boolean[]{guest});
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
