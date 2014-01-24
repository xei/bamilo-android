package pt.rocket.framework.objects;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Class used to save the customer addresses
 * TODO: Save the fast line from JSON object 
 * @author sergiopereira
 *
 */
public class CustomerAddresses implements IJSONSerializable, Parcelable {

	private static final String TAG = CustomerAddresses.class.getSimpleName();

	private Address shippingAddress;
	
	private Address billingAddress;
	
	private Address fastline;
	
	private HashMap<String, Address> addresses = new HashMap<String, CustomerAddresses.Address>();


	/**
	 * Constructor
	 * @param jsonObject
	 * @throws JSONException 
	 */
	public CustomerAddresses(JSONObject jsonObject) throws JSONException{
		initialize(jsonObject);
	}
    
	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		Log.d(TAG, "INITIALIZE");
		// Get shipping address and save it
		shippingAddress = new Address(jsonObject.getJSONObject(RestConstants.JSON_SHIPPING_TAG));
		addresses.put("" + shippingAddress.getIdCustomerAddress(), shippingAddress);
		// Get billing address and save it
		billingAddress = new Address(jsonObject.getJSONObject(RestConstants.JSON_BILLING_TAG));
		addresses.put("" + billingAddress.getIdCustomerAddress(), billingAddress);
		// Get other addresses
		JSONObject jsonOther = jsonObject.getJSONObject(RestConstants.JSON_OTHER_TAG);
        Iterator<?> jsonIteretor = jsonOther.keys();
        while (jsonIteretor.hasNext()) {
            String key = (String) jsonIteretor.next();
            addresses.put(key, new Address((JSONObject) jsonOther.get(key)));
        }
		// TODO: Get fast line
		// fastline = new Address().initialize(jsonObject.getJSONObject(RestConstants.JSON_FASTLINE_TAG));
        return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		// TODO: Implement this method
		return null;
	}
    
    /**
	 * @return the shippingAddress
	 */
	public Address getShippingAddress() {
		return shippingAddress;
	}


	/**
	 * @return the billingAddress
	 */
	public Address getBillingAddress() {
		return billingAddress;
	}


	/**
	 * @return the fastline
	 */
	public Address getFastline() {
		return fastline;
	}


	/**
	 * @return the addresses
	 */
	public HashMap<String, Address> getAddresses() {
		return addresses;
	}


	/**
	 * @param shippingAddress the shippingAddress to set
	 */
	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}


	/**
	 * @param billingAddress the billingAddress to set
	 */
	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}


	/**
	 * @param fastline the fastline to set
	 */
	public void setFastline(Address fastline) {
		this.fastline = fastline;
	}


	/**
	 * @param addresses the addresses to set
	 */
	public void setAddresses(HashMap<String, Address> addresses) {
		this.addresses = addresses;
	}
	
	
	/**
	 * #################### PARCEL ####################  
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
		dest.writeParcelable(shippingAddress, flags);
		dest.writeParcelable(billingAddress, flags);
		dest.writeParcelable(fastline, flags);
		dest.writeMap(addresses);
	}
	
	/**
	 * Parcel constructor
	 * @param in
	 */
	private CustomerAddresses(Parcel in) {
		shippingAddress = in.readParcelable(Address.class.getClassLoader());
		billingAddress = in.readParcelable(Address.class.getClassLoader());
		fastline = in.readParcelable(Address.class.getClassLoader());
        in.readMap(addresses, null);
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<CustomerAddresses> CREATOR = new Parcelable.Creator<CustomerAddresses>() {
        public CustomerAddresses createFromParcel(Parcel in) {
            return new CustomerAddresses(in);
        }

        public CustomerAddresses[] newArray(int size) {
            return new CustomerAddresses[size];
        }
    };
	

	
	/**
	 * #################### ADDRESS ####################  
	 */

	/**
     * Class used to save a Address
     * @author sergiopereira
     *
     */
    public static class Address implements IJSONSerializable, Parcelable {
    	
    	private static final String TAG = Address.class.getSimpleName();
    	
        private int idCustomerAddress;
        private String firstName;
        private String lastName;
        private String address;
        private String city;
        private String postcode;
        private int phone;
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
        
        /**
         * Constructor
         * @param jsonObject
         */
        public Address(JSONObject jsonObject){
        	initialize(jsonObject);
        }
        
        /*
         * (non-Javadoc)
         * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
         */
		@Override
		public boolean initialize(JSONObject jsonObject) {
			Log.d(TAG, "INITIALIZE");
	        JSONObject dataObject = jsonObject;
			idCustomerAddress = dataObject.optInt(RestConstants.JSON_ADDRESS_ID_TAG);
			firstName = dataObject.optString(RestConstants.JSON_FIRST_NAME_TAG);
			lastName = dataObject.optString(RestConstants.JSON_LAST_NAME_TAG);
			address = dataObject.optString(RestConstants.JSON_ADDRESS1_TAG);
			city = dataObject.optString(RestConstants.JSON_CITY_TAG);
			postcode = dataObject.optString(RestConstants.JSON_POSTCODE_TAG);
			phone = dataObject.optInt(RestConstants.JSON_PHONE_TAG);
			fkCustomer = dataObject.optInt(RestConstants.JSON_CUSTOMER_ID_TAG);
			fkCountry = dataObject.optInt(RestConstants.JSON_COUNTRY_ID_TAG);
			fkCustomerAddressRegion = dataObject.optInt(RestConstants.JSON_REGION_ID_TAG);
			fkCustomerAddressCity = dataObject.optInt(RestConstants.JSON_CITY_ID_TAG);
			isDefaultBilling = dataObject.optBoolean(RestConstants.JSON_IS_DEFAULT_BILLING_TAG);
			isDefaultShipping = dataObject.optBoolean(RestConstants.JSON_IS_DEFAULT_SHIPPING_TAG);
			hidden = dataObject.optBoolean(RestConstants.JSON_HIDDEN_TAG);
			createdAt = dataObject.optString(RestConstants.JSON_CREATED_AT_TAG);
			updatedAt = dataObject.optString(RestConstants.JSON_UPDATED_AT_TAG);
			createdBy = dataObject.optInt(RestConstants.JSON_CREATED_BY_TAG);
			updatedBy = dataObject.optInt(RestConstants.JSON_UPDATED_BY_TAG);
	        return true;
		}

		/*
		 * (non-Javadoc)
		 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
		 */
		@Override
		public JSONObject toJSON() {
			// TODO: Implement this method
			return null;
		}

		/**
		 * @return the idCustomerAddress
		 */
		public int getIdCustomerAddress() {
			return idCustomerAddress;
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
			return address;
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
		public int getPhone() {
			return phone;
		}

		/**
		 * @return the fkCustomer
		 */
		public int getFkCustomer() {
			return fkCustomer;
		}

		/**
		 * @return the fkCountry
		 */
		public int getFkCountry() {
			return fkCountry;
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
		 * @return the hidden
		 */
		public boolean isHidden() {
			return hidden;
		}

		/**
		 * @return the createdAt
		 */
		public String getCreatedAt() {
			return createdAt;
		}

		/**
		 * @return the updatedAt
		 */
		public String getUpdatedAt() {
			return updatedAt;
		}

		/**
		 * @return the createdBy
		 */
		public int getCreatedBy() {
			return createdBy;
		}

		/**
		 * @return the updatedBy
		 */
		public int getUpdatedBy() {
			return updatedBy;
		}

		/**
		 * @param idCustomerAddress the idCustomerAddress to set
		 */
		public void setIdCustomerAddress(int idCustomerAddress) {
			this.idCustomerAddress = idCustomerAddress;
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
			this.address = address;
		}

		/**
		 * @param city the city to set
		 */
		public void setCity(String city) {
			this.city = city;
		}

		/**
		 * @param postcode the postcode to set
		 */
		public void setPostcode(String postcode) {
			this.postcode = postcode;
		}

		/**
		 * @param phone the phone to set
		 */
		public void setPhone(int phone) {
			this.phone = phone;
		}

		/**
		 * @param fkCustomer the fkCustomer to set
		 */
		public void setFkCustomer(int fkCustomer) {
			this.fkCustomer = fkCustomer;
		}

		/**
		 * @param fkCountry the fkCountry to set
		 */
		public void setFkCountry(int fkCountry) {
			this.fkCountry = fkCountry;
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
		 * @param isDefaultBilling the isDefaultBilling to set
		 */
		public void setDefaultBilling(boolean isDefaultBilling) {
			this.isDefaultBilling = isDefaultBilling;
		}

		/**
		 * @param isDefaultShipping the isDefaultShipping to set
		 */
		public void setDefaultShipping(boolean isDefaultShipping) {
			this.isDefaultShipping = isDefaultShipping;
		}

		/**
		 * @param hidden the hidden to set
		 */
		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}

		/**
		 * @param createdAt the createdAt to set
		 */
		public void setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
		}

		/**
		 * @param updatedAt the updatedAt to set
		 */
		public void setUpdatedAt(String updatedAt) {
			this.updatedAt = updatedAt;
		}

		/**
		 * @param createdBy the createdBy to set
		 */
		public void setCreatedBy(int createdBy) {
			this.createdBy = createdBy;
		}

		/**
		 * @param updatedBy the updatedBy to set
		 */
		public void setUpdatedBy(int updatedBy) {
			this.updatedBy = updatedBy;
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
			dest.writeInt(idCustomerAddress);
			dest.writeString(firstName);
			dest.writeString(lastName);
			dest.writeString(address);
			dest.writeString(city);
			dest.writeString(postcode);
			dest.writeInt(phone);
			dest.writeInt(fkCustomer);
			dest.writeInt(fkCountry);
			dest.writeInt(fkCustomerAddressRegion);
			dest.writeInt(fkCustomerAddressCity);
			dest.writeBooleanArray(new boolean[] {isDefaultBilling, isDefaultShipping, hidden});
			dest.writeString(createdAt);
			dest.writeString(updatedAt);
			dest.writeInt(createdBy);
			dest.writeInt(updatedBy);
		}
		
		
		/**
		 * Parcel constructor
		 * @param in
		 */
		private Address(Parcel in) {
			idCustomerAddress = in.readInt();
			firstName = in.readString();
			lastName = in.readString();
			address = in.readString();
			city = in.readString();
			postcode = in.readString();
			phone = in.readInt();
			fkCustomer = in.readInt();
			fkCountry = in.readInt();
			fkCustomerAddressRegion = in.readInt();
			fkCustomerAddressCity = in.readInt();
			in.readBooleanArray(new boolean[] {isDefaultBilling, isDefaultShipping, hidden});
			createdAt = in.readString();
			updatedAt = in.readString();
			createdBy = in.readInt();
			updatedBy = in.readInt();
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

}
