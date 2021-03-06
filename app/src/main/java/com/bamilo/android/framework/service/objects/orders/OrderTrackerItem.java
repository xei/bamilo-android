package com.bamilo.android.framework.service.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.objects.product.pojo.ProductRegular;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents an Order Tracked Item
 * 
 * @author manuelsilva
 * 
 */
public class OrderTrackerItem extends ProductRegular {

	public final static String TAG = OrderTrackerItem.class.getSimpleName();

    private String delivery;
    private String size;
    private String quantity;
    private String status;
    private String updateDate;
    private boolean isCheckedForAction;
    private ArrayList<OrderReturn> mOrderReturns;
    private ArrayList<OrderActions> mOrderActions;
    private boolean isEligibleToReturn = false;

    /**
     * OrderTrackerItem empty constructor.
     */
    public OrderTrackerItem() {
        // ...
    }

    @Nullable
    public String getQuantity(){
    	return this.quantity;
    }

    @Nullable
    public String getSize(){
    	return this.size;
    }

    @Nullable
    public String getStatus(){
    	return this.status;
    }

    @Nullable
    public String getDelivery() {
        return delivery;
    }

    @Nullable
    public String getUpdateDate() {
        return updateDate;
    }

    @Nullable
    public ArrayList<OrderReturn> getOrderReturns() {
        return mOrderReturns;
    }

    @Nullable
    public ArrayList<OrderActions> getOrderActions() {
        return mOrderActions;
    }

    @Nullable
    public OrderActions getDefaultOrderAction() {
        return CollectionUtils.isNotEmpty(mOrderActions) ? mOrderActions.get(IntConstants.DEFAULT_POSITION) : null;
    }

    public boolean isEligibleToReturn(){
        return isEligibleToReturn;
    }

    public boolean isCheckedForAction() {
        return isCheckedForAction;
    }

    /**
     * Sets whether this item is selected to return or not.
     */
    public void setCheckedForAction(boolean checkedForAction) {
        isCheckedForAction = checkedForAction;
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
        size = jsonObject.optString(RestConstants.SIZE);
		quantity = jsonObject.getString(RestConstants.QUANTITY);
        JSONObject statusObject = jsonObject.getJSONObject(RestConstants.STATUS);
        status = statusObject.optString(RestConstants.LABEL);
        updateDate = statusObject.optString(RestConstants.UPDATE_AT);

        JSONArray itemReturns = jsonObject.optJSONArray(RestConstants.RETURNS);
        if(CollectionUtils.isNotEmpty(itemReturns)){
            mOrderReturns = new ArrayList<>();
            for (int i = 0; i < itemReturns.length(); i++) {
                OrderReturn orderReturn = new OrderReturn();
                try {
                    orderReturn.initialize(itemReturns.getJSONObject(i));
                    mOrderReturns.add(orderReturn);
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }

        JSONArray itemActions = jsonObject.optJSONArray(RestConstants.ACTIONS);
        if(CollectionUtils.isNotEmpty(itemActions)){
            mOrderActions = new ArrayList<>();
            for (int i = 0; i < itemActions.length(); i++) {
                OrderActions orderActions = new OrderActions();
                try {
                    orderActions.initialize(itemActions.getJSONObject(i));
                    if(orderActions.getReturnableQuantity() > 0){
                        isEligibleToReturn = true;
                    }
                    mOrderActions.add(orderActions);
                } catch (JSONException e){
                    e.printStackTrace();
                }
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.QUANTITY, quantity);
            jsonObject.put(RestConstants.STATUS, status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
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
        dest.writeString(size);
        dest.writeString(quantity);
        dest.writeString(status);
        dest.writeString(updateDate);
        dest.writeByte((byte) (isCheckedForAction ? 0x01 : 0x00));
        if (mOrderReturns == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mOrderReturns);
        }
        if (mOrderActions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mOrderActions);
        }
        dest.writeByte((byte) (isEligibleToReturn ? 0x01 : 0x00));
    }

    protected OrderTrackerItem(Parcel in) {
        super(in);
        delivery = in.readString();
        size = in.readString();
        quantity = in.readString();
        status = in.readString();
        updateDate = in.readString();
        isCheckedForAction = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            mOrderReturns = new ArrayList<>();
            in.readList(mOrderReturns, OrderReturn.class.getClassLoader());
        } else {
            mOrderReturns = null;
        }
        if (in.readByte() == 0x01) {
            mOrderActions = new ArrayList<>();
            in.readList(mOrderActions, OrderActions.class.getClassLoader());
        } else {
            mOrderActions = null;
        }
        isEligibleToReturn = in.readByte() != 0x00;
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
