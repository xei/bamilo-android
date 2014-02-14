/**
 * @author Manuel Silva
 * 
 * @version 1.1
 * 
 * 2013/10/22
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Class that represents an Order Tracked
 * 
 * @author manuelsilva
 * 
 */
public class OrderSummary implements IJSONSerializable, Parcelable {

	public final static String TAG = LogTagHelper.create(OrderSummary.class);

	private String mSubTotal;

	private String mShippingFee;

	private String mTotal;

	private ArrayList<String> mProducts;

	private Address mShippingAddress;

	private Address mBillingAddress;

	private ShippingMethods mShippingMethod;

	private PaymentMethods mPaymentOptions;

	private String mOrderNumber;

	private String mFirstName;

	private String mLastName;

	/**
	 * OrderTracker empty constructor.
	 */
	public OrderSummary() {
	}
	
	/**
	 * OrderTracker empty constructor.
	 * @throws JSONException 
	 */
	public OrderSummary(JSONObject jsonObject) throws JSONException {
		initialize(jsonObject);
	}

	/**
	 * @return the mSubTotal
	 */
	public String getSubTotal() {
		return mSubTotal;
	}

	/**
	 * @return the mShippingFee
	 */
	public String getShippingFee() {
		return mShippingFee;
	}

	/**
	 * @return the mTotal
	 */
	public String getTotal() {
		return mTotal;
	}

	/**
	 * @return the mProducts
	 */
	public ArrayList<String> getProducts() {
		return mProducts;
	}

	/**
	 * @return the mShippingAddress
	 */
	public Address getShippingAddress() {
		return mShippingAddress;
	}

	/**
	 * @return the mBillingAddress
	 */
	public Address getBillingAddress() {
		return mBillingAddress;
	}

	/**
	 * @return the mShippingMethod
	 */
	public ShippingMethods getShippingMethod() {
		return mShippingMethod;
	}

	/**
	 * @return the mPaymentOptions
	 */
	public PaymentMethods getPaymentOptions() {
		return mPaymentOptions;
	}

	/**
	 * @param mSubTotal
	 *            the mSubTotal to set
	 */
	public void setSubTotal(String mSubTotal) {
		this.mSubTotal = mSubTotal;
	}

	/**
	 * @param mShippingFee
	 *            the mShippingFee to set
	 */
	public void setShippingFee(String mShippingFee) {
		this.mShippingFee = mShippingFee;
	}

	/**
	 * @param mTotal
	 *            the mTotal to set
	 */
	public void setTotal(String mTotal) {
		this.mTotal = mTotal;
	}

	/**
	 * @param mProducts
	 *            the mProducts to set
	 */
	public void setProducts(ArrayList<String> mProducts) {
		this.mProducts = mProducts;
	}

	/**
	 * @param mShippingAddress
	 *            the mShippingAddress to set
	 */
	public void setShippingAddress(Address mShippingAddress) {
		this.mShippingAddress = mShippingAddress;
	}

	/**
	 * @param mBillingAddress
	 *            the mBillingAddress to set
	 */
	public void setBillingAddress(Address mBillingAddress) {
		this.mBillingAddress = mBillingAddress;
	}

	/**
	 * @param mShippingMethod
	 *            the mShippingMethod to set
	 */
	public void setShippingMethod(ShippingMethods mShippingMethod) {
		this.mShippingMethod = mShippingMethod;
	}

	/**
	 * @param mPaymentOptions
	 *            the mPaymentOptions to set
	 */
	public void setPaymentOptions(PaymentMethods mPaymentOptions) {
		this.mPaymentOptions = mPaymentOptions;
	}

	/**
	 * @return the mOrderNumber
	 */
	public String getOrderNumber() {
		return mOrderNumber;
	}

	/**
	 * @return the mFirstName
	 */
	public String getFirstName() {
		return mFirstName;
	}

	/**
	 * @return the mLastName
	 */
	public String getLastName() {
		return mLastName;
	}

	/**
	 * @param mOrderNumber the mOrderNumber to set
	 */
	public void setOrderNumber(String mOrderNumber) {
		this.mOrderNumber = mOrderNumber;
	}

	/**
	 * @param mFirstName the mFirstName to set
	 */
	public void setFirstName(String mFirstName) {
		this.mFirstName = mFirstName;
	}

	/**
	 * @param mLastName the mLastName to set
	 */
	public void setLastName(String mLastName) {
		this.mLastName = mLastName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		
        // Metada:
        
        // Cash On Delivery:
//        {
//            "success": true,
//            "messages": {
//                "success": [
//                    "ORDER_SUCCESS"
//                ]
//            },
//            "session": {
//                "id": "hec322fvnb97f0kqp7mv6s2e81",
//                "expire": null,
//                "YII_CSRF_TOKEN": "92945c37307600580b25eee7e6d6fa690a9aa126"
//            },
//            "metadata": {
//                "order_nr": "300012712",
//                "customer_first_name": "mob nsme",
//                "customer_last_name": "mob last",
//                "payment": []
//            }
//        }
		
		
		// Get order number
        mOrderNumber = jsonObject.getString("order_nr");
        // Get first name
        mFirstName = jsonObject.optString("customer_first_name");
        // Get last name
        mLastName = jsonObject.optString("customer_last_name");
        
        // Validate payment content
        if(jsonObject.has("payment")){
        	Log.d(TAG, "HAS PAYMENT DATA");	
        	JSONObject jsonPayment = jsonObject.optJSONObject("payment");
        	if(jsonPayment != null) {
        		Log.d(TAG, "PAYMENT DATA: " + jsonPayment.toString());
        	}
        	
        }
        
        
        // Paga:
        // payment: { type, method, form, url}
        
        // This step only occurs if the response returned on step 6 has a key "payment".
        // Depending on the selected payment method, client will be asked to provide payment details or redirected to the payment provider's external page to provide payment details.
        // TODO: Filter response by method
        // Cash On Delivery -   If the payment method selected was "Cash On Delivery" this step not applicable.
        // Paga             -   The follow response tell us that is need made a auto submit form for action
        // WebPAY           -   In this case the form has a property named "target" that indicates the result of the form's submit has to be displayed in an iframe, whose NAME is "target" property's value
        // GlobalPay        -   Auto-submit-external
        // Wallety          -   Submit-external
        // AdyenPayment     -   Page
		
		
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		// TODO
		return null;
	}

	/**
	 * ########### Parcelable ###########
	 * 
	 * @author sergiopereira
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO
		// dest.writeString(order_id);
		// dest.writeString(creation_date);
		// dest.writeString(payment_method);
		// dest.writeString(last_order_update);
		// dest.writeList(orderTracketItems);
	}

	/**
	 * Parcel constructor
	 * 
	 * @param in
	 */
	private OrderSummary(Parcel in) {
		// TODO
		// order_id = in.readString();
		// creation_date = in.readString();
		// payment_method = in.readString();
		// last_order_update = in.readString();
		// in.readList(orderTracketItems,
		// OrderTrackerItem.class.getClassLoader());
	}

	/**
	 * Create parcelable
	 */
	public static final Parcelable.Creator<OrderSummary> CREATOR = new Parcelable.Creator<OrderSummary>() {
		public OrderSummary createFromParcel(Parcel in) {
			return new OrderSummary(in);
		}

		public OrderSummary[] newArray(int size) {
			return new OrderSummary[size];
		}
	};

}
