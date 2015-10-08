package com.mobile.newFramework.objects.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


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

    /**
     * Constructor
     */
    public PurchaseEntity() {
        super();
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Cart entity
        JSONObject cartEntity = jsonObject.getJSONObject(RestConstants.CART_ENTITY);
        // Total
        mTotal = cartEntity.getDouble(RestConstants.TOTAL);
        mTotalConverted = cartEntity.getDouble(RestConstants.TOTAL_CONVERTED);
        // Get cart sub total
        mSubTotal = cartEntity.optDouble(RestConstants.SUB_TOTAL);
        mSubTotalConverted = cartEntity.optDouble(RestConstants.SUB_TOTAL_CONVERTED);
        // Vat
        JSONObject vatObject = cartEntity.optJSONObject(RestConstants.VAT);
        if (vatObject != null) {
            mVatValue = vatObject.optDouble(RestConstants.VALUE);
        }
        // Delivery
        JSONObject deliveryObject = cartEntity.optJSONObject(RestConstants.DELIVERY);
        if (deliveryObject != null) {
            mShippingValue = deliveryObject.optDouble(RestConstants.AMOUNT);
        }
        // Coupon
        JSONObject couponObject = cartEntity.optJSONObject(RestConstants.COUPON);
        if (couponObject != null) {
            mCouponCode = couponObject.optString(RestConstants.CODE);
            mCouponDiscount = couponObject.optDouble(RestConstants.VALUE);
        }
        // Costs
        mSumCostsValue = cartEntity.optDouble(RestConstants.SUM_COSTS_VALUE);
        // Extra costs
        mExtraCosts = cartEntity.optDouble(RestConstants.EXTRA_COSTS);
        // Get cart count
        mCartCount = cartEntity.getInt(RestConstants.TOTAL_PRODUCTS);
        JSONArray cartArray = cartEntity.getJSONArray(RestConstants.PRODUCTS);
        mCartItems = new ArrayList<>();
        for (int i = 0; i < cartArray.length(); i++) {
            JSONObject cartObject = cartArray.getJSONObject(i);
            PurchaseCartItem item = new PurchaseCartItem();
            item.initialize(cartObject);
            mCartItems.add(item);
        }
        // Price rules
        JSONArray priceRules = cartEntity.optJSONArray(RestConstants.PRICE_RULES);
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
        JSONObject jsonShip = cartEntity.optJSONObject(RestConstants.SHIPPING_METHOD);
        if (jsonShip != null) {
            mShippingMethod = jsonShip.optString(RestConstants.METHOD);
        }
        // Get payment method
        JSONObject jsonPay = cartEntity.optJSONObject(RestConstants.PAYMENT_METHOD);
        if (jsonPay != null) {
            mPaymentMethod = jsonPay.optString(RestConstants.LABEL);
        }
        // Get billing address
        JSONObject jsonBilAddress = cartEntity.optJSONObject(RestConstants.JSON_ORDER_BIL_ADDRESS_TAG);
        if (jsonBilAddress != null) {
            mBillingAddress = new Address();
            mBillingAddress.initialize(jsonBilAddress);
        }
        // Get shipping address
        JSONObject jsonShipAddress = cartEntity.optJSONObject(RestConstants.JSON_ORDER_SHIP_ADDRESS_TAG);
        if (jsonShipAddress != null) {
            mShippingAddress = new Address();
            mShippingAddress.initialize(jsonShipAddress);
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
    public RequiredJson getRequiredJson() {
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

    public double getCartValueEuroConverted() {
        return mTotalConverted;
    }

    public double getPriceForTracking() {
        return mTotalConverted;
    }

    public double getSubTotal() {
        return mSubTotal;
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
        return mVatValue > 0 && mVatValue != Double.NaN;
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


    /**
     * @return A string with all attribute set Ids separated by ;
     */
    public String getAttributeSetIdList() {
        String attributeList = "";
        PurchaseCartItem item;
        if(mCartItems != null && mCartItems.size() > 0){

            for (int i = 0; i < mCartItems.size() ; i++) {
                if (TextUtils.isEmpty(attributeList)) {
                    attributeList = mCartItems.get(i).getAttributeSetId();
                } else {
                    attributeList = attributeList +";"+ mCartItems.get(i).getAttributeSetId();
                }
            }

        }
        return attributeList;
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
        dest.writeDouble(mShippingValue);
        dest.writeDouble(mExtraCosts);
        dest.writeDouble(mSumCostsValue);
        dest.writeDouble(mCouponDiscount);
        dest.writeString(mCouponCode);
        dest.writeValue(mPriceRules);
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

}