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
public class Addresses implements IJSONSerializable, Parcelable {

	private static final String TAG = Addresses.class.getSimpleName();

	private Address shippingAddress;
	
	private Address billingAddress;
	
	private Address fastline;
	
	private HashMap<String, Address> addresses = new HashMap<String, Address>();


	/**
	 * Constructor
	 * @param jsonObject
	 * @throws JSONException 
	 */
	public Addresses(JSONObject jsonObject) throws JSONException{
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
		JSONObject jsonShip = jsonObject.optJSONObject(RestConstants.JSON_SHIPPING_TAG);
		if(jsonShip != null) shippingAddress = new Address(jsonShip);
		//addresses.put("" + shippingAddress.getIdCustomerAddress(), shippingAddress);
		// Get billing address and save it
		JSONObject jsonBil = jsonObject.optJSONObject(RestConstants.JSON_BILLING_TAG);
		if(jsonBil != null) billingAddress = new Address(jsonBil);
		//addresses.put("" + billingAddress.getIdCustomerAddress(), billingAddress);
		// Get other addresses
		JSONObject jsonOther = jsonObject.optJSONObject(RestConstants.JSON_OTHER_TAG);
		if(jsonOther != null && jsonOther.length() > 0) {
	        Iterator<?> jsonIteretor = jsonOther.keys();
	        while (jsonIteretor.hasNext()) {
	            String key = (String) jsonIteretor.next();
	            addresses.put(key, new Address((JSONObject) jsonOther.get(key)));
	        }
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
	
	public boolean hasShippingAddress(){
		return (shippingAddress != null) ? true : false;
	}
	
	public boolean hasBillingAddress(){
		return (billingAddress != null) ? true : false;
	}
	
	public boolean hasOtherAddresses(){
		return (addresses != null && addresses.size() > 0) ? true : false;
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
	private Addresses(Parcel in) {
		shippingAddress = in.readParcelable(Address.class.getClassLoader());
		billingAddress = in.readParcelable(Address.class.getClassLoader());
		fastline = in.readParcelable(Address.class.getClassLoader());
        in.readMap(addresses, null);
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<Addresses> CREATOR = new Parcelable.Creator<Addresses>() {
        public Addresses createFromParcel(Parcel in) {
            return new Addresses(in);
        }

        public Addresses[] newArray(int size) {
            return new Addresses[size];
        }
    };

}
