
package pt.rocket.framework.objects;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class OrderSummary implements IJSONSerializable, Parcelable {

	public final static String TAG = LogTagHelper.create(OrderSummary.class);

	private String mGrandTotal;

	private String mShippingAmount;

	private String mExtraCost;

	private String mDiscountAmount;

	private String mInstallmentFees;

	private String mTaxAmount;

	private String mCustomerDevice;
	
	private ShoppingCart mCart;
	
	private String mShippingMethod;
	
	private String mPaymentMethod;
	
	private Address mShippingAddress;
	
	private Address mBillingAddress;

	private Map<String, Map<String, String>> simpleData;

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
	public OrderSummary(JSONObject jsonObject, Map<String, Map<String, String>> simpleData) throws JSONException {
		this.simpleData = simpleData;
		initialize(jsonObject);
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
		
		
		// Get order
        JSONObject jsonOrder = null;
        if(!jsonObject.isNull(RestConstants.JSON_ORDER_TAG)) {
            jsonOrder = jsonObject.optJSONObject(RestConstants.JSON_ORDER_TAG);
            Log.d(TAG, "ORDER: " + jsonOrder.toString());
    		mGrandTotal = jsonOrder.optString(RestConstants.JSON_ORDER_GRAND_TOTAL_TAG);
    		mShippingAmount = jsonOrder.optString(RestConstants.JSON_ORDER_SHIP_AMOUNT_TAG);
    		mExtraCost = jsonOrder.optString(RestConstants.JSON_ORDER_EXTRA_PAYMENTS_TAG);
    		mDiscountAmount = jsonOrder.optString(RestConstants.JSON_ORDER_BNP_DISCOUNT_TAG);
    		mInstallmentFees = jsonOrder.optString(RestConstants.JSON_ORDER_INSTALLMENT_FEES_TAG);
    		mTaxAmount = jsonOrder.optString(RestConstants.JSON_TAX_AMOUNT_TAG);				// VAT
    		mCustomerDevice = jsonOrder.optString(RestConstants.JSON_ORDER_USER_DEVICE_TAG);
        }
        
        // Get cart
        if(jsonOrder != null && !jsonObject.isNull(RestConstants.JSON_CART_TAG)) {
            JSONObject jsonCart = jsonObject.optJSONObject(RestConstants.JSON_CART_TAG);
            ShoppingCart cart = new ShoppingCart(simpleData);
            cart.initialize(jsonCart);
            mCart = cart;
        }
        
        // Get shipping method
        if(jsonOrder != null && !jsonOrder.isNull(RestConstants.JSON_ORDER_SHIP_MET_TAG)) {
            JSONObject jsonShip = jsonOrder.optJSONObject(RestConstants.JSON_ORDER_SHIP_MET_TAG);
            Log.d(TAG, "SHIP METHOD: " + jsonShip.toString());
            String shipMethod = jsonShip.optString(RestConstants.JSON_METHOD_TAG);
            mShippingMethod = shipMethod;
        }
    
        // Get payment method
        if(jsonOrder != null && !jsonOrder.isNull(RestConstants.JSON_ORDER_PAYMENT_METHOD_TAG)) {
            JSONObject jsonPay = jsonOrder.optJSONObject(RestConstants.JSON_ORDER_PAYMENT_METHOD_TAG);
            if(jsonPay != null){
            	Log.d(TAG, "PAY METHOD: " + jsonPay.toString());
                // String payId = jsonPay.optString("id");
                String payProvider = jsonPay.optString(RestConstants.JSON_ORDER_PAYMENT_PROVIDER_TAG);
                mPaymentMethod = payProvider;	
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
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
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
		String shipMethod = (mShippingMethod != null) ? mShippingMethod : "";
		String payMethod = (mPaymentMethod != null) ? mPaymentMethod : "";
		return mGrandTotal + " " + 
				mShippingAmount + " " + 
				mExtraCost + " " + 
				mDiscountAmount + " " + 
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
	public String getShippingAmount() {
		return mShippingAmount;
	}

	/**
	 * @return the extraCost
	 */
	public String getExtraCost() {
		return mExtraCost;
	}

	/**
	 * @return the discountAmount
	 */
	public String getDiscountAmount() {
		return mDiscountAmount;
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
	public void setShippingAmount(String shippingAmount) {
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
	 * @param discountAmount
	 *            the discountAmount to set
	 */
	public void setDiscountAmount(String discountAmount) {
		this.mDiscountAmount = discountAmount;
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
	 * @param customerDevice
	 *            the customerDevice to set
	 */
	public void setCart(ShoppingCart cart) {
		this.mCart = cart;
	}
	
	/**
	 * @param customerDevice
	 *            the customerDevice to set
	 */
	public void setShippingMethod(String method) {
		this.mShippingMethod = method;
	}
	
	/**
	 * @param customerDevice
	 *            the customerDevice to set
	 */
	public void setPaymentMethod(String method) {
		this.mPaymentMethod = method;
	}
	
	/**
	 * @param customerDevice
	 *            the customerDevice to set
	 */
	public void setShippingAddress(Address address) {
		this.mShippingAddress = address;
	}
	
	/**
	 * @param customerDevice
	 *            the customerDevice to set
	 */
	public void setBillingAddress(Address address) {
		this.mBillingAddress = address;
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
		return (mExtraCost != null && !mExtraCost.equals("0")) ? true : false;
	}
	
	public boolean hasCoupon(){
		return (mDiscountAmount != null && !mDiscountAmount.equals("0")) ? true : false;
	}

	public boolean hasDiscount(){
		Log.d(TAG, "DISCOUNT: " + mDiscountAmount);
		return (mDiscountAmount != null && !mDiscountAmount.equals("0")) ? true : false;
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
		 dest.writeString(mShippingAmount);
		 dest.writeString(mExtraCost);
		 dest.writeString(mDiscountAmount);
		 dest.writeString(mInstallmentFees);
		 dest.writeString(mTaxAmount);
		 dest.writeString(mCustomerDevice);
		 dest.writeParcelable(mCart, 0);
		 dest.writeString(mShippingMethod);
		 dest.writeString(mPaymentMethod);
		 dest.writeParcelable(mShippingAddress, 0);
		 dest.writeParcelable(mBillingAddress, 0);
	}

	/**
	 * Parcel constructor
	 * 
	 * @param in
	 */
	private OrderSummary(Parcel in) {
		 mGrandTotal = in.readString();
		 mShippingAmount = in.readString();
		 mExtraCost = in.readString();
		 mDiscountAmount = in.readString();
		 mInstallmentFees = in.readString();
		 mTaxAmount = in.readString();
		 mCustomerDevice = in.readString();
		 mCart = in.readParcelable(ShoppingCart.class.getClassLoader());
		 mShippingMethod = in.readString();
		 mPaymentMethod = in.readString();
		 mShippingAddress = in.readParcelable(Address.class.getClassLoader());
		 mBillingAddress = in.readParcelable(Address.class.getClassLoader());
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
