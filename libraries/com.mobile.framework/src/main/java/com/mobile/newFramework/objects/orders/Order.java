package com.mobile.newFramework.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Paulo Carvalho
 */
public class Order implements IJSONSerializable, Parcelable {

    public final static String TAG = Order.class.getSimpleName();

    private int mNumber;

    private String mDate;

    private double mTotal;


    /**
     * Order empty constructor.
     */
    public Order() {
        // ...
    }

    /**
     * Order empty constructor.
     *
     * @throws JSONException
     */
    public Order(JSONObject jsonObject) throws JSONException {
        initialize(jsonObject);
    }

    public int getNumber() {
        return mNumber;
    }

    public String getDate() {
        return mDate;
    }


    public double getTotal() {
        return mTotal;
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        mNumber = jsonObject.getInt(RestConstants.JSON_NUMBER_TAG);
        mDate = jsonObject.optString(RestConstants.JSON_ORDER_DATE_TAG);
        mTotal = jsonObject.optDouble(RestConstants.JSON_ORDER_TOTAL_TAG);
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

    protected Order(Parcel in) {
        mNumber = in.readInt();
        mDate = in.readString();
        mTotal = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mNumber);
        dest.writeString(mDate);
        dest.writeDouble(mTotal);
    }

    @SuppressWarnings("unused")
    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

}
