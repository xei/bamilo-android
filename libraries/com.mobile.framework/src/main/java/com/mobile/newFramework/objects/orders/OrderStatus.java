package com.mobile.newFramework.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;

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
    private String mPaymentMethod;
    private Address mBillingAddress;
    private Address mShippingAddress;
    private ArrayList<OrderTrackerItem> mItems;

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
        // Get total
        mTotal = jsonObject.getDouble("grand_total"); // RestConstants.TOTAL
        // Get payment method
        JSONObject jsonPay = jsonObject.optJSONObject(RestConstants.PAYMENT_METHOD);
        if (jsonPay != null) {
            mPaymentMethod = jsonPay.optString(RestConstants.LABEL);
        }
        // Get billing address
        JSONObject jsonBilAddress = jsonObject.optJSONObject(RestConstants.JSON_ORDER_BIL_ADDRESS_TAG);
        if (jsonBilAddress != null) {
            mBillingAddress = new Address();
            mBillingAddress.initialize(jsonBilAddress);
        }
        // Get shipping address
        JSONObject jsonShipAddress = jsonObject.optJSONObject(RestConstants.JSON_ORDER_SHIP_ADDRESS_TAG);
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
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }


    public String getId() {
        return this.mId;
    }

    public String getPaymentMethod() {
        return this.mPaymentMethod;
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
        dest.writeString(mPaymentMethod);
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
        mPaymentMethod = in.readString();
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
