package com.mobile.newFramework.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents an Order Tracked Item
 * 
 * @author manuelsilva
 * 
 */
public class OrderTrackerItem extends ProductRegular {

	public final static String TAG = OrderTrackerItem.class.getSimpleName();

    private String delivery;
    private String quantity;
    private String status;
    private String updateDate;


    /**
     * OrderTrackerItem empty constructor.
     */
    public OrderTrackerItem() {
        // ...
    }

    public String getQuantity(){
    	return this.quantity;
    }

    public String getStatus(){
    	return this.status;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    /*
         * (non-Javadoc)
         *
         * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
         */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        delivery = jsonObject.getString(RestConstants.DELIVERY);
		quantity = jsonObject.getString(RestConstants.JSON_QUANTITY_TAG);
        JSONObject statusObject = jsonObject.getJSONObject(RestConstants.JSON_ORDER_STATUS_TAG);
        status = statusObject.optString(RestConstants.LABEL);
        updateDate = statusObject.optString(RestConstants.UPDATE_AT);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_QUANTITY_TAG, quantity);
            jsonObject.put(RestConstants.JSON_ORDER_STATUS_TAG, status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    /**
     * ########### Parcelable ###########
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(delivery);
        dest.writeString(quantity);
        dest.writeString(status);
        dest.writeString(updateDate);
    }

    protected OrderTrackerItem(Parcel in) {
        super(in);
        delivery = in.readString();
        quantity = in.readString();
        status = in.readString();
        updateDate = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OrderTrackerItem> CREATOR = new Parcelable.Creator<OrderTrackerItem>() {
        @Override
        public OrderTrackerItem createFromParcel(Parcel in) {
            return new OrderTrackerItem(in);
        }

        @Override
        public OrderTrackerItem[] newArray(int size) {
            return new OrderTrackerItem[size];
        }
    };

}
