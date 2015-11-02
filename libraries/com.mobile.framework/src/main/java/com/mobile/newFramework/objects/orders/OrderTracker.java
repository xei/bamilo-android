package com.mobile.newFramework.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents an Order Tracked
 *
 * @author manuelsilva
 * @modified sergio pereira
 */
public class OrderTracker implements IJSONSerializable, Parcelable {

    public final static String TAG = OrderTracker.class.getSimpleName();

    private String mId;
    private String mDate;
    private String mPaymentMethod;
    private String mLastUpdate;
    private ArrayList<OrderTrackerItem> mOrderTrackerItems;


    /**
     * OrderTracker empty constructor.
     */
    @SuppressWarnings("unused")
    public OrderTracker() {
        // ...
    }

    public String getId() {
        return this.mId;
    }

    public String getDate() {
        return this.mDate;
    }

    public String getPaymentMethod() {
        return this.mPaymentMethod;
    }

    public @Nullable ArrayList<OrderTrackerItem> getOrderTrackerItems() {
        return this.mOrderTrackerItems;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        mId = jsonObject.optString(RestConstants.ORDER_NUMBER);
        mDate = jsonObject.optString(RestConstants.JSON_ORDER_CREATION_DATE_TAG);
        mPaymentMethod = jsonObject.optString(RestConstants.PAYMENT_METHOD);
        mLastUpdate = jsonObject.optString(RestConstants.JSON_ORDER_LAST_UPDATE_TAG);
        // Get items
        JSONArray items = jsonObject.optJSONArray(RestConstants.PRODUCTS);
        if (items != null && items.length() > 0) {
            mOrderTrackerItems = new ArrayList<>();
            int size = items.length();
            for (int i = 0; i < size; i++) {
                OrderTrackerItem mOrderTrackerItem = new OrderTrackerItem();
                mOrderTrackerItem.initialize(items.getJSONObject(i));
                mOrderTrackerItems.add(mOrderTrackerItem);
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
        return new JSONObject();
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
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
        dest.writeString(mDate);
        dest.writeString(mPaymentMethod);
        dest.writeString(mLastUpdate);
        dest.writeList(mOrderTrackerItems);
    }

    /**
     * Parcel constructor
     */
    private OrderTracker(Parcel in) {
        mId = in.readString();
        mDate = in.readString();
        mPaymentMethod = in.readString();
        mLastUpdate = in.readString();
        mOrderTrackerItems = new ArrayList<>();
        in.readList(mOrderTrackerItems, OrderTrackerItem.class.getClassLoader());
    }

    /**
     * Create parcelable
     */
    public static final Creator<OrderTracker> CREATOR = new Creator<OrderTracker>() {
        public OrderTracker createFromParcel(Parcel in) {
            return new OrderTracker(in);
        }

        public OrderTracker[] newArray(int size) {
            return new OrderTracker[size];
        }
    };

}
