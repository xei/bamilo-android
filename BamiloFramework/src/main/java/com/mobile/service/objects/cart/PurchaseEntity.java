package com.mobile.service.objects.cart;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.addresses.Address;
import com.mobile.service.objects.checkout.Fulfillment;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * The Order entity class representation.<br/>
 * Contains: Order, Items, Shipping, Payment and Billing.
 * @author Sergio Pereira
 */
public class PurchaseEntity implements IJSONSerializable, Parcelable {

    public static final String TAG = PurchaseEntity.class.getSimpleName();

    private double mTotal;
    private double mTotalConverted;
    private double mSubTotal;
    private double mSubTotalConverted;
    private int mCartCount;
    private double mVatValue;
    private String mVatLabel;
    private double mShippingValue;
    private double mExtraCosts;
    private double mSumCostsValue;
    private double mCouponDiscount;
    private String mCouponCode;
    private HashMap<String, String> mPriceRules;
    private ArrayList<PurchaseCartItem> mCartItems;
    private String mShippingMethod;
    private String mPaymentMethod;
    private Address mBillingAddress;
    private Address mShippingAddress;
    private boolean mIsVatEnabled;
    private ArrayList<Fulfillment> mFulfillmentList;
    private PurchaseCartItem mLastItemAdded;
    private boolean hasFreeShipping;
    private double mSubTotalUnDiscounted;

    /**
     * Constructor
     */
    public PurchaseEntity() {
        super();
    }

