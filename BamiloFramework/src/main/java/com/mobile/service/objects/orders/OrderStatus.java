package com.mobile.service.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.addresses.Address;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to represent an order status.
 *
 * @author sergio pereira
 */
public class OrderStatus implements IJSONSerializable, Parcelable {

    public final static String TAG = OrderStatus.class.getSimpleName();

    private String mId;
    private double mTotal;
    private String mPaymentName;
    private String mPaymentType;
    private Address mBillingAddress;
    private Address mShippingAddress;
    private ArrayList<OrderTrackerItem> mItems;
    private String mDate;

    /**
     * OrderTracker empty constructor.
     */
    @SuppressWarnings("unused")
    public OrderStatus() {
        // ...
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get id
        mId = jsonObject.getString(RestConstants.ORDER_NUMBER);
        // Get date
        mDate = jsonObject.getString(RestConstants.CREATION_DATE);
        // Get total
        mTotal = jsonObject.getDouble(RestConstants.GRAND_TOTAL_2);
        // Get payment method
        JSONObject jsonPay = jsonObject.optJSONObject(RestConstants.PAYMENT);
        if (jsonPay != null) {
            mPaymentName = jsonPay.optString(RestConstants.LABEL);
            mPaymentType = jsonPay.optString(RestConstants.TYPE);
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
        // Get order items
        JSONArray items = jsonObject.optJSONArray(RestConstants.PRODUCTS);
        if (items != null && items.length() > 0) {
            mItems = new ArrayList<>();
            int size = items.length();
            for (int i = 0; i < size; i++) {
                OrderTrackerItem mOrderTrackerItem = new OrderTrackerItem();
                mOrderTrackerItem.initialize(items.getJSONObject(i));
                mItems.add(mOrderTrackerItem);
            }
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return new JSONObject();
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public String getId() {
        return this.mId;
    }

    public String getPaymentName() {
        return this.mPaymentName;
    }

    public String getPaymentType() {
        return this.mPaymentType;
    }

    public @Nullable ArrayList<OrderTrackerItem> getItems() {
        return this.mItems;
    }

    public int getTotalProducts() {
        return CollectionUtils.isNotEmpty(mItems) ? mItems.size() : 0;
    }

    public Address getBillingAddress() {
        return mBillingAddress;
    }

    public Address getShippingAddress() {
        return mShippingAddress;
    }

    public double getTotal() {
        return mTotal;
    }

    public String getDate() {
        return mDate;
    }

    /**
     * ########### Parcelable ###########
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
        dest.writeString(mId);
        dest.writeDouble(mTotal);
        dest.writeString(mDate);
        dest.writeString(mPaymentName);
        dest.writeString(mPaymentType);
        if (mItems == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mItems);
        }
        dest.writeValue(mBillingAddress);
        dest.writeValue(mShippingAddress);
    }

    /**
     * Parcel constructor
     */
    private OrderStatus(Parcel in) {
        mId = in.readString();
        mTotal = in.readDouble();
        mDate = in.readString();
        mPaymentName = in.readString();
        mPaymentType = in.readString();
        if (in.readByte() == 0x01) {
            mItems = new ArrayList<>();
            in.readList(mItems, OrderTrackerItem.class.getClassLoader());
        } else {
            mItems = null;
        }
        mBillingAddress = (Address) in.readValue(Address.class.getClassLoader());
        mShippingAddress = (Address) in.readValue(Address.class.getClassLoader());
    }

    /**
     * Create parcelable
     */
    public static final Creator<OrderStatus> CREATOR = new Creator<OrderStatus>() {
        public OrderStatus createFromParcel(Parcel in) {
            return new OrderStatus(in);
        }

        public OrderStatus[] newArray(int size) {
            return new OrderStatus[size];
        }
    };

}
