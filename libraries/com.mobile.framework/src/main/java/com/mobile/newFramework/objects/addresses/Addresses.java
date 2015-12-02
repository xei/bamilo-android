package com.mobile.newFramework.objects.addresses;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class used to save the customer addresses
 *
 * @author sergiopereira
 */
public class Addresses implements IJSONSerializable, Parcelable {

    public static final String TAG = Addresses.class.getSimpleName();

    private Address shippingAddress;

    private Address billingAddress;

    private HashMap<String, Address> addresses = new HashMap<>();


    /**
     * Constructor
     */
    @SuppressWarnings("unused")
    public Addresses() {
        super();
    }

    /**
     * Constructor
     *
     * @throws JSONException
     */
    public Addresses(JSONObject jsonObject) throws JSONException {
        initialize(jsonObject);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        Print.d("INITIALIZE");
        // Get shipping address and save it
        JSONObject jsonShip = jsonObject.optJSONObject(RestConstants.SHIPPING);
        if (jsonShip != null) {
            shippingAddress = new Address();
            shippingAddress.initialize(jsonShip);
        } else {
            throw new JSONException("");
        }
        // Get billing address and save it
        JSONObject jsonBil = jsonObject.optJSONObject(RestConstants.BILLING);
        if (jsonBil != null) {
            billingAddress = new Address();
            billingAddress.initialize(jsonBil);
        } else {
            throw new JSONException("");
        }
        // Get other addresses
        JSONArray jsonOthersArray = jsonObject.optJSONArray(RestConstants.OTHER);
        if (jsonOthersArray != null && jsonOthersArray.length() > 0) {
            JSONObject jsonOtherAddress;
            for (int i = 0; i < jsonOthersArray.length(); i++) {
                jsonOtherAddress = jsonOthersArray.optJSONObject(i);
                Address other = new Address();
                other.initialize(jsonOtherAddress);
                addresses.put("" + other.getId(), other);    //uses address id as key
            }
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
    public int getRequiredJson() {
        return RequiredJson.METADATA;
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
     * @return the addresses
     */
    public HashMap<String, Address> getAddresses() {
        return addresses;
    }

    /**
     * @param addresses the addresses to set
     */
    public void setAddresses(HashMap<String, Address> addresses) {
        this.addresses = addresses;
    }

    /**
     * Validate has shipping address
     *
     * @return true/false
     */
    public boolean hasShippingAddress() {
        return shippingAddress != null;
    }

    /**
     * Validate has billing address
     *
     * @return true/false
     */
    public boolean hasBillingAddress() {
        return billingAddress != null;
    }

    /**
     * Validate if the shipping and billing addresses are the same
     *
     * @return true/false
     */
    public boolean hasDefaultShippingAndBillingAddress() {
        Print.d("SHIPPING ID:" + shippingAddress.getId() + " BILLING ID:" + billingAddress.getId());
        return shippingAddress.getId() == billingAddress.getId();
    }

    /**
     * Method used to switch the defaults address for the current
     */
    public void switchShippingAddress(int position) {
        try {
            ArrayList<Address> array = new ArrayList<>(addresses.values());
            // Get selected address
            Address selectedAddress = array.get(position);
            // Add old shipping to map
            addresses.put("" + shippingAddress.getId(), shippingAddress);
            // Set the new shipping
            shippingAddress = selectedAddress;
            // Validate the old billing
            if (shippingAddress.getId() != billingAddress.getId()) {
                // Add old billing to map
                addresses.put("" + billingAddress.getId(), billingAddress);
                // Set the new
                billingAddress = selectedAddress;
            }
            // Remove from others
            addresses.remove("" + selectedAddress.getId());
        } catch (IndexOutOfBoundsException e) {
            Print.d("Exception on switch shipping address" + e);
        }
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
        dest.writeMap(addresses);
    }

    /**
     * Parcel constructor
     */
    private Addresses(Parcel in) {
        shippingAddress = in.readParcelable(Address.class.getClassLoader());
        billingAddress = in.readParcelable(Address.class.getClassLoader());
        addresses = new HashMap<>();
        in.readMap(addresses, Address.class.getClassLoader());
    }

    /**
     * Create parcelable
     */
    public static final Creator<Addresses> CREATOR = new Creator<Addresses>() {
        public Addresses createFromParcel(Parcel in) {
            return new Addresses(in);
        }

        public Addresses[] newArray(int size) {
            return new Addresses[size];
        }
    };

}