    @Override
    public boolean initialize(JSONObject json) throws JSONException {
        // Cart entity
        JSONObject jsonObject = json.getJSONObject(RestConstants.CART_ENTITY);
        // Total
        mTotal = jsonObject.getDouble(RestConstants.TOTAL);
        mTotalConverted = jsonObject.getDouble(RestConstants.TOTAL_CONVERTED);
        // Get cart sub total
        mSubTotal = jsonObject.optDouble(RestConstants.SUB_TOTAL);
        mSubTotalUnDiscounted = jsonObject.optDouble(RestConstants.SUB_TOTAL_UN_DISCOUNTED);
        mSubTotalConverted = jsonObject.optDouble(RestConstants.SUB_TOTAL_CONVERTED);
        // Vat
        JSONObject vatObject = jsonObject.optJSONObject(RestConstants.VAT);
        if (vatObject != null) {
            mVatValue = vatObject.optDouble(RestConstants.VALUE);
            mVatLabel = vatObject.optString(RestConstants.LABEL);
            mIsVatEnabled = vatObject.optBoolean(RestConstants.LABEL_CONFIGURATION);
        }
        // Delivery
        JSONObject deliveryObject = jsonObject.optJSONObject(RestConstants.DELIVERY);
        if (deliveryObject != null) {
            mShippingValue = deliveryObject.optDouble(RestConstants.AMOUNT);
        }
        // Coupon
        JSONObject couponObject = jsonObject.optJSONObject(RestConstants.COUPON);
        if (couponObject != null) {
            mCouponCode = couponObject.optString(RestConstants.CODE);
            mCouponDiscount = couponObject.optDouble(RestConstants.VALUE);
        }
        // Costs
        mSumCostsValue = jsonObject.optDouble(RestConstants.SUM_COSTS_VALUE);
        // Extra costs
        mExtraCosts = jsonObject.optDouble(RestConstants.EXTRA_COSTS);
        // Get cart count
        mCartCount = jsonObject.getInt(RestConstants.TOTAL_PRODUCTS);
        JSONArray cartArray = jsonObject.getJSONArray(RestConstants.PRODUCTS);
        mCartItems = new ArrayList<>();
        for (int i = 0; i < cartArray.length(); i++) {
            JSONObject cartObject = cartArray.getJSONObject(i);
            PurchaseCartItem item = new PurchaseCartItem();
            item.initialize(cartObject);
            mCartItems.add(item);
            hasFreeShipping = item.hasFreeShipping() || hasFreeShipping;
        }
        // Last item added
        if(CollectionUtils.isNotEmpty(mCartItems)) {
            mLastItemAdded = mCartItems.get(mCartItems.size() - 1);
        } else {
            mLastItemAdded = new PurchaseCartItem();
        }
        // Price rules
        JSONArray priceRules = jsonObject.optJSONArray(RestConstants.PRICE_RULES);
        if (priceRules != null && priceRules.length() > 0) {
            mPriceRules = new HashMap<>();
            for (int i = 0; i < priceRules.length(); i++) {
                JSONObject pRulesElement = priceRules.optJSONObject(i);
                if (pRulesElement != null) {
                    mPriceRules.put(pRulesElement.getString(RestConstants.LABEL), pRulesElement.getString(RestConstants.VALUE));
                }
            }
        }
        // Get shipping method
        JSONObject jsonShip = jsonObject.optJSONObject(RestConstants.SHIPPING_METHOD);
        if (jsonShip != null) {
            mShippingMethod = jsonShip.optString(RestConstants.METHOD);
        }
        // Get payment method
        JSONObject jsonPay = jsonObject.optJSONObject(RestConstants.PAYMENT_METHOD);
        if (jsonPay != null) {
            mPaymentMethod = jsonPay.optString(RestConstants.LABEL);
        }
        // Get billing address
        JSONObject jsonBilAddress = jsonObject.optJSONObject(RestConstants.BILLING_ADDRESS);
        if (jsonBilAddress != null) {
            mBillingAddress = new Address();
            mBillingAddress.initialize(jsonBilAddress);
        }
        // Get shipping address
        JSONObject jsonShipAddress = jsonObject.optJSONObject(RestConstants.SHIPPING_ADDRESS);
        if (jsonShipAddress != null) {
            mShippingAddress = new Address();
            mShippingAddress.initialize(jsonShipAddress);
        }
        // Get fulfilment
        JSONArray fulfillmentArray = jsonObject.optJSONArray(RestConstants.FULFILLMENT);
        if(fulfillmentArray != null) {
            mFulfillmentList = new ArrayList<>();
            for (int i = 0; i < fulfillmentArray.length(); i++) {
                mFulfillmentList.add(new Fulfillment(fulfillmentArray.getJSONObject(i)));
            }
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
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

	/*
     * ########### GETTERS AND SETTERS ###########
	 */

    public ArrayList<PurchaseCartItem> getCartItems() {
        return mCartItems;
    }

    public double getTotal() {
        return mTotal;
    }

    public int getCartCount() {
        return mCartCount;
    }

    public double getVatValue() {
        return this.mVatValue;
    }

    public String getVatLabel() {
        return this.mVatLabel;
    }

    public double getShippingValue() {
        return mShippingValue;
    }

    public double getExtraCosts() {
        return mExtraCosts;
    }

    public double getSumCostsValue() {
        return mSumCostsValue;
    }

    public double getCouponDiscount() {
        return mCouponDiscount;
    }

    public String getCouponCode() {
        return mCouponCode;
    }

    public HashMap<String, String> getPriceRules() {
        return mPriceRules;
    }

    public double getPriceForTracking() {
        return mTotalConverted;
    }

    public double getSubTotal() {
        return mSubTotal;
    }

    public double getSubTotalUnDiscounted() {
        return mSubTotalUnDiscounted;
    }

    public boolean hasSubTotalUnDiscounted() {
        return mSubTotal < mSubTotalUnDiscounted && !Double.isNaN(mSubTotalUnDiscounted);
    }

    public String getShippingMethod() {
        return mShippingMethod;
    }

    public String getPaymentMethod() {
        return mPaymentMethod;
    }

    public Address getShippingAddress() {
        return mShippingAddress;
    }

    public Address getBillingAddress() {
        return mBillingAddress;
    }

    public ArrayList<Fulfillment> getFulfillmentList() {
        return mFulfillmentList;
    }

    /**
     * For tracking
     */
    @NonNull
    public PurchaseCartItem getLastItemAdded() {
        return mLastItemAdded;
    }

    /**
     * For tracking
     */
    @NonNull
    public PurchaseCartItem getTheMostExpensiveItem() {
        Iterator<PurchaseCartItem> it = mCartItems.iterator();
        PurchaseCartItem max = it.next();
        while (it.hasNext()) {
            PurchaseCartItem next = it.next();
            if (next.getPrice() > max.getPrice()) {
                max = next;
            }
        }
        return max;
    }

    /**
     * ########### VALIDATORS ###########
     */

    public boolean hasSumCosts() {
        return mSumCostsValue > 0 && mSumCostsValue != Double.NaN;
    }

    public boolean hasCouponDiscount() {
        return TextUtils.isNotEmpty(mCouponCode);
    }

    public boolean isVatLabelEnable() {
        return mIsVatEnabled;
    }

    public boolean hasShippingAddress() {
        return mShippingAddress != null;
    }

    public boolean hasBillingAddress() {
        return mBillingAddress != null;
    }

    public boolean hasSameAddresses() {
        return mBillingAddress != null && mShippingAddress != null && mBillingAddress.getId() == mShippingAddress.getId();
    }

    public boolean hasShippingMethod() {
        return TextUtils.isNotEmpty(mShippingMethod);
    }

    public boolean hasFreeShipping() {
        return hasFreeShipping;
    }

	/*
     * ########### PARCELABLE ###########
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
        dest.writeDouble(mTotal);
        dest.writeDouble(mTotalConverted);
        dest.writeDouble(mSubTotal);
        dest.writeDouble(mSubTotalConverted);
        dest.writeInt(mCartCount);
        dest.writeDouble(mVatValue);
        dest.writeString(mVatLabel);
        dest.writeDouble(mShippingValue);
        dest.writeDouble(mExtraCosts);
        dest.writeDouble(mSumCostsValue);
        dest.writeDouble(mCouponDiscount);
        dest.writeString(mCouponCode);
        dest.writeMap(mPriceRules);
        if (mCartItems == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mCartItems);
        }
        dest.writeString(mShippingMethod);
        dest.writeString(mPaymentMethod);
        dest.writeValue(mBillingAddress);
        dest.writeValue(mShippingAddress);
        dest.writeByte((byte) (mIsVatEnabled ? 1 : 0));
        if (mFulfillmentList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mFulfillmentList);
        }
        dest.writeValue(mLastItemAdded);
        dest.writeByte((byte) (hasFreeShipping ? 0x01 : 0x00));
        dest.writeDouble(mSubTotalUnDiscounted);
    }

    /**
     * Parcel constructor
     */
    public PurchaseEntity(Parcel in) {
        mTotal = in.readDouble();
        mTotalConverted = in.readDouble();
        mSubTotal = in.readDouble();
        mSubTotalConverted = in.readDouble();
        mCartCount = in.readInt();
        mVatValue = in.readDouble();
        mVatLabel = in.readString();
        mShippingValue = in.readDouble();
        mExtraCosts = in.readDouble();
        mSumCostsValue = in.readDouble();
        mCouponDiscount = in.readDouble();
        mCouponCode = in.readString();
        mPriceRules = new HashMap<>();
        in.readMap(mPriceRules, String.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mCartItems = new ArrayList<>();
            in.readList(mCartItems, PurchaseCartItem.class.getClassLoader());
        } else {
            mCartItems = null;
        }
        mShippingMethod = in.readString();
        mPaymentMethod = in.readString();
        mBillingAddress = (Address) in.readValue(Address.class.getClassLoader());
        mShippingAddress = (Address) in.readValue(Address.class.getClassLoader());
        mIsVatEnabled = in.readByte() == 1;
        if (in.readByte() == 0x01) {
            mFulfillmentList = new ArrayList<>();
            in.readList(mFulfillmentList, Fulfillment.class.getClassLoader());
        } else {
            mFulfillmentList = null;
        }
        mLastItemAdded = (PurchaseCartItem) in.readValue(PurchaseCartItem.class.getClassLoader());
        hasFreeShipping = in.readByte() != 0x00;
        mSubTotalUnDiscounted = in.readDouble();
    }

    /**
     * Create parcelable
     */
    public static final Creator<PurchaseEntity> CREATOR = new Creator<PurchaseEntity>() {
        public PurchaseEntity createFromParcel(Parcel in) {
            return new PurchaseEntity(in);
        }

        public PurchaseEntity[] newArray(int size) {
            return new PurchaseEntity[size];
        }
    };

    public void setTotal(double total) {
        mTotal = total;
    }

    public void setTotalConverted(double totalConverted) {
        mTotalConverted = totalConverted;
    }

    public void setSubTotal(double subTotal) {
        mSubTotal = subTotal;
    }

    public void setSubTotalConverted(double subTotalConverted) {
        mSubTotalConverted = subTotalConverted;
    }

    public void setCartCount(int cartCount) {
        mCartCount = cartCount;
    }

    public void setVatValue(double vatValue) {
        mVatValue = vatValue;
    }

    public void setVatLabel(String vatLabel) {
        mVatLabel = vatLabel;
    }

    public void setShippingValue(double shippingValue) {
        mShippingValue = shippingValue;
    }

    public void setExtraCosts(double extraCosts) {
        mExtraCosts = extraCosts;
    }

    public void setSumCostsValue(double sumCostsValue) {
        mSumCostsValue = sumCostsValue;
    }

    public void setCouponDiscount(double couponDiscount) {
        mCouponDiscount = couponDiscount;
    }

    public void setCouponCode(String couponCode) {
        mCouponCode = couponCode;
    }

    public void setPriceRules(HashMap<String, String> priceRules) {
        mPriceRules = priceRules;
    }

    public void setCartItems(ArrayList<PurchaseCartItem> cartItems) {
        mCartItems = cartItems;
    }

    public void setShippingMethod(String shippingMethod) {
        mShippingMethod = shippingMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        mPaymentMethod = paymentMethod;
    }

    public void setBillingAddress(Address billingAddress) {
        mBillingAddress = billingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        mShippingAddress = shippingAddress;
    }

    public void setVatEnabled(boolean vatEnabled) {
        mIsVatEnabled = vatEnabled;
    }

    public void setFulfillmentList(
            ArrayList<Fulfillment> fulfillmentList) {
        mFulfillmentList = fulfillmentList;
    }

    public void setLastItemAdded(PurchaseCartItem lastItemAdded) {
        mLastItemAdded = lastItemAdded;
    }

    public void setHasFreeShipping(boolean hasFreeShipping) {
        this.hasFreeShipping = hasFreeShipping;
    }

    public void setSubTotalUnDiscounted(double subTotalUnDiscounted) {
        mSubTotalUnDiscounted = subTotalUnDiscounted;
    }
}