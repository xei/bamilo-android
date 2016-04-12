package com.mobile.newFramework.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by msilva on 4/11/16.
 */
public class OrderReturn implements Parcelable, IJSONSerializable {

    private int mQuantity;
    private String mDate;

    public OrderReturn(){}

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        mQuantity = jsonObject.getInt(RestConstants.QUANTITY);
        mDate = jsonObject.getString(RestConstants.DATE);

        return false;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public String getDate() {
        return mDate;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mQuantity);
        dest.writeString(mDate);

    }

    protected OrderReturn(Parcel in) {
        mQuantity = in.readInt();
        mDate = in.readString();
    }


    public static final Parcelable.Creator<OrderReturn> CREATOR = new Parcelable.Creator<OrderReturn>() {
        @Override
        public OrderReturn createFromParcel(Parcel in) {
            return new OrderReturn(in);
        }

        @Override
        public OrderReturn[] newArray(int size) {
            return new OrderReturn[size];
        }
    };
}
