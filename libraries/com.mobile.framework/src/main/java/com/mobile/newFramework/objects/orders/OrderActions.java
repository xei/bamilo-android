package com.mobile.newFramework.objects.orders;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by msilva on 4/11/16.
 */
public class OrderActions implements Parcelable, IJSONSerializable {

    public final static String ACTION_ONLINE_RETURN = "online_return";
    public final static String ACTION_CALL_RETURN = "call_return";

    private String mReturnType;
    private String mTarget;
    private int mReturnableQuantity;

    public OrderActions(){}

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        mReturnType = jsonObject.getString(RestConstants.TYPE);
        mTarget = jsonObject.getString(RestConstants.TARGET);
        mReturnableQuantity = jsonObject.getInt(RestConstants.RETURNABLE_QUANTITY);

        return false;
    }

    public int getReturnableQuantity() {
        return mReturnableQuantity;
    }

    public String getReturnType() {
        return mReturnType;
    }

    public String getTarget() {
        return mTarget;
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
        dest.writeString(mReturnType);
        dest.writeString(mTarget);
        dest.writeInt(mReturnableQuantity);

    }

    protected OrderActions(Parcel in) {
        mReturnType = in.readString();
        mTarget = in.readString();
        mReturnableQuantity = in.readInt();
    }


    public static final Creator<OrderActions> CREATOR = new Creator<OrderActions>() {
        @Override
        public OrderActions createFromParcel(Parcel in) {
            return new OrderActions(in);
        }

        @Override
        public OrderActions[] newArray(int size) {
            return new OrderActions[size];
        }
    };
}
