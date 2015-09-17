package com.mobile.newFramework.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 
 * @author Paulo Carvalho
 * 
 */
public class Order implements IJSONSerializable, Parcelable {

    public final static String TAG = Order.class.getSimpleName();

    private String mOrderNumber;

    private String mPayment;

    private String mDate;

    private String mOrderTotal;

    private ArrayList<OrderItem> mOrderProducts;

    private int totalOrdersHistory = -1;

    /**
     * Order empty constructor.
     */
    public Order() {
        mOrderNumber = "";
        mPayment = "";
        mDate = "";
        mOrderTotal = "";
        mOrderProducts = new ArrayList<>();
    }

    /**
     * Order empty constructor.
     *
     * @throws JSONException
     */
    public Order(JSONObject jsonObject) throws JSONException {
        mOrderNumber = "";
        mPayment = "";
        mDate = "";
        mOrderTotal = "";
        mOrderProducts = new ArrayList<>();
        initialize(jsonObject);
    }

    public String getmOrderNumber() {
        return mOrderNumber;
    }

//    public void setmOrderNumber(String mOrderNumber) {
//        this.mOrderNumber = mOrderNumber;
//    }

    public String getmPayment() {
        return mPayment;
    }

//    public void setmPayment(String mPayment) {
//        this.mPayment = mPayment;
//    }

    public String getmDate() {
        return mDate;
    }

//    public void setmDate(String mDate) {
//        this.mDate = mDate;
//    }

    public String getmOrderTotal() {
        return mOrderTotal;
    }
//
//    public void setmOrderTotal(String mOrderTotal) {
//        this.mOrderTotal = mOrderTotal;
//    }

    public ArrayList<OrderItem> getmOrderProducts() {
        return mOrderProducts;
    }
//
//    public void setmOrderProducts(ArrayList<OrderItem> mOrderProducts) {
//        this.mOrderProducts = mOrderProducts;
//    }

//    public int getTotalOrdersHistory() {
//        return totalOrdersHistory;
//    }

    public void setTotalOrdersHistory(int totalOrdersHistory) {
        this.totalOrdersHistory = totalOrdersHistory;
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

        try {

            mDate = jsonObject.optString(RestConstants.JSON_ORDER_DATE_TAG);
            mOrderNumber = jsonObject.optString(RestConstants.JSON_NUMBER_TAG);
            mOrderTotal = jsonObject.optString(RestConstants.JSON_ORDER_TOTAL_TAG);
            mPayment = jsonObject.optJSONObject(RestConstants.JSON_ORDER_PAYMENT_TAG).optString(RestConstants.JSON_TITLE_TAG);

            JSONArray productsArray = jsonObject.optJSONArray(RestConstants.PRODUCTS);
            if (null != productsArray && productsArray.length() > 0)
                for (int j = 0; j < productsArray.length(); j++) {

                    OrderItem product = new OrderItem(productsArray.optJSONObject(j));

                    mOrderProducts.add(product);
                }

        } catch (Exception e) {
            e.printStackTrace();
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

    protected Order(Parcel in) {
        mOrderNumber = in.readString();
        mPayment = in.readString();
        mDate = in.readString();
        mOrderTotal = in.readString();
        if (in.readByte() == 0x01) {
            mOrderProducts = new ArrayList<>();
            in.readList(mOrderProducts, OrderItem.class.getClassLoader());
        } else {
            mOrderProducts = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOrderNumber);
        dest.writeString(mPayment);
        dest.writeString(mDate);
        dest.writeString(mOrderTotal);
        if (mOrderProducts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mOrderProducts);
        }
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
