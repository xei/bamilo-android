package com.bamilo.android.framework.service.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Paulo Carvalho
 *
 */
public class OrderItem implements IJSONSerializable, Parcelable {

	public final static String TAG = OrderItem.class.getSimpleName();

	private int mProductQuantity;

	private double mProductUnitPrice;

	private String mProductSku;

	private String mProductSimpleSku;

	private double mProductTotal;

	private String mProductName;

	/**
	 * OrderItem empty constructor.
	 */
    @SuppressWarnings("unused")
	public OrderItem() {
	    mProductQuantity = 0;
	    mProductUnitPrice = 0.0d;
	    mProductSku = "";
	    mProductSimpleSku = "";
	    mProductTotal = 0.0;
	    mProductName = "";
	}

	/**
	 * OrderItem empty constructor.
	 *
	 * @throws JSONException
	 */
	public OrderItem(JSONObject jsonObject) throws JSONException {
	    mProductQuantity = 0;
	    mProductUnitPrice = 0.0d;
	    mProductSku = "";
	    mProductSimpleSku = "";
	    mProductTotal = 0.0;
	    mProductName = "";
		initialize(jsonObject);
	}


	public int getmProductQuantity() {
        return mProductQuantity;
    }

//    public void setmProductQuantity(int mProductQuantity) {
//        this.mProductQuantity = mProductQuantity;
//    }
//
//    public double getmProductUnitPrice() {
//        return mProductUnitPrice;
//    }
//
//    public void setmProductUnitPrice(double mProductUnitPrice) {
//        this.mProductUnitPrice = mProductUnitPrice;
//    }

//    public String getmProductSku() {
//        return mProductSku;
//    }
//
//    public void setmProductSku(String mProductSku) {
//        this.mProductSku = mProductSku;
//    }
//
//    public String getmProductSimpleSku() {
//        return mProductSimpleSku;
//    }
//
//    public void setmProductSimpleSku(String mProductSimpleSku) {
//        this.mProductSimpleSku = mProductSimpleSku;
//    }

    public double getmProductTotal() {
        return mProductTotal;
    }

    public String getmProductTotalString() {
        return String.valueOf(mProductTotal);
    }

//    public void setmProductTotal(double mProductTotal) {
//        this.mProductTotal = mProductTotal;
//    }

    public String getmProductName() {
        return mProductName;
    }

//    public void setmProductName(String mProductName) {
//        this.mProductName = mProductName;
//    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
	 * )
	 */
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {

	    mProductQuantity = jsonObject.optInt(RestConstants.QUANTITY);
	    mProductUnitPrice = jsonObject.optDouble(RestConstants.UNIT_PRICE);
	    mProductSku = jsonObject.optString(RestConstants.CONFIG_SKU);
	    mProductSimpleSku = jsonObject.optString(RestConstants.SKU);
	    mProductTotal = jsonObject.optDouble(RestConstants.TOTAL);
	    mProductName = jsonObject.optString(RestConstants.NAME);

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
        return RequiredJson.NONE;
    }

    protected OrderItem(Parcel in) {
        mProductQuantity = in.readInt();
        mProductUnitPrice = in.readDouble();
        mProductSku = in.readString();
        mProductSimpleSku = in.readString();
        mProductTotal = in.readDouble();
        mProductName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mProductQuantity);
        dest.writeDouble(mProductUnitPrice);
        dest.writeString(mProductSku);
        dest.writeString(mProductSimpleSku);
        dest.writeDouble(mProductTotal);
        dest.writeString(mProductName);
    }

    @SuppressWarnings("unused")
    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

}
