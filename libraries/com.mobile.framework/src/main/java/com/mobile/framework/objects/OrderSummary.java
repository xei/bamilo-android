package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.LogTagHelper;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class OrderSummary implements IJSONSerializable, Parcelable {

	public final static String TAG = LogTagHelper.create(OrderSummary.class);

	private String mGrandTotal;

	private double mShippingAmount;

	private String mExtraCost;

	private String mInstallmentFees;

	private String mTaxAmount;

	private String mCustomerDevice;
	
	private ShoppingCart mCart;
	
	private String mShippingMethod;
	
	private String mPaymentMethod;
	
	private Address mShippingAddress;
	
	private Address mBillingAddress;

	private String mDiscountCouponValue;

	private String mDiscountCouponCode;

    private double mGrandTotalConverted;
    
    private String mShippingMethodLabel;
    
    private String mPaymentMethodLabel;

	/**
	 * 
	 */
	public OrderSummary() {
	}

	/**
	 * 
	 * @param jsonObject
	 * @throws JSONException
	 */
	public OrderSummary(JSONObject jsonObject) throws JSONException {
		initialize(jsonObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		
		
		// Get order
        JSONObject jsonOrder = null;
        if(!jsonObject.isNull(RestConstants.JSON_ORDER_TAG)) {
            jsonOrder = jsonObject.optJSONObject(RestConstants.JSON_ORDER_TAG);
            Log.d(TAG, "ORDER: " + jsonOrder.toString());
    		mGrandTotal = jsonOrder.optString(RestConstants.JSON_ORDER_GRAND_TOTAL_TAG);
    		mGrandTotalConverted = jsonOrder.optDouble(RestConstants.JSON_ORDER_GRAND_TOTAL_CONVERTED_TAG);
    		mShippingAmount = jsonOrder.optDouble(RestConstants.JSON_ORDER_SHIP_AMOUNT_TAG);
    		mExtraCost = jsonOrder.optString(RestConstants.JSON_ORDER_EXTRA_PAYMENTS_TAG);
    		mInstallmentFees = jsonOrder.optString(RestConstants.JSON_ORDER_INSTALLMENT_FEES_TAG);
    		mTaxAmount = jsonOrder.optString(RestConstants.JSON_TAX_AMOUNT_TAG);				// VAT
    		mCustomerDevice = jsonOrder.optString(RestConstants.JSON_ORDER_USER_DEVICE_TAG);
    		mDiscountCouponValue = jsonOrder.optString(RestConstants.JSON_ORDER_COUPON_DISCOUNT_TAG);
    		mDiscountCouponCode = jsonOrder.optString(RestConstants.JSON_ORDER_COUPON_CODE_TAG);
        }
        
        // Get cart
        if(jsonOrder != null && !jsonObject.isNull(RestConstants.JSON_CART_TAG)) {
            JSONObject jsonCart = jsonObject.optJSONObject(RestConstants.JSON_CART_TAG);
            Log.d(TAG, "CART: " + jsonCart.toString());
            ShoppingCart cart = new ShoppingCart();
            cart.initialize(jsonCart);
            mCart = cart;
        }
        
        // Get shipping method
        if(jsonOrder != null && !jsonOrder.isNull(RestConstants.JSON_ORDER_SHIP_MET_TAG)) {
            JSONObject jsonShip = jsonOrder.optJSONObject(RestConstants.JSON_ORDER_SHIP_MET_TAG);
            Log.d(TAG, "SHIP METHOD: " + jsonShip.toString());
            String shipMethod = jsonShip.optString(RestConstants.JSON_METHOD_TAG);
            mShippingMethod = shipMethod;
            String shipMethodLabel = jsonShip.optString(RestConstants.JSON_LABEL_TAG);
            if("".equals(shipMethodLabel))
                mShippingMethodLabel = mShippingMethod;
            
            mShippingMethodLabel = shipMethodLabel;
        }
    
        // Get payment method
        if(jsonOrder != null && !jsonOrder.isNull(RestConstants.JSON_ORDER_PAYMENT_METHOD_TAG)) {
            JSONObject jsonPay = jsonOrder.optJSONObject(RestConstants.JSON_ORDER_PAYMENT_METHOD_TAG);
            if(jsonPay != null){
            	Log.d(TAG, "PAY METHOD: " + jsonPay.toString());
                // String payId = jsonPay.optString("id");
                String payProvider = jsonPay.optString(RestConstants.JSON_ORDER_PAYMENT_PROVIDER_TAG);
                mPaymentMethod = payProvider;	
                
                String paymentMethodLabel = jsonPay.optString(RestConstants.JSON_LABEL_TAG);
                if("".equals(paymentMethodLabel))
                    mPaymentMethodLabel = mPaymentMethod;
                
                mPaymentMethodLabel = paymentMethodLabel;
                
            } else {
            	mPaymentMethod = jsonOrder.optString(RestConstants.JSON_ORDER_PAYMENT_METHOD_TAG);
            }
            
        }
        
        // Get billing address
        if(jsonOrder != null && !jsonOrder.isNull(RestConstants.JSON_ORDER_BIL_ADDRESS_TAG)) {
            JSONObject jsonBilAddress = jsonOrder.optJSONObject(RestConstants.JSON_ORDER_BIL_ADDRESS_TAG);
            Log.d(TAG, "BILLING ADDRESS: " + jsonBilAddress.toString());
            Address billingAddress = new Address(jsonBilAddress);
            mBillingAddress = billingAddress;
        }
        
        // Get shipping address
        if(jsonOrder != null && !jsonOrder.isNull(RestConstants.JSON_ORDER_SHIP_ADDRESS_TAG)) {
            JSONObject jsonShipAddress = jsonOrder.optJSONObject(RestConstants.JSON_ORDER_SHIP_ADDRESS_TAG);
            Log.d(TAG, "SHIPPING ADDRESS: " + jsonShipAddress.toString());
            Address shippingAddress = new Address(jsonShipAddress);
            mShippingAddress = shippingAddress;
        }

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		// TODO
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String shipAddress = (mShippingAddress != null) ? mShippingAddress.getAddress() : "";
		String billAddress = (mBillingAddress != null) ? mBillingAddress.getAddress() : "";
		String shipMethod = (mShippingMethodLabel != null) ? mShippingMethodLabel : "";
		String payMethod = (mPaymentMethodLabel != null) ? mShippingMethodLabel : "";
		return mGrandTotal + " " + 
				mShippingAmount + " " + 
				mExtraCost + " " + 
				mInstallmentFees + " " + 
				mTaxAmount + " " + 
				mCustomerDevice + " " +
				shipAddress + " " + 
				billAddress + " " +
				shipMethod + " " +
				payMethod;
	}

	/**
	 * @return the total
	 */
	public String getTotal() {
		return mGrandTotal;
	}

	/**
	 * @return the shippingAmount
	 */
	public double getShippingAmount() {
		return mShippingAmount;
	}

	/**
	 * @return the extraCost
	 */
	public String getExtraCost() {
		return mExtraCost;
	}

	/**
	 * @return the installmentFees
	 */
	public String getInstallmentFees() {
		return mInstallmentFees;
	}

	/**
	 * @return the taxAmount
	 */
	public String getTaxAmount() {
		return mTaxAmount;
	}

	/**
	 * @return the customerDevice
	 */
	public String getCustomerDevice() {
		return mCustomerDevice;
	}
	
	/**
	 * @return the mCart
	 */
	public ShoppingCart getCart() {
		return mCart;
	}

	/**
	 * @return the mShippingMethod
	 */
	public String getShippingMethod() {
		return mShippingMethod;
	}

	/**
	 * @return the mPaymentMethod
	 */
	public String getPaymentMethod() {
		return mPaymentMethod;
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
	 * Return the order value for tracking
	 * @author sergiopereira
	 */
	public double getValueForTracking() {
	    return mGrandTotalConverted;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(String total) {
		this.mGrandTotal = total;
	}

	/**
	 * @param shippingAmount
	 *            the shippingAmount to set
	 */
	public void setShippingAmount(double shippingAmount) {
		this.mShippingAmount = shippingAmount;
	}

	/**
	 * @param extraCost
	 *            the extraCost to set
	 */
	public void setExtraCost(String extraCost) {
		this.mExtraCost = extraCost;
	}

	/**
	 * @param installmentFees
	 *            the installmentFees to set
	 */
	public void setInstallmentFees(String installmentFees) {
		this.mInstallmentFees = installmentFees;
	}

	/**
	 * @param taxAmount
	 *            the taxAmount to set
	 */
	public void setTaxAmount(String taxAmount) {
		this.mTaxAmount = taxAmount;
	}

	/**
	 * @param customerDevice
	 *            the customerDevice to set
	 */
	public void setCustomerDevice(String customerDevice) {
		this.mCustomerDevice = customerDevice;
	}
	
	/**
	 * @param cart
	 *
	 */
	public void setCart(ShoppingCart cart) {
		this.mCart = cart;
	}
	
	/**
	 * @param method
	 *
	 */
	public void setShippingMethod(String method) {
		this.mShippingMethod = method;
	}
	
	/**
	 * @param method
	 *
	 */
	public void setPaymentMethod(String method) {
		this.mPaymentMethod = method;
	}
	
	/**
	 * @param address
	 *
	 */
	public void setShippingAddress(Address address) {
		this.mShippingAddress = address;
	}
	
	/**
	 * @param address
	 *
	 */
	public void setBillingAddress(Address address) {
		this.mBillingAddress = address;
	}
	
	
	
	/**
	 * @return the mDiscountCouponValue
	 */
	public String getDiscountCouponValue() {
		return mDiscountCouponValue;
	}

	/**
	 * @return the mDiscountCouponCode
	 */
	public String getDiscountCouponCode() {
		return mDiscountCouponCode;
	}

	/**
	 * @param mDiscountCouponValue the mDiscountCouponValue to set
	 */
	public void setDiscountCouponValue(String mDiscountCouponValue) {
		this.mDiscountCouponValue = mDiscountCouponValue;
	}

	/**
	 * @param mDiscountCouponCode the mDiscountCouponCode to set
	 */
	public void setDiscountCouponCode(String mDiscountCouponCode) {
		this.mDiscountCouponCode = mDiscountCouponCode;
	}

	/**
	 * ########### VALIDATORS ###########
	 */
	
	
	public boolean hasShippingAddress(){
		return (mShippingAddress != null) ? true : false;
	}
	
	public boolean hasShippingMethod(){
		return (mShippingMethod != null) ? true : false;
	}
	
	public boolean hasShippingFees(){
		return (!TextUtils.isEmpty(mExtraCost)) ? true : false;
	}
	
	public boolean hasCouponCode(){
		Log.d(TAG, "DISCOUNT CODE: " + mDiscountCouponCode);
		return (!TextUtils.isEmpty(mDiscountCouponCode)) ? true : false;
	}

	public boolean hasCouponDiscount(){
		Log.d(TAG, "DISCOUNT VALUE: " + mDiscountCouponValue);
		return (!TextUtils.isEmpty(mDiscountCouponValue) && !mDiscountCouponValue.equals("0")) ? true : false;
	}
	
	public String getmShippingMethodLabel() {
        return mShippingMethodLabel;
    }

    public void setmShippingMethodLabel(String mShippingMethodLabel) {
        this.mShippingMethodLabel = mShippingMethodLabel;
    }

    public String getmPaymentMethodLabel() {
        return mPaymentMethodLabel;
    }

    public void setmPaymentMethodLabel(String mPaymentMethodLabel) {
        this.mPaymentMethodLabel = mPaymentMethodLabel;
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
		 dest.writeString(mGrandTotal);
		 dest.writeDouble(mShippingAmount);
		 dest.writeString(mExtraCost);
		 dest.writeString(mInstallmentFees);
		 dest.writeString(mTaxAmount);
		 dest.writeString(mCustomerDevice);
		 dest.writeParcelable(mCart, 0);
		 dest.writeString(mShippingMethod);
		 dest.writeString(mPaymentMethod);
		 dest.writeParcelable(mShippingAddress, 0);
		 dest.writeParcelable(mBillingAddress, 0);
		 dest.writeDouble(mGrandTotalConverted);
		 dest.writeString(mShippingMethodLabel);
         dest.writeString(mPaymentMethodLabel);
	}

	/**
	 * Parcel constructor
	 * 
	 * @param in
	 */
	private OrderSummary(Parcel in) {
		 mGrandTotal = in.readString();
		 mShippingAmount = in.readDouble();
		 mExtraCost = in.readString();
		 mInstallmentFees = in.readString();
		 mTaxAmount = in.readString();
		 mCustomerDevice = in.readString();
		 mCart = in.readParcelable(ShoppingCart.class.getClassLoader());
		 mShippingMethod = in.readString();
		 mPaymentMethod = in.readString();
		 mShippingAddress = in.readParcelable(Address.class.getClassLoader());
		 mBillingAddress = in.readParcelable(Address.class.getClassLoader());
		 mGrandTotalConverted = in.readDouble();
		 mShippingMethodLabel = in.readString();
         mPaymentMethodLabel = in.readString();
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
